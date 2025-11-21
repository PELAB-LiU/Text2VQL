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

############################
# Load generated responses #
############################
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
def loadPrompts(path='finetuning/evaluation.db'):
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
PROMPTS = loadPrompts()

#####################
# Load ground truth #
#####################
def loadTruth(path='dataset_construction/test_metamodel/truth.csv'):
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
TRUTH = loadTruth()

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
def makeCountTable():
    results = (
        PROMPTS.copy().groupby(["llm", "finetune", "caseid", "category", "lang"])
        .size()
        .reset_index(name="count")
    )
    results["finetune"] = results["finetune"].map({True: "Finetuned", False: "In context"})
    #results["x_label"] = results["caseid"].astype(str) + " - " + results["finetune"]
    return make_tables(results)

def inplaceRemapTable(df, mapper, exclude=['llm']):
    for col in df.columns:
        if not col in exclude:
            df[col] = df[col].apply(mapper)

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

