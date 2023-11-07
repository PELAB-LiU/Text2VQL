import argparse
import sqlite3

import networkx as nx
import pandas as pd

from compute_similarities import get_metamodel_paths


def get_metamodel_pairs(db):
    cnx = sqlite3.connect(db)
    df = pd.read_sql_query("SELECT m1, m2 FROM similarities", cnx)
    pairs = []
    for i, row in df.iterrows():
        pairs.append((row['m1'], row['m2']))
    return pairs


def get_graph(paths, pairs):
    G = nx.Graph()
    G.add_nodes_from(paths)
    G.add_edges_from(pairs)
    return G


def main(args):
    paths = get_metamodel_paths(args.db)
    pairs = get_metamodel_pairs(args.db)
    G = get_graph(paths, pairs)

    # connected components
    representatives = []
    for c in nx.connected_components(G):
        c = list(c)
        c.sort()
        representatives.append(c[0])

    # register representatives
    cnx = sqlite3.connect(args.db)
    cursor = cnx.cursor()
    for rep in representatives:
        cursor.execute("INSERT INTO representatives(id) VALUES (?)", (rep,))
    cnx.commit()


if __name__ == '__main__':
    # parse args
    parser = argparse.ArgumentParser()
    parser.add_argument('--db', type=str, default='dataset.db')
    args = parser.parse_args()

    main(args)
