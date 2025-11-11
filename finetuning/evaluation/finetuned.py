from evaluation.external import TEXT2VQL_ROOT as ROOT #Load text2vql project to system path.

import os
import re
import csv
import random 
import json
import time
import pickle

import argparse
from collections import defaultdict
import sqlite3
import pandas as pd
import torch
from datetime import datetime
from peft import PeftModel
from tqdm import tqdm
from transformers import AutoModelForCausalLM, AutoTokenizer

from openai import OpenAI

# Must import evaluation.external before in order to ensure proper operation
# evaluation.external finds the text2vql root folder and adds the text2vql python module to the system module path.
from text2vql.util.metamodel import MetaModel
from text2vql.datasetgen.batchpull import Puller

from evaluation.templates import COMPLETION_QUERY, QUERY
from text2vql.seed.seed_yakindu import SEED
from text2vql.seed.util import AttrDict

from transformers.trainer_utils import set_seed

set_seed(123)

def findCheckpoint(base: str) -> str | None:
    pattern = re.compile(r"^checkpoint-(\d+)$")
    max_num = -1
    checkpoint = None
    try:
        for entry in os.listdir(base):
            path = os.path.join(base, entry)
            if os.path.isdir(path):
                match = pattern.match(entry)
                if match:
                    num = int(match.group(1))
                    if num > max_num:
                        max_num = num
                        checkpoint = path
    except FileNotFoundError:
        return None
    return checkpoint

class LLM:
    def __init__(self, basemodel, checkpoint=None, verbose=False, description='nl', headername='header_vql', db='evaluation.db', lang='vql'):
        self.basename = basemodel
        self.finetune = False
        self.lang = lang
        self.db = db
        
        if basemodel is not None:
            self.model = AutoModelForCausalLM.from_pretrained(basemodel,
                                                 trust_remote_code=True,
                                                 dtype=torch.float16,
                                                 device_map="auto")
        if checkpoint is not None:
            self.model = PeftModel.from_pretrained(self.model, findCheckpoint(checkpoint)).eval()
            self.finetune = True

        self.tokenizer = AutoTokenizer.from_pretrained(basemodel)
        
        self.verbose = verbose
        self.descr = description
        self.headername = headername
        self.random = random.Random(42)
        
    def save(self, conn, caseID, domain, shotID, query):
        cursor = conn.cursor()
        cursor.execute("INSERT OR REPLACE INTO evaluation (llm, finetune, caseid, domain, lang, shotid, query) VALUES (?, ?, ?, ?, ?, ?, ?)", (self.basename, self.finetune, caseID, domain, self.lang, shotID, query))
        conn.commit() 
        
    def makeContextHints(self):
        hint = f"""
```
{SEED.metamodel.get_metamodel_info()}
```
"""
        types = ['normal','find','disjunction','negation','aggregate','type']
        for feature in types:
            queryhint = self.random.choice(SEED[feature].examples)
            if self.lang=='ocl':
                hint = f"{hint}{queryhint.description}\n{queryhint[self.lang].signature}\n{QUERY.safe_substitute(lang=self.lang, query=queryhint[self.lang].query)}"
            else:
                hint = f"{hint}{queryhint.description}\n{QUERY.safe_substitute(lang=self.lang, query=queryhint[self.lang].query)}"
        return hint
        
    def test(self, metamodel, domain, tests, maxnewtokens=512, shots=5):
        outputs = defaultdict(list)

        for testcase in tqdm(tests, desc='Iterating test dataset', total=len(tests)):
            nl_description = testcase[self.descr]
            header = testcase[self.headername]
            caseID = testcase['id']

            prompt = COMPLETION_QUERY[self.lang].safe_substitute(
                    metamodel=metamodel.get_metamodel_info(),
                    description=nl_description,
                    header=header
                
            )
            if not self.finetune:
                prompt = f"""
{self.makeContextHints()}
{prompt}
"""
            
            sample = self.tokenizer([prompt], return_tensors="pt")
    
            with torch.no_grad():
                generated_sequences = self.model.generate(
                    input_ids=sample["input_ids"].cuda(),
                    attention_mask=sample["attention_mask"].cuda(),
                    do_sample=True,
                    max_new_tokens=maxnewtokens,
                    num_return_sequences=shots,
                    temperature=1, # We might play around but this is what ChatGPT 5 uses.
                    pad_token_id=self.tokenizer.eos_token_id,
                    eos_token_id=self.tokenizer.eos_token_id
                )
    
            generated_sequences = generated_sequences.cpu().numpy()
            generated_new_tokens = generated_sequences[:, sample["input_ids"].shape[1]:]

            with sqlite3.connect(self.db) as conn:
                for k, new_tokens in enumerate(generated_new_tokens):
                    generated = self.tokenizer.decode(new_tokens, skip_special_tokens=True)
                    query = (header + '\n' + generated) if self.lang!='ocl' else generated
                    if self.verbose:
                        print(query)
                        print('-' * 100)
                    self.save(conn, caseID, domain, k, query)

class ChatGPT:
    def __init__(self, effort="minimal", description=None, lang=None, tokens=0):
        self.client = OpenAI()
        self.effort = effort
        self.descr = description
        self.lang = lang
        self.tokens=newtokens
        self.random = random.Random(42)
        self.expertise = SEED.language[lang]

    def makeContextHints(self):
        hint = f"""
```
{SEED.metamodel.get_metamodel_info()}
```
"""
        types = ['normal','find','disjunction','negation','aggregate','type']
        for feature in types:
            queryhint = self.random.choice(SEED[feature].examples)
            if self.lang=='ocl':
                hint = f"{hint}{queryhint.description}\n{queryhint[self.lang].signature}\n{QUERY.safe_substitute(lang=self.lang, query=queryhint[self.lang].query)}"
            else:
                hint = f"{hint}{queryhint.description}\n{QUERY.safe_substitute(lang=self.lang, query=queryhint[self.lang].query)}"
        return hint

    def test(self, metamodel, domain, testcases):
        requests = []
        for testcase in testcases:
            for shot in range(5):
                rq = self.makeRequest(metamodel, testcase[self.descr], testcase[f"header_{self.lang}"], testcase['id'], shot, self.tokens)
                requests.append(rq)
        return requests

    def makeRequest(self, metamodel, description, header, case, shot, maxnewtokens):
        prompt = COMPLETION_QUERY[self.lang].safe_substitute(
                metamodel=metamodel.get_metamodel_info(),
                description=description,
                header=header        
        )
        prompt = f"""
{self.makeContextHints()}
{prompt}
"""

        return {
            "custom_id": f"eval_{case}_{self.lang}_{shot}", 
            "method": "POST", 
            "url": "/v1/responses", 
            "body": {
                "model": "gpt-5-2025-08-07",
                "instructions": f"You are an expert in {self.expertise}. Complete the last query. Follow the format of the examples for the completion.",
                "input": prompt,
                "reasoning": {
                    "effort": self.effort #minimal, low, medium
                }, 
                "max_output_tokens": maxnewtokens
            }
        }
    
    def submit(self, file, requests):
        base, ext = os.path.splitext(file)
        if ext != ".jsonl":
            raise ValueError("File must have a .jsonl extension")
        
        with open(file, 'w') as outfile:
            for entry in requests:
                json.dump(entry, outfile)
                outfile.write('\n')
        
        batch_input_file = self.client.files.create(
                file=open(file, "rb"),
                purpose="batch"
        )

        new_file = f"{base}_{batch_input_file.id}{ext}"
        os.rename(file, new_file)

        batchobject = self.client.batches.create(
            input_file_id=batch_input_file.id,
            endpoint="/v1/responses",
            completion_window="24h",
            metadata={
                "description": "Evaluation of Text2VQL test queries."
            }
        )
        with open(f"chatgpt/batchobj_{batchobject.id}.pkl", "wb") as f:
            pickle.dump(batchobject, f)


def processLine(line, db="evaluation.db", verbose=False):
    params = line.custom_id.split("_")#"eval_{case}_{self.lang}_{shot}"
    caseID = params[1]
    lang = params[2]
    shotID = params[3]

    message = next((msg for msg in line.response.body.output if msg.type=="message"), None)
    text = message.content[0].text
    #print(text)

    # VQL is aither in code block or just the whole prompt
    query_match = re.search(r'```[^\n]*\n(.*?)```', text, re.DOTALL)
    if query_match:
        text = query_match.group(1).strip()

    domain = None
    if params[1]=='':
        doamin = "dlt"
    else:
        if int(caseID) in range(0, 16+1):
            domain = 'railway'
        if int(caseID) in range(17, 26+1):
            domain = 'dlt'
        if int(caseID) in range(27, 38+1):
            domain = 'cps'
    if verbose:
        print("=======================================================================")
        print(text)
    with sqlite3.connect(db) as conn:
        cursor = conn.cursor()
        #cursor.execute("INSERT OR REPLACE INTO evaluation (llm, finetune, caseid, domain, lang, shotid, query) VALUES (ChatGPT, 0, ?, ?, ?, ?, ?)", (caseID, domain, lang, shotID, text))
        #conn.commit() 

        

def loadCSV(file):
    data = []
    with open(file, newline="", encoding='unicode_escape') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            data.append(dict(row))
    return data

# Code to generate the evaluation shots
"""
python -m evaluation.finetuned --lang java --basemodel qwen/qwen2.5-coder-1.5b
python -m evaluation.finetuned --lang java --basemodel qwen/qwen2.5-coder-1.5b --checkpoint qwen-1.5-java
python -m evaluation.finetuned --lang ocl --basemodel qwen/qwen2.5-coder-1.5b
python -m evaluation.finetuned --lang ocl --basemodel qwen/qwen2.5-coder-1.5b --checkpoint qwen-1.5-ocl
python -m evaluation.finetuned --lang vql --basemodel qwen/qwen2.5-coder-1.5b
python -m evaluation.finetuned --lang vql --basemodel qwen/qwen2.5-coder-1.5b --checkpoint qwen-1.5-vql
"""
"""
./promptllm.sh  Qwen/Qwen3-1.7B-Base qwen3-1.7b
python -m evaluation.finetuned --lang java --basemodel qwen/qwen2.5-coder-1.5b --checkpoint qwen-1.5-java
python -m evaluation.finetuned --lang ocl --basemodel qwen/qwen2.5-coder-1.5b
python -m evaluation.finetuned --lang ocl --basemodel qwen/qwen2.5-coder-1.5b --checkpoint qwen-1.5-ocl
python -m evaluation.finetuned --lang vql --basemodel qwen/qwen2.5-coder-1.5b
python -m evaluation.finetuned --lang vql --basemodel qwen/qwen2.5-coder-1.5b --checkpoint qwen-1.5-vql
"""

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Run trained models')
    parser.add_argument('--times', type=int, default=5)
    #parser.add_argument('--temperature', type=float, default=0.4)
    parser.add_argument('--basemodel', default="qwen/qwen2.5-coder-1.5b")
    parser.add_argument('--checkpoint')
    parser.add_argument('--lang', default="java")
    parser.add_argument('--db', default="evaluation.db")
    parser.add_argument('--description', default="description")
    parser.add_argument('--verbose', default=True)
    parser.add_argument('--truth', default='../dataset_construction/test_metamodel/truth.csv')
    parser.add_argument('--files', default='', help='Coma separated list of output files')
    args = parser.parse_args()

    newtokens = 512 if args.lang!='java' else 1024
    tests = loadCSV(args.truth)
    railway = [x for x in tests if x['domain']=='railway']
    dlt = [x for x in tests if x['domain']=='dlt']
    cps = [x for x in tests if x['domain']=='cps']

    if args.basemodel in ['ChatGPT--prompt','ChatGPT--process']:
        if args.basemodel=='ChatGPT--prompt':
            llm = ChatGPT(description=args.description, lang=args.lang, tokens=newtokens)
            rq_railway = llm.test(MetaModel(os.path.join(ROOT, 'dataset_construction/test_metamodel/dlt.ecore')), 'railway', railway)
            rq_dlt = llm.test(MetaModel(os.path.join(ROOT, 'dataset_construction/test_metamodel/dlt.ecore')), 'dlt', dlt)
            rq_cps = llm.test(MetaModel(os.path.join(ROOT, 'dataset_construction/test_metamodel/dlt.ecore')), 'cps', cps)
            
            combined = rq_railway+rq_dlt+rq_cps
            llm.submit(f"chatgpt/eval_{args.lang}.jsonl",combined)
        else:
            puller = Puller()
            time = datetime.now().isoformat()
            
            with open(f"chatgpt/response_{time}.jsonl", "w") as f:
                for file in args.files.split(','):
                    results = puller.pulloutput(file)
                    f.write(results)
            with open(f"chatgpt/response_{time}.jsonl", 'r') as f:
                for line in f:
                    processLine(AttrDict(json.loads(line)), verbose=True)# Do something with the record


    else:
        llm = LLM(args.basemodel, args.checkpoint, verbose=args.verbose, description=args.description, headername=f"header_{args.lang}", db=args.db, lang=args.lang)

        # Let's warm the server room
        llm.test(MetaModel(os.path.join(ROOT, 'dataset_construction/test_metamodel/dlt.ecore')), 'dlt', dlt, maxnewtokens=newtokens)
        llm.test(MetaModel(os.path.join(ROOT, 'dataset_construction/test_metamodel/railway.ecore')), 'railway', railway, maxnewtokens=newtokens)
    
        llm.test(MetaModel(os.path.join(ROOT, 'dataset_construction/test_metamodel/cps.ecore')), 'cps', cps, maxnewtokens=newtokens)
        