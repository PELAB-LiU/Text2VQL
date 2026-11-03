#Import this file as: from diagutil import *
import sqlite3
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import os
import sys
import numpy as np
import re
import glob
import json
from collections.abc import Iterable
from collections import ChainMap
from functools import lru_cache

################
# Plot helpers #
################
COLUMN_WIDTH_IN = 2.99213 #in, (76mm)
TEXT_WIDTH_IN = 6.29921 #in, (160 mm)

# Settings for matplotlib
PTL_BASE = {
    "axes.titlesize": 10,
    "axes.labelsize": 8,
    "xtick.labelsize": 6,
    "ytick.labelsize": 6,
    "figure.constrained_layout.use": True,
    'mathtext.default':  'regular'
}

PLT_COL = ChainMap({
    "figure.figsize": (COLUMN_WIDTH_IN,  (6/10)*COLUMN_WIDTH_IN)
}, PTL_BASE)

PLT_PG = ChainMap({
    "figure.figsize": (TEXT_WIDTH_IN,  (6/10)*TEXT_WIDTH_IN)
}, PTL_BASE)

# Settings for seaborn
SNS_BASE = {
    #"context": "notebook",
    "style": "whitegrid",
    "palette": "muted",
    "font": "DejaVu Sans",
    "font_scale": 1.0,
}
COLW = ChainMap({
},{"rc": PLT_COL}, SNS_BASE)

TEXTW = ChainMap({
}, {"rc": PLT_PG}, SNS_BASE)

def sns_col():
    sns.set_theme(**COLW)
def sns_page():
    sns.set_theme(**TEXTW)

def toSize(w=1,h=1, scale=1):
    figwidth, _ = plt.rcParams["figure.figsize"]
    figwidth *= scale   
    figheight = (h/w)*figwidth
    return {"figsize": (figwidth,figheight)}

def oneCol(w=1,h=1, scale=1, pagewidth=False):
    figwidth = COLUMN_WIDTH_IN
    if pagewidth:
        figwidth = TEXT_WIDTH_IN #in, (160 mm)
    figwidth *= scale   
    #w, h = aspect_ratio
    figheight = (h/w)*figwidth
    return (figwidth,figheight)
#########################
# Project file settings #
#########################
TEXT2VQL_ROOT = os.path.abspath(os.path.dirname(__file__))

while True:
    if os.path.basename(TEXT2VQL_ROOT) == "Text2VQL":
        break
    new = os.path.dirname(TEXT2VQL_ROOT)
    if new == TEXT2VQL_ROOT:
        raise FileNotFoundError("Could not find a parent directory named 'Text2VQL'.")
    TEXT2VQL_ROOT = new

#############
# Constants #
#############
EVAL_TYPE_MAPPING = {
    "id": "Int64",
    "llm": str,
    "finetune": "boolean",
    "caseid": "Int64",
    "domain": str,
    "lang": str,
    "shotid": "Int64",
    "query": str,
    "syntax": "boolean",
    "diagnostics": str,
    "semantics": "boolean",
    "idicator": str
}
LLM_NAME_MAPPING = {
    'Qwen/Qwen2.5-Coder-1.5B': 'Qwen2.5 Coder 1.5B', 
    'Qwen/Qwen2.5-Coder-7B': 'Qwen2.5 Coder 7B',
    'Qwen/Qwen3-1.7B-Base': 'Qwen3 1.7B', 
    'Qwen/Qwen3-8B-Base': 'Qwen3 8B',
    'codellama/CodeLlama-7b-hf': 'CodeLlama 7B',
    'deepseek-ai/deepseek-coder-1.3b-base': 'DeepSeek Coder 1.3B', 
    'deepseek-ai/deepseek-coder-7b-base-v1.5': 'DeepSeek Coder 7B',
    'ChatGPT': 'GPT-5'
}
LLM_ORDER = ['GPT-5','DeepSeek Coder 1.3B','Qwen2.5 Coder 1.5B','Qwen3 1.7B','CodeLlama 7B','DeepSeek Coder 7B','Qwen2.5 Coder 7B','Qwen3 8B']
CFG_CUFFIX = ["\\ic{}", "\\ft{}", "$\\Delta$"]


#####################
# Utility functions #
#####################
def classify(row):
    # Use pd.isna() to handle missing values
    if pd.isna(row["syntax"]) or not row["syntax"]:
        return "Syntax Error"
    elif pd.isna(row["semantics"]) or not row["semantics"]:
        return "Semantic Error"
    else:
        return "Correct"

def remapDictionary(dictionary, function):
    return {key: function(value) for key, value in dictionary.items()}
    
def reorderColumns(df: pd.DataFrame, order: list=LLM_ORDER) -> pd.DataFrame:
    order = list(order)
    # Columns not in the provided order (preserve original order)
    front = [c for c in df.columns if c not in order]
    # Columns in the provided order that actually exist in the DataFrame
    back = [c for c in order if c in df.columns]
    return df[front + back]
    
############################
# Load generated responses #
############################

# The database path is fixed in practice, so one cached load is enough.
@lru_cache(maxsize=1)
def _loadAllPrompts(path):
    with sqlite3.connect(os.path.join(TEXT2VQL_ROOT,path)) as conn:
        prompts = pd.read_sql_query("SELECT * FROM evaluation", conn)
    
        for col, dtype in EVAL_TYPE_MAPPING.items():
            if dtype == "Int64":
                prompts[col] = pd.to_numeric(prompts[col], errors="coerce").astype("Int64")
            elif dtype == "boolean":
                # Coerce non-boolean to NaN
                prompts[col] = prompts[col].map(lambda x: bool(x) if x in [0, 1, True, False] else pd.NA).astype("boolean")
            elif dtype == str:
                prompts[col] = prompts[col].astype(str, errors="ignore")  # keep invalid as string

        prompts["category"] = prompts.apply(classify, axis=1)
        
    prompts['llm'] = prompts['llm'].map(lambda x : LLM_NAME_MAPPING[x])
    return prompts

def filterShots(prompts, k=None):
    """
    Keep only the first k shots (shotid is 0-based), so that "solved" means pass@k.
    k=None keeps all shots. Always returns a copy, so cached data is never modified.
    """
    if k is None:
        return prompts.copy()
    return prompts[prompts["shotid"] < k].copy()

def loadPrompts(k=None, path='finetuning/evaluation.db'):
    return filterShots(_loadAllPrompts(path), k)

#####################
# Load ground truth #
#####################
@lru_cache(maxsize=1)
def _loadTruth(path):
    df = pd.read_csv(
            os.path.join(TEXT2VQL_ROOT,path),
            encoding='unicode_escape',
            converters={
                "construct_vql": lambda x: {s.strip() for s in str(x).split(",") if s.strip()}
            }
    )

    # Convert 'id' to int and drop rows where it's missing or invalid
    df["id"] = pd.to_numeric(df["id"], errors="coerce")
    df = df.dropna(subset=["id"])
    df["id"] = df["id"].astype(int)
    return df

def loadTruth(path='dataset_construction/test_metamodel/truth.csv'):
    return _loadTruth(path).copy()

####################################
# Make improvement data            #
# columns: LLMs                    #
# rows: Test case ids              #
# cell: dictionary data as (ic,ft) #
####################################

# Helper: build dictionary or None
def build_counts(df_part):
    if df_part.empty:
        return None
    counts = df_part.groupby("category")["count"].sum().to_dict()
    # Ensure all required keys exist
    for cat in ["Correct", "Syntax Error", "Semantic Error"]:
        counts.setdefault(cat, 0)
    return counts
        
def fillContent(df_lang, llm, caseid):
    """
    Builds the cell string for one llm–caseid pair.
    Shows counts for all categories for In context and Finetuned versions.
    Handles missing Finetuned or In context data gracefully.
    """
    # Filter for this llm and caseid
    sub = df_lang[(df_lang["llm"] == llm) & (df_lang["caseid"] == caseid)]

    # Split by finetune type
    in_context = sub[sub["finetune"] == "In context"]
    finetuned = sub[sub["finetune"] == "Finetuned"]

    in_counts = build_counts(in_context)
    ft_counts = build_counts(finetuned)

    # Return combined string
    return (in_counts, ft_counts)

def move_to_front(arr, values):
    """
    Rearrange the arr array such that the resulting lists starts with elements that are present in both arr and values, and ends with elements only present in arr.
    """
    return [x for x in arr if x in values] + [x for x in arr if not x in values]

def make_tables(df):
    """
    Returns a dictionary of pivoted DataFrames, one per language,
    with cells filled using fillContent().
    """
    tables = {}
    for lang, df_lang in df.groupby("lang"):
        llms = sorted(df_lang["llm"].unique())

        llms = move_to_front(llms, ['GPT-5'])
        
        caseids = sorted(df_lang["caseid"].unique())

        # Build table manually
        data = []
        for llm in llms:
            row = {caseid: fillContent(df_lang, llm, caseid) for caseid in caseids}
            row["llm"] = llm
            data.append(row)

        # Convert to DataFrame
        table = pd.DataFrame(data).set_index("llm")[caseids]
        tables[lang] = table.transpose()
    return tables
# One entry per k: None (all shots) and 1..5 shots.
@lru_cache(maxsize=6)
def _makeCountTable(k):
    results = (
        loadPrompts(k=k).groupby(["llm", "finetune", "caseid", "category", "lang"])
        .size()
        .reset_index(name="count")
    )
    results["finetune"] = results["finetune"].map({True: "Finetuned", False: "In context"})
    #results["x_label"] = results["caseid"].astype(str) + " - " + results["finetune"]
    return make_tables(results)

def makeCountTable(k=None):
    return {lang: df.copy() for lang, df in _makeCountTable(k).items()}

def inplaceRemapTable(df, mapper, exclude=['llm']):
    for col in df.columns:
        if not col in exclude:
            df[col] = df[col].apply(mapper)

############################
# Make grouped improvemnts #
############################


def aggregateResultCount(column):
    accu_ic = 0
    accu_ft = 0;
    for ic, ft in column:
        if ic['Correct']>=1:
            accu_ic+=1
        if ft and ft['Correct']>=1:
            accu_ft+=1
    delta = (accu_ft-accu_ic)
    return accu_ic, accu_ft , f"+{delta}" if delta>=0 else f"{delta}"
    
def label(df):
    truth = loadTruth()
    df = df.merge(truth[['id','domain','source']], left_index=True, right_on="id", how="left")
    df["group"] = df["domain"]  # default
    df.loc[(df["domain"] == "railway") & (df["source"] == "text2vql"), "group"] = "text2vql"
    df.loc[(df["domain"] == "railway") & (df["source"] != "text2vql"), "group"] = "railway"
    df = df.groupby('group')[LLM_ORDER].aggregate(aggregateResultCount).reset_index()
    return df

def explode(df, group_col="group", row_suffixes=CFG_CUFFIX, aggregate_name=None):
    expanded_df = df.set_index(group_col).apply(pd.Series.explode).reset_index()
    new_labels = []
    for group in df[group_col]:
        for suffix in row_suffixes:
            new_labels.append(f"{group} {suffix}")
    expanded_df['group'] = new_labels
    
    if aggregate_name is None:
        return expanded_df
    
    agg_rows = []
    for suffix in row_suffixes:
        mask = expanded_df["group"].str.endswith(f" {suffix}")
        subset = expanded_df.loc[mask].drop(columns=["group"])

        row = {}

        for col in subset.columns:
            s = subset[col]

            # remember original type
            is_string = s.dtype == object

            # numeric processing
            nums = pd.to_numeric(s, errors="coerce").fillna(0)
            total = nums.sum()

            if suffix == "$\\Delta$":
                row[col] = f"{int(total):+d}" 
            else:
                row[col] = total

        row["group"] = f"{aggregate_name} {suffix}"
        agg_rows.append(row)

    expanded_df = pd.concat(
        [expanded_df, pd.DataFrame(agg_rows)],
        ignore_index=True
    )
    return expanded_df

def makeGroupCountTable(aggregate_name=None, k=None):
    dfs = makeCountTable(k=k)
    return {
        'vql': reorderColumns(explode(label(dfs['vql']), aggregate_name=aggregate_name)),
        'ocl': reorderColumns(explode(label(dfs['ocl']), aggregate_name=aggregate_name)),
        'java': reorderColumns(explode(label(dfs['java']), aggregate_name=aggregate_name))
    }
    
######################
# Latex helper stuff #
######################
def replaceToText(text, locator, content):
    return re.sub(locator, content, text, flags=re.MULTILINE)
def insertHline(text, locator):
    return replaceToText(text, locator, lambda match : f"\\hline\n"+match.group(1))
def insertHdashline(text, locator):
    return replaceToText(text, locator, lambda match : f"\\hdashline\n"+match.group(1))
    
def replaceFirstCell(content, lang, hline=None, hdashline=None):
    match_tab = re.search(r'\\begin{tabular}.*?\\end{tabular}', content, re.DOTALL)
    if match_tab:
        text = re.sub(r'(^\\toprule\n)(^ )(&)', lambda match : match.group(1)+f"\\rotatebox{{90}}{{\\textbf{{{lang}}}}} "+match.group(3), match_tab.group(), flags=re.MULTILINE )
        if hline:
            text = insertHline(text, hline)
        if hdashline:
            text = insertHdashline(text, hdashline)
        return text
    else:
        return content

def dictionaryToTabluar(tables: dict, group_col="group", group_order=None, drop_columns=None, excel=None):
    # Determine base group order
    if group_order is None:
        first_key = next(iter(tables))
        base_groups = tables[first_key][group_col]
    else:
        base_groups = pd.Series(group_order)

    aligned_tables = []
    column_tuples = []
    lang_offsets = []
    offset = 2
    for lang, df in tables.items():
        df = df.copy()
        if drop_columns is not None:
            df = df.drop(drop_columns, axis=1)
        df = df.set_index(group_col).apply(pd.Series.explode).reset_index()
        df = df.set_index(group_col).loc[base_groups]
        
        # Build MultiIndex columns: (Language, LLM)
        for col in df.columns:
            rotated_col = f"\\rotatebox{{90}}{{{col}}}"
            column_tuples.append((lang.upper(), rotated_col))
        lang_offsets.append((offset, offset+len(df.columns)-1))
        offset += len(df.columns)
        aligned_tables.append(df)

    # ---- 2. Concatenate horizontally with MultiIndex columns ----
    merged = pd.concat(aligned_tables, axis=1)
    merged.columns = pd.MultiIndex.from_tuples(
        column_tuples, names=["Language", "LLM"]
    )

    # ---- 3. Reinsert group column as first column ----
    merged.insert(0, ("", group_col), base_groups.values)

    # ---- 4. Export to LaTeX with multicolumn headers ----
    latex = merged.to_latex(
        index=False,
        multicolumn=True,
        multicolumn_format="c",
        column_format="l|"+'|'.join(["c"*(1+e-s) for s, e in lang_offsets]),
        escape=False
    )
    if excel is not None:
        merged.to_excel(excel)
    
    return latex, lang_offsets
################################
# Dataset Construction Helpers #
################################

def datasetResponse(root='dataset_construction/chatgpt'):
    all_records = []
    for filepath in glob.glob(os.path.join(os.path.join(TEXT2VQL_ROOT,root), "response_2025-10-12T*.jsonl")):
        with open(filepath, "r", encoding="utf-8") as f:
            for line in f:
                all_records.append(json.loads(line))
    return all_records

def countTokens(lines=None):
    inputTokens = 0
    cachedTokens = 0
    outputTokens = 0
    
    if not lines:
        lines = datasetResponse()
        
    for data in lines:
        usage = data['response']['body']['usage']
        newInputTokens = usage['input_tokens']
        inputTokens += newInputTokens
        newCachedTokens = usage['input_tokens_details']['cached_tokens']
        cachedTokens += newCachedTokens
        newOutputTokens = usage['output_tokens']
        outputTokens += newOutputTokens
        
    return {'input': inputTokens, 'cached':cachedTokens, 'output': outputTokens}

def countTokensPerLanguage():
    inputTokens = 0
    cachedTokens = 0
    outputTokens = 0

    alldata = datasetResponse()
    vql = [item for item in alldata if '_vql_' in item['custom_id']]
    ocl = [item for item in alldata if '_ocl_' in item['custom_id']]
    java = [item for item in alldata if '_java_' in item['custom_id']]
    
    return {'vql': countTokens(vql), 'ocl': countTokens(ocl), 'java': countTokens(java)}

def toUSD(tokencount, inCost=1.25, cacheCost=0.125, outCost=10, discount=1.0):
    out = {}
    for key in tokencount.keys():
        if key=='input':
            out[key] = discount * (tokencount[key]*inCost/1_000_000)
        if key=='cached':
            out[key] = discount * (tokencount[key]*cacheCost/1_000_000)
        if key=='output':
            out[key] = discount * (tokencount[key]*outCost/1_000_000)
        if key in ['vql','ocl','java']:
            out[key] = toUSD(tokencount[key], inCost, cacheCost, outCost, discount)
    if 'input' in out.keys() and 'output' in out.keys():
        out['total'] = out['input'] + out['output']
    return out
################
# Export stuff #
################
__all__ = [name for name in globals() if not name.startswith('_')]

