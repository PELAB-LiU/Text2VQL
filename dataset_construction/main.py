import argparse
import os
import sqlite3

from util2.parse_dataset import indexAll
from util2.init_db import init_db

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



init_db(db, schema)
indexAll(folders, db)
# python compute_similarities.py
# python select_representative_metamodels.py
# python generate_dataset.py --sample 10