import argparse
import glob
import os
import sqlite3

from tqdm import tqdm

from text2vql.metamodel import MetaModel

# parse arguments
parser = argparse.ArgumentParser(description='Parse dataset')
parser.add_argument('--metamodels_dataset', type=str, default='ecore555',
                    help='metamodels dataset folder')
parser.add_argument('--db', type=str, default='dataset.db', help='database file')
args = parser.parse_args()
folder = args.metamodels_dataset
db = args.db


def index_metamodels(metamodels, db, folder):
    conn = sqlite3.connect(db)
    cursor = conn.cursor()
    for m in tqdm(metamodels, desc="Indexing metamodels"):
        tuple = (m.path, folder, m.get_metamodel_info(), m.number_of_elements())
        cursor.execute("INSERT INTO metamodels(id, dataset, definition, elements) VALUES (?,?,?,?)", tuple)
    conn.commit()
    conn.close()


metamodels = []
# for each *.ecore
for file in tqdm(glob.glob(os.path.join(folder, "**", "*.ecore"), recursive=True), desc="Parsing metamodels"):
    try:
        metamodel = MetaModel(file)
        info = metamodel.get_metamodel_info()
        if len(info.strip()) == 0:
            continue
        metamodels.append(metamodel)
    except:
        continue

index_metamodels(metamodels, db, folder)
