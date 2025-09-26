import argparse
import os
import sqlite3

from text2vql.util.parse_dataset import indexAll
from text2vql.util.init_db import init_db
from text2vql.util.compute_similarities import compute_similarities
from text2vql.util.compute_clusters import update_clusters
from text2vql.util.sample_clusters import sample

from text2vql.util.args import parser

args = parser.parse_args()
folders = args.metamodels_datasets.split(",")
db = args.db
schema = args.schema
samples = args.sampleloc


init_db(db, schema)
indexAll(folders, db)
compute_similarities(db, skip_on_exists=True)
update_clusters(db)
sample(db, 600, samples)

print("Completed. Please continue by compiling the jars with the provided 'ecore2jar' tool. When completed continue with readback_java.py.")
