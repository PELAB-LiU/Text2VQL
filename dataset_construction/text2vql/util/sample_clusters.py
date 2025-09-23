import sqlite3
import random
import shutil
from pathlib import Path

def clear_samples_db(conn):
    cursor = conn.cursor()
    cursor.execute("DELETE FROM samples")
    conn.commit()

def get_clusters(conn):
    cursor = conn.cursor()
    cursor.execute("SELECT DISTINCT cluster FROM clusters WHERE cluster>0")
    clusters = [row[0] for row in cursor.fetchall()]
    return clusters

def get_clusters_large(conn, minsize):
    cursor = conn.cursor()
    cursor.execute("SELECT DISTINCT c.cluster FROM clusters c JOIN metamodels m ON c.model = m.model WHERE c.cluster > 0 AND m.elements >= ?", (minsize,))
    clusters = [row[0] for row in cursor.fetchall()]
    return clusters

def get_models_in_cluster(conn, cluster_id):
    cursor = conn.cursor()
    cursor.execute("SELECT model FROM clusters WHERE cluster = ?", (cluster_id,))
    models = [row[0] for row in cursor.fetchall()]
    return models

def save(conn, data, folder):
    cursor = conn.cursor()
    dst = Path(folder)
    dst.mkdir(parents=True, exist_ok=True)  

    clear_samples_db(conn)
    old_files = list(dst.glob("*.ecore"))
    for ecore_file in old_files:
        try:
            ecore_file.unlink()
        except Exception as e:
            print(f"Failed to delete {ecore_file}: {e}")

    for model in data:
        path,id = model
        cursor.execute("INSERT OR REPLACE INTO samples (model, cluster) VALUES (?, ?)", model)
        shutil.copy(path, dst / (f"{id}_" + Path(path).name))

    conn.commit()
    

def sample_clusters(conn, count, minsize):
    #clusters = get_clusters(conn)
    clusters = get_clusters_large(conn, minsize)
    samples = random.sample(clusters,min(count, len(clusters)))

    selected = []
    for cluster_id in samples:
        candidates = get_models_in_cluster(conn, cluster_id)
        model = random.choice(candidates)
        selected.append((model,cluster_id))
    
    return selected
    
def sample(db, count, out="metamodels/1-sample/", minsize=30):
    with sqlite3.connect(db) as conn: 
        data = sample_clusters(conn, count, minsize)
        save(conn, data, out)
