import sqlite3

import pandas as pd
from tqdm import tqdm
from concurrent.futures import ProcessPoolExecutor,ThreadPoolExecutor

import os
import pickle

from text2vql.util.metamodel import MetaModel
from text2vql.util.processor import  ConcurrentPipelineProcessor,Iterable2Queue,Queue2Iterable
from text2vql.util.args import makeParser

def clear_similarities(conn):
    cursor = conn.cursor()
    cursor.execute("DELETE FROM similarities")
    cursor.execute("DELETE FROM sqlite_sequence WHERE name='similarities'")
    conn.commit()

def list_models_of(conn, dataset):
    cursor = conn.cursor()
    cursor.execute("SELECT model FROM metamodels WHERE dataset = ?", (dataset,))
    return [row[0] for row in cursor.fetchall()]

def drop_similarities_of(conn, dataset):
    cursor = conn.cursor()
    # Delete from similarities where m1 or m2 is a model from the given dataset
    cursor.execute("""
        DELETE FROM similarities
        WHERE m1 IN (SELECT model FROM metamodels WHERE dataset = ?)
           OR m2 IN (SELECT model FROM metamodels WHERE dataset = ?)
    """, (dataset, dataset))
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

def add_similarities(conn, similarities):
    cursor = conn.cursor()
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

def extract_concepts(path):
    metamodel = MetaModel(path)
    return path, {c.lower() for c in metamodel.get_elements()}

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
    
def compute_similarity_pairs(db, concepts_dict,BATCH_SIZE=100_000_000, update=None):
    with sqlite3.connect(db) as conn:
        clear_similarities(conn)

        entries = concepts_dict
        if(update is not None):
            entries = {}
            for path in update:
                entries[path] = concepts_dict[path]

        batch_results = []
        for entry in tqdm(entries.items(), desc='Computing similarities'):
            f = SimilarityComputer(db, entry)
            with ConcurrentPipelineProcessor(f, Iterable2Queue(concepts_dict.items())) as sim:
                result = list(Queue2Iterable(sim.output()))
                batch_results.extend(result)
                if len(batch_results) >= BATCH_SIZE:
                    add_similarities(conn, batch_results)
                    batch_results.clear()  # reset batch
            
        add_similarities(conn, batch_results)
        batch_results.clear()  # reset batch

def load_concepts_cache(conn, cache_file="cache/concepts.pkl", load_cache=True, update=None):
    concepts = {}
    if load_cache and os.path.exists(cache_file):
        with open(cache_file, "rb") as f:
            concepts = pickle.load(f)
    else:
        paths = get_valid_metamodel_paths(conn)
        with ProcessPoolExecutor() as executor:
            results = list(tqdm(executor.map(extract_concepts, paths), total=len(paths), desc='Extracting concepts'))
            concepts = dict(results)
        with open(cache_file, "wb") as f:
            pickle.dump(concepts, f)
    
    if load_cache and update is not None: 
        with ProcessPoolExecutor() as executor:
            results = list(tqdm(executor.map(extract_concepts, update), total=len(update), desc='Updating concepts'))
            concepts.update(dict(results))
        with open(cache_file, "wb") as f:
            pickle.dump(concepts, f)
    return concepts

def compute_similarities(db, skip_on_exists=False, skip_over_size=-1, load_cache=True, update=None):
    with sqlite3.connect(db) as conn:
        if skip_on_exists and similarities_exists(conn):
            return
        if skip_over_size >= 0 and count_similarities(conn) >= skip_over_size:
            return
    
        concepts = load_concepts_cache(conn, load_cache=load_cache, update=update)
        compute_similarity_pairs(db, concepts, update=update)

# Runnable version to (re)compute similarities for a set of metamodels
# /workspaces/Text2VQL/dataset_construction$ python util2/compute_similarities.py --dataset=test_metamodel
def main():
    parser = makeParser()
    parser.add_argument('--dataset', type=str, default='test_metamodel', help='Dataset where the similarities should be recomputed.')
    args = parser.parse_args()
    db = args.db
    dataset = args.dataset

    conn = sqlite3.connect(db)
    models = list_models_of(conn, dataset)
    drop_similarities_of(conn, dataset)
    conn.close()

    compute_similarities(db, update=models)

if __name__ == "__main__":
    main()