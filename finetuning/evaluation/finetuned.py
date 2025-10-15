from evaluation.external import TEXT2VQL_ROOT as ROOT #Load text2vql project to system path.

import os
import re

import argparse
from collections import defaultdict
import sqlite3
import pandas as pd
import torch
from peft import PeftModel
from tqdm import tqdm
from transformers import AutoModelForCausalLM, AutoTokenizer

# Must import evaluation.external before in order to ensure proper operation
# It loads finds the text2vql root folder and adds the text2vql python module to the system module path.
from text2vql.util.metamodel import MetaModel

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
    def __init__(self, basemodel, checkpoint=None, verbose=False, description='nl', signature='header_vql', db='evaluation.db', lang='vql'):
        self.basename = basemodel
        self.finetune = False
        self.lang = lang
        self.db = db
        
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
        self.sign = signature
    
    def save(self, conn, caseID, domain, shotID, query):
        cursor = conn.cursor()
        cursor.execute("INSERT OR REPLACE INTO evaluation (llm, finetune, caseid, domain, lang, shotid, query) VALUES (?, ?, ?, ?, ?, ?, ?)", (self.basename, self.finetune, caseID, domain, self.lang, shotID, query))
        conn.commit() 
        
    def test(self, metamodel, domain, tests, maxnewtokens=512, shots=5):
        outputs = defaultdict(list)

        for testcase in tqdm(tests, desc='Iterating test dataset', total=len(tests)):
            nl_description = testcase[self.descr]
            header = testcase[self.sign]
            caseID = testcase['id']

            prompt = """
                {metamodel}
                //{nl}
                {header}
                """.format(metamodel=metamodel.get_metamodel_info(),
                           nl=nl_description,
                           header=header)
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
                    query = header + '\n' + generated
                    if self.verbose:
                        print(query)
                        print('-' * 100)
                    self.save(conn, caseID, domain, k, query)




if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='Run trained models')
    parser.add_argument('--times', type=int, default=5)
    #parser.add_argument('--temperature', type=float, default=0.4)
    parser.add_argument('--basemodel', default="qwen/qwen2.5-coder-1.5b")
    #parser.add_argument('--checkpoint', default="qwen-1.5-java")
    parser.add_argument('--checkpoint')
    parser.add_argument('--lang', default="java")
    parser.add_argument('--db', default="evaluation.db")
    parser.add_argument('--nl', default="nl")
    parser.add_argument('--header', default="header_java")
    parser.add_argument('--verbose', default=True)

    args = parser.parse_args()

    #test_dataset = pd.read_csv(os.path.join(ROOT, 'dataset_construction/test_metamodel/test_queries.csv'), sep=',', encoding='unicode_escape')
    
    llm = LLM(args.basemodel, args.checkpoint, verbose=args.verbose, description=args.nl, signature=args.header, db=args.db, lang=args.lang)
    llm.test(MetaModel(os.path.join(ROOT, 'dataset_construction/test_metamodel/railway.ecore')),
        'trainbenchmark',
        [{
            'id': 0,
            'nl': 'Semaphores with STOP or GO signals.',
            'header_java': 'public class Query{ public List<Semaphore> stopOrGo(Resource resource){'
        },
        {
            'id': 1,
            'nl': 'Track elements monitored by at lest two sensors.',
            'header_java': 'public class Query{ public List<TrackElement> monitoredBy2Sensors(Resource resource){'
        }]
    )
        