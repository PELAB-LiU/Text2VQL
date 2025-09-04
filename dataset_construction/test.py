import argparse
import os
import sqlite3

from util2.parse_dataset import indexAll
from util2.init_db import init_db
from util2.compute_similarities import compute_similarities
# parse arguments
parser = argparse.ArgumentParser(description='Parse dataset')
parser.add_argument('--metamodels_datasets', type=str, default='metamodels/0-raw/ecore555,metamodels/0-raw/repo-atlanmod,metamodels/0-raw/repo-ecore-all,test_metamodel',
                    help='metamodels dataset folder (coma separated list)')
parser.add_argument('--db', type=str, default='dataset.db', help='database file')
parser.add_argument('--schema', type=str, default='schema.sql', help='SQL file to create databse tables')
#parser.add_argument('--csv', type=str, default='dataset.csv', help='output CSV file')

args = parser.parse_args()
folders = args.metamodels_datasets.split(",")
db = args.db
schema = args.schema


from util2.processor import  ConcurrentPipelineProcessor,Iterable2Queue,Queue2Iterable



#def pow2(x):
#    print(f"Processing {x}")
#    return x**2
#
#def is_even(x):
#    return x%2 == 0
#
#queue = Iterable2Queue(range(10))
#phase1 = ConcurrentPipelineProcessor(pow2, queue)
#phase2 = ConcurrentPipelineProcessor(is_even, phase1.output())
#array = Queue2Iterable(phase2.output())
#
#for e in array:
#    print(e)
#sim_none = ConcurrentPipelineProcessor(f, Iterable2Queue(concepts_dict.items()))
#                sims = ConcurrentPipelineProcessor(remove_none, sim_none.output())
#                result = list(Queue2Iterable(sims.output()))

#init_db(db, schema)
#indexAll(folders, db)

compute_similarities(db, 0.7)
# python select_representative_metamodels.py
# python generate_dataset.py --sample 10