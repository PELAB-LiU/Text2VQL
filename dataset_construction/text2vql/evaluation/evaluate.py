import argparse
import sqlite3
import requests

from text2vql.util.args import makeParser
from text2vql.seed.seed_yakindu import SEED
from text2vql.datasetgen.campaign import CampaignBase
from text2vql.seed.util import AttrDict

import json
import os
import pickle
import json
import csv
import re
from datetime import datetime

def getVQLQueryName(header_vql):
    pattern = re.compile(r"\bpattern\s+(\w+)\s*\(")
    match = pattern.search(header_vql)
    return match.group(1) if match else None

def getJavaMethodName(header_java):
    pattern = re.compile(
        r'\b(?:public|protected|private|static|\s)*\w+[\[<]?[\w,\d\s]+[\]>]?\s+(\w+)\s*\(\s*Resource\s*\w*\)\s*\{?'
    )
    match = pattern.search(header_java)
    return match.group(1) if match else None

class Evaluator:
    def __init__(self, db, truth):
        self.db = db
        self.timeout = 60*60*5 # 5 hours?
        self.endpoint = "http://localhost:63028"
        self.truth = {}

        with open(truth, mode='r', newline='', encoding='unicode_escape') as file:
            reader = csv.DictReader(file)
            data = [row for row in reader]
            ids = [row['id'] for row in data if row['id'].isdigit()]

            for row in data:
                row_id = row['id']
                # Only add if this id hasn't been seen yet
                if (row_id not in self.truth) and (row_id is not None) and (row_id.isdigit()):
                    self.truth[int(row_id)] = row

    def extractQuery(self, string):
        pattern = r"```(?:vql|java|ocl)([\s\S]*?)(?:```|$)"
        match = re.search(pattern, string, re.IGNORECASE)
        return match.group(1).strip() if match else string

    def getTest(self, caseid):
        with sqlite3.connect(self.db) as conn:
            cursor = conn.cursor()
            #cursor.execute("SELECT id, lang, query FROM evaluation WHERE caseid = ? AND shotid=1 AND finetune=1", (caseid,))
            cursor.execute("SELECT id, lang, query FROM evaluation WHERE caseid = ? AND syntax IS NULL AND semantics IS NULL", (caseid,))
            return cursor.fetchall()
        
    def getDomain(self, caseid):
        with sqlite3.connect(self.db) as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT DISTINCT domain FROM evaluation WHERE caseid = ?", (caseid,))
            return cursor.fetchone()[0]
    
    def update(self, ids, data):
        with sqlite3.connect(self.db) as conn:
            cursor = conn.cursor()
            for id in ids:
                entry = data[id]
                cursor.execute("UPDATE evaluation SET syntax = ?, diagnostics = ?, semantics = ?, idicator = ? WHERE id = ?",
                        (entry.syntax.syntax, str(entry.syntax.diagnostics), entry.semantics.semantics, entry.semantics.indicator, id))
            conn.commit()
        

    def process(self, caseid):
        domain = self.getDomain(caseid)
        allshots = self.getTest(caseid)

        if not allshots:
            return AttrDict({})
        
        vqlname = getVQLQueryName(self.truth[caseid]['header_vql'])
        javaname = getJavaMethodName(self.truth[caseid]['header_java'])

        task = {
            "wd": os.getcwd(),
            "metamodel": f"test_metamodel/{domain}.ecore",
            'jar': f"test_metamodel/{domain}.jar",
            'models': f'../results/testmodels/{domain}',
            'testcase': {
                'truth': {'id': -1, 'entry': vqlname, 'query': self.truth[caseid]['truth_vql']} ,
                'vql': [{'id': x[0], 'entry': vqlname, 'query': self.extractQuery(x[2])} for x in allshots if x[1]=='vql'],
                'ocl': [{'id': x[0], 'entry': '', 'query': self.extractQuery(x[2])} for x in allshots if x[1]=='ocl'],
                'java': [{'id': x[0], 'entry': javaname, 'query': self.extractQuery(x[2])} for x in allshots if x[1]=='java']
            }
        }
        print('==========================================================')
        print(task)
        resp = requests.post(f"{self.endpoint}/eval", json=task, timeout=self.timeout)
        resp.raise_for_status()
        data = resp.json()
        print('----------------------------------------------------')
        print(data)
        return AttrDict(data)


        
    
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Parse dataset')
    parser.add_argument('--db', type=str, default='../finetuning/evaluation(8).db')
    parser.add_argument('--truth', type=str, default='test_metamodel/truth.csv')
    parser.add_argument('--id', type=int, default=None)
    args = parser.parse_args()

    eval = Evaluator(args.db, args.truth)
    if args.id is not None:
        result = eval.process(args.id)
        #eval.update(result.keys(), result)
    else:
        for i in range(0, 38+1):
            if i==23:
                continue
            result = eval.process(i)
            eval.update(result.keys(), result)
