import sqlite3

import pandas as pd
from tqdm import tqdm
from concurrent.futures import ProcessPoolExecutor,ThreadPoolExecutor

from util2.metamodel import MetaModel
from util2.processor import  ConcurrentPipelineProcessor,Iterable2Queue,Queue2Iterable
from util2.queue import Queue

import threading
import time
import os
import signal
import sys
import tracemalloc
import time

def clear_similarities(conn):
    cursor = conn.cursor()
    cursor.execute("DELETE FROM similarities")
    cursor.execute("DELETE FROM sqlite_sequence WHERE name='similarities'")
    conn.commit()


def check_pair_exists(conn, model1, model2):
    if model1 == model2:
        return True
    
    cursor = conn.cursor()
    cursor.execute("SELECT 1 FROM similarities WHERE (m1 = ? AND m2 = ?) OR (m1 = ? AND m2 = ?) LIMIT 1", (model1,model2,model2,model1))
    return cursor.fetchone() is not None

def add_pair(conn, model1, model2, similarity):
    cursor = conn.cursor()
    cursor.execute("INSERT INTO similarities(m1, m2, similarity) VALUES (?, ?, ?) ", (model1,model2,similarity))
    conn.commit()


class SkipNone:
    def __init__(self, iterable):
        self.iterable = iterable

    def __iter__(self):
        for item in self.iterable:
            if item is not None:
                yield item
def add_similarities(conn, similarities):
    cursor = conn.cursor()
    #cursor.executemany("INSERT INTO similarities(m1, m2, similarity) VALUES (?, ?, ?) ON CONFLICT(m1, m2) DO UPDATE SET similarity=excluded.similarity", similarities)
    cursor.executemany("INSERT INTO similarities(m1, m2, similarity) VALUES (?, ?, ?)", similarities)
    conn.commit()

def similarities_exists(conn):
    cursor = conn.cursor()
    cursor.execute("SELECT 1 FROM similarities LIMIT 1")
    return cursor.fetchone() is not None


def count_similarities(conn):
    cursor = conn.cursor()
    cursor.execute("SELECT COUNT(*) FROM similarities")
    count = cursor.fetchone()[0]
    return count

def get_valid_metamodel_paths(conn):
    df = pd.read_sql_query("SELECT model FROM metamodels WHERE parseable", conn)
    paths = list(df['model'])
    return paths


#def register_pairs(pairs, db):
#    cnx = sqlite3.connect(db)
#    cursor = cnx.cursor()
#    for pair in pairs:
#        cursor.execute("INSERT INTO similarities(m1, m2) VALUES (?, ?)", pair)
#    cnx.commit()
#    cnx.close()

def extract_concepts(path):
    metamodel = MetaModel(path)
    return path, {c.lower() for c in metamodel.get_elements()}

#def jaccard(x, y):
#    intersection_cardinality = len(x.intersection(y))
#    union_cardinality = len(x.union(y))
#    return intersection_cardinality / float(union_cardinality)

#def compute_similarity_pairs_helper(db, concepts_entry, concepts_dict, skip=True):
#    path1, concepts1 = concepts_entry
#    with sqlite3.connect(db) as conn:
#        for path2,concepts2 in concepts_dict.items():
#            if not (skip and check_pair_exists(conn, path1, path2)):
#                    sim = jaccard(concepts1, concepts2)
#                    return path1, path2, sim
#                    #add_pair(conn, path1, path2, sim)
#    return None

class SimilarityComputer:
    def __init__(self, db, current):
        self.db = db
        self.current = current
    def __call__(self, candidate):
        path1, concepts1 = candidate
        path2, concepts2 = self.current
        if(path1 < path2):
            sim = self.jaccard(concepts1, concepts2)
            if(sim > 0.5):
                return path1, path2, sim
        return None

    def jaccard(self, x, y):
        intersection_cardinality = len(x.intersection(y))
        union_cardinality = len(x.union(y))
        return intersection_cardinality / float(union_cardinality)
    
def compute_similarity_pairs(db, concepts_dict,BATCH_SIZE=100_000_000):
    with sqlite3.connect(db) as conn:
        clear_similarities(conn)

        batch_results = []
        for entry in tqdm(concepts_dict.items(), desc='Computing similarities'):
            f = SimilarityComputer(db, entry)
            with ConcurrentPipelineProcessor(f, Iterable2Queue(concepts_dict.items())) as sim:
                result = list(Queue2Iterable(sim.output()))
                batch_results.extend(result)
                if len(batch_results) >= BATCH_SIZE:
                    add_similarities(conn, batch_results)
                    batch_results.clear()  # reset batch
            
        add_similarities(conn, batch_results)
        batch_results.clear()  # reset batch
        print("saveing")
    

def remove_none(iterable):
    return [x for x in iterable if x is not None]

def compute_similarities(db, threshold, skip_on_exists=False, skip_over_size=-1):
    conn = sqlite3.connect(db)
    if skip_on_exists and similarities_exists(conn):
        conn.close()
        return
    if skip_over_size >= 0 and count_similarities(conn) >= skip_over_size:
        conn.close
        return

    paths = get_valid_metamodel_paths(conn)#[:2000]
    concepts = {}
    with ProcessPoolExecutor() as executor:
        results = list(tqdm(executor.map(extract_concepts, paths), total=len(paths), desc='Extracting concepts'))
        concepts = dict(results)
    
    compute_similarity_pairs(db, concepts)
    
    
 #   register_pairs(pairs, db)
