import argparse
import sqlite3

import pandas as pd
from tqdm import tqdm

from text2vql.metamodel import MetaModel


def get_metamodel_paths(db):
    cnx = sqlite3.connect(db)
    df = pd.read_sql_query("SELECT id FROM metamodels", cnx)
    paths = list(df['id'])
    return paths


def register_pairs(pairs, db):
    cnx = sqlite3.connect(db)
    cursor = cnx.cursor()
    for pair in pairs:
        cursor.execute("INSERT INTO similarities(m1, m2) VALUES (?, ?)", pair)
    cnx.commit()
    cnx.close()


def jaccard(x, y):
    intersection_cardinality = len(x.intersection(y))
    union_cardinality = len(x.union(y))
    return intersection_cardinality / float(union_cardinality)


def get_duplicates(concepts_dict, threshold):
    pairs = []
    for path1, concepts1 in tqdm(concepts_dict.items(), desc='Computing similarities'):
        for path2, concepts2 in concepts_dict.items():
            if path1 >= path2:
                continue
            sim = jaccard(concepts1, concepts2)
            if sim > threshold:
                pairs.append((path1, path2))
    return pairs


def main(args):
    paths = get_metamodel_paths(args.db)

    concepts = {}
    for path in tqdm(paths, desc='Extracting concepts'):
        metamodel = MetaModel(path)
        concepts[path] = set([c.lower() for c in metamodel.get_elements()])
    pairs = get_duplicates(concepts, args.threshold)
    register_pairs(pairs, args.db)


if __name__ == '__main__':
    # parse args
    parser = argparse.ArgumentParser()
    parser.add_argument('--db', type=str, default='dataset.db')
    parser.add_argument('--threshold', type=float, default=0.6)
    args = parser.parse_args()

    main(args)
