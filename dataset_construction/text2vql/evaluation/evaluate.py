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
        self.timeout = 60*60*5
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



    def getTest(self, caseid):
        with sqlite3.connect(self.db) as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT id, lang, query FROM evaluation WHERE caseid = ? AND shotid=1 AND finetune=1", (caseid,))
            return cursor.fetchall()
    def getDomain(self, caseid):
        with sqlite3.connect(self.db) as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT DISTINCT domain FROM evaluation WHERE caseid = ?", (caseid,))
            return cursor.fetchone()[0]

    def process(self, caseid):
        domain = self.getDomain(caseid)
        allshots = self.getTest(caseid)

        vqlname = getVQLQueryName(self.truth[caseid]['header_vql'])
        javaname = getJavaMethodName(self.truth[caseid]['header_java'])

        task = {
            "wd": os.getcwd(),
            "metamodel": f"test_metamodel/{domain}.ecore",
            'jar': f"test_metamodel/{domain}.jar",
            'models': '../results/testmodels',
            'testcase': {
                'truth': {'id': -1, 'entry': vqlname, 'query': self.truth[caseid]['truth_vql']} ,
                'vql': [{'id': x[0], 'entry': vqlname, 'query':x[2]} for x in allshots if x[1]=='vql'],
                'ocl': [{'id': x[0], 'entry': '', 'query':x[2]} for x in allshots if x[1]=='ocl'],
                'java': [{'id': x[0], 'entry': javaname, 'query':x[2]} for x in allshots if x[1]=='java']
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
    parser.add_argument('--db', type=str, default='../finetuning/evaluation-sample.db')
    parser.add_argument('--truth', type=str, default='test_metamodel/truth.csv')
    args = parser.parse_args()

    eval = Evaluator(args.db, args.truth)
    print(eval.process(2))
