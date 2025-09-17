import glob
import os
import sys
import sqlite3

from tqdm import tqdm
from concurrent.futures import ProcessPoolExecutor,ThreadPoolExecutor

current_dir = os.path.dirname(os.path.abspath(__file__))
if current_dir not in sys.path:
    sys.path.append(current_dir)

from metamodel import MetaModel
from args import parser


def dataset_exists(db, dataset_name):
    conn = sqlite3.connect(db)
    cursor = conn.cursor()
    cursor.execute("SELECT 1 FROM metamodels WHERE dataset = ? LIMIT 1", (dataset_name,))
    flag = cursor.fetchone() is not None
    conn.close()
    return flag


def check_model_already_parsed(conn, model_path):
    cursor = conn.cursor()
    cursor.execute("SELECT 1 FROM metamodels WHERE model = ? LIMIT 1", (model_path,))
    line = cursor.fetchone()
    return line is not None

def insert_ok(conn, model, dataset, parsed):
    cursor = conn.cursor()
    cursor.execute("INSERT INTO metamodels(model, dataset, parseable, definition, elements) VALUES (?,?,?,?,?)", (model,dataset,True,parsed.get_metamodel_info(),parsed.number_of_elements()))
    conn.commit()

def insert_ok2(conn, model, dataset, info, elements):
    cursor = conn.cursor()
    cursor.execute("INSERT INTO metamodels(model, dataset, parseable, definition, elements) VALUES (?,?,?,?,?)", (model,dataset,True,info,elements))
    conn.commit()

def insert_erroneous(conn, model, dataset):
    cursor = conn.cursor()
    cursor.execute("INSERT INTO metamodels(model, dataset, parseable) VALUES (?,?,?)", (model,dataset,False))
    conn.commit()

class Indexer:
    def __init__(self, db, dataset, skip):
        self.db = db
        self.dataset = dataset
        self.skip = skip
    def __call__(self, file):
        with sqlite3.connect(self.db) as conn:
            try:
                if not (self.skip and check_model_already_parsed(conn, file)):
                    metamodel = MetaModel(file)
                    info = metamodel.get_metamodel_info()
                    if len(info.strip()) == 0:
                        return False, file, self.dataset
                    else:
                        return True, file, self.dataset, metamodel
            except:
                return False, file, self.dataset
        return None
    
class Indexer2:
    def __init__(self, db, dataset, skip):
        self.indexer = Indexer(db,dataset,skip)
    def __call__(self, file):
        result = self.indexer(file)
        if result is None:
            return None
        if result[0]:
            ok, file, dataset, metamodel = result
            return ok, file, dataset, metamodel.get_metamodel_info(), metamodel.number_of_elements()
        else:
            return result

def find_and_index_metamodels_multithread(db, root, dataset, skip=True):
    models = glob.glob(os.path.join(root, "**", "*.ecore"), recursive=True)
    f = Indexer(db, dataset, skip)
    # This is messed up.
    # If ThreadPool is used, then something leaks memory, leading to 24GiB memory usage with 5 of 6 GiB SWAP.
    # If ProcessPool is used, then no leak, but you can't serialize the metamodel. 
    # I assume the problem has to do something with PyEcore. (Code seems to use a gloabl package registry.) 
    with ThreadPoolExecutor() as executor:
        with sqlite3.connect(db) as conn:
            for result in tqdm(executor.map(f, models), total=len(models), desc='Indexing metamodels'):
                if result is not None:
                    if result[0]:
                        _, file, dataset, metamodel = result
                        insert_ok(conn, file, dataset, metamodel)
                    else:
                        _, file, dataset = result
                        insert_erroneous(conn, file, dataset)
                    conn.commit()

def find_and_index_metamodels_multiprocess(db, root, dataset, skip=True):
    models = glob.glob(os.path.join(root, "**", "*.ecore"), recursive=True)
    f = Indexer2(db, dataset, skip)
    # This is messed up.
    # If ThreadPool is used, then something leaks memory, leading to 24GiB memory usage with 5 of 6 GiB SWAP.
    # If ProcessPool is used, then no leak, but you can't serialize the metamodel. 
    # I assume the problem has to do something with PyEcore. (Code seems to use a gloabl package registry.) 
    with ProcessPoolExecutor() as executor:
        with sqlite3.connect(db) as conn:
            for result in tqdm(executor.map(f, models), total=len(models), desc='Indexing metamodels'):
                if result is not None:
                    if result[0]:
                        _, file, dataset, info, elements = result
                        insert_ok2(conn, file, dataset, info, elements)
                    else:
                        _, file, dataset = result
                        insert_erroneous(conn, file, dataset)
                    conn.commit()


def indexAll(roots, db, skip_dataset=True, skip_model=True):
    with sqlite3.connect(db, timeout=30) as conn:
        conn.execute("PRAGMA journal_mode=WAL;")  # harmless if already WAL
    for root in roots:
        dataset_name = os.path.basename(os.path.normpath(root))
        if not (skip_dataset and dataset_exists(db, dataset_name)):
            print(f"Parse dataset {dataset_name}")
            find_and_index_metamodels_multiprocess(db, root, dataset_name, skip_model)
            #find_and_index_metamodels_multithread(db, root, dataset_name, skip_model)
        else:
            print(f"Skipping dataset {dataset_name} (entry from this dataset exists)")

# Runnable version to look for new metamodels in folders and insert them to the database
def main():
    args = parser.parse_args()
    db = args.db
    folders = args.metamodels_datasets.split(",")
    indexAll(folders,db, False, True)

if __name__ == "__main__":
    main()