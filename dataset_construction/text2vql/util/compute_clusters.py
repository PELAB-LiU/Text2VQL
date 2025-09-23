import sqlite3

import networkx as nx
import pandas as pd

from util2.compute_similarities import get_valid_metamodel_paths

def upsert_cluster(conn, model, cluster):
    cursor = conn.cursor()
    cursor.execute("INSERT OR REPLACE INTO clusters (model, cluster) VALUES (?, ?)", (model, cluster))
    conn.commit()

def upsert_many_clusters(conn, data):
    cursor = conn.cursor()
    cursor.executemany("INSERT OR REPLACE INTO clusters (model, cluster) VALUES (?, ?)", data)
    conn.commit()

def get_similar_metamodels(conn, threshold):
    df = pd.read_sql_query(f"SELECT m1, m2 FROM similarities WHERE similarity >= {threshold}", conn)
    pairs = []
    for i, row in df.iterrows():
        pairs.append((row['m1'], row['m2']))
    return pairs

def get_models_of_dataset(conn, dataset):
    df = pd.read_sql_query("SELECT model FROM metamodels WHERE dataset=?", conn, params=(dataset,))
    paths = df['model'].tolist()
    return paths

def get_graph(paths, pairs):
    G = nx.Graph()
    G.add_nodes_from(paths)
    G.add_edges_from(pairs)
    return G


def update_clusters(db, threshold=0.7, testset="test_metamodel" ):
    with sqlite3.connect(db) as conn:
        testset = set(get_models_of_dataset(conn, testset))
        paths = get_valid_metamodel_paths(conn)
        pairs = get_similar_metamodels(conn, threshold)
        G = get_graph(paths, pairs)

        component_id = int(1)
        for component in nx.connected_components(G):
            component = set(component)

            if testset.intersection(component):
                upsert_many_clusters(conn, [(x, 0) for x in component])
                conn.commit()
            else:
                upsert_many_clusters(conn, [(x, component_id) for x in component])
                conn.commit()
                component_id += 1
        conn.commit()

