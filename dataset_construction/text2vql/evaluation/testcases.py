import argparse
import requests
import csv
import os

from text2vql.evaluation.evaluate import getVQLQueryName
from text2vql.seed.util import AttrDict

class Testcases:
    def __init__(self, truth):
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

    def process(self, caseid):
        domain = self.truth[caseid]['domain']

        vqlname = getVQLQueryName(self.truth[caseid]['header_vql'])

        task = {
            "wd": os.getcwd(),
            "metamodel": f"test_metamodel/{domain}.ecore",
            'models': f'../results/testmodels/{domain}',
            'query': {'id': -1, 'entry': vqlname, 'query': self.truth[caseid]['truth_vql']},
        }
        print('==========================================================')
        print(task)
        resp = requests.post(f"{self.endpoint}/count", json=task, timeout=self.timeout)
        resp.raise_for_status()
        data = resp.json()
        print('----------------------------------------------------')
        counter = 0
        for model, matches in data.items():
            counter += matches
        print(data)
        print(counter)
        return AttrDict(data)


        
    
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Parse dataset')
    parser.add_argument('--truth', type=str, default='test_metamodel/truth.csv')
    parser.add_argument('--id', type=int, default=0)
    args = parser.parse_args()

    eval = Testcases(args.truth)
    print(eval.process(args.id))