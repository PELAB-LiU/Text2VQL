import argparse
import sqlite3

import networkx as nx
import pandas as pd

from util2.compute_similarities import get_valid_metamodel_paths


def get_metamodel_pairs(conn, threshold=0.7):
    df = pd.read_sql_query(f"SELECT m1, m2 FROM similarities WHERE similarity >= {threshold}", conn)
    pairs = []
    for i, row in df.iterrows():
        pairs.append((row['m1'], row['m2']))
    return pairs


def get_graph(paths, pairs):
    G = nx.Graph()
    G.add_nodes_from(paths)
    G.add_edges_from(pairs)
    return G


TEST_METAMODEL = 'test_metamodel/railway.ecore'


def main(args):
    paths = get_valid_metamodel_paths(args.db)
    pairs = get_metamodel_pairs(args.db)
    G = get_graph(paths, pairs)

    cnx = sqlite3.connect(args.db)
    cursor = cnx.cursor()

    component_id = 1
    for c in nx.connected_components(G):
        c = list(c)
        c.sort()

        # Skip the component containing the test metamodel
        testcluster = TEST_METAMODEL in c

        # Assign the same component ID to all members of this component
        cluster_id = 0 if TEST_METAMODEL in c else component_id
        for node in c:
            cursor.execute(
                "INSERT INTO representatives(id, cluster_id) VALUES (?, ?)",
                (node, cluster_id)
            )
        # TODO augment database table
        if not cluster_id == 0:
            component_id += 1

    cnx.commit()
    cnx.close()



if __name__ == '__main__':
    # parse args
    parser = argparse.ArgumentParser()
    parser.add_argument('--db', type=str, default='dataset.db')
    args = parser.parse_args()

    main(args)