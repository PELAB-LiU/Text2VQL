from util.metamodel import MetaModel

import os
import argparse
import pandas as pd
from tqdm import tqdm


    
def register_pairs(pairs, similarities_csv):
    """
    Saves similarity pairs to a CSV file.
    If the file exists, appends new pairs (avoiding duplicates).
    """
    new_df = pd.DataFrame(pairs, columns=['m1', 'm2'])

    try:
        existing_df = pd.read_csv(similarities_csv)
        combined_df = pd.concat([existing_df, new_df], ignore_index=True)
        combined_df.drop_duplicates(subset=['m1', 'm2'], inplace=True)
    except FileNotFoundError:
        combined_df = new_df

    combined_df.to_csv(similarities_csv, index=False)
    return combined_df

def jaccard(x, y):
    intersection_cardinality = len(x.intersection(y))
    union_cardinality = len(x.union(y))
    return intersection_cardinality / float(union_cardinality)

def get_duplicates(paths, threshold, existing_pairs=None):
    """
    Compare metamodels, loading them only when needed and caching them in memory.
    """
    pairs = []
    metamodel_cache = {}

    for i, path1 in enumerate(tqdm(paths, desc='Computing similarities')):
        for path2 in paths[i+1:]:
            # Skip if already computed
            if existing_pairs is not None and ((path1, path2) in existing_pairs or (path2, path1) in existing_pairs):
                continue

            # Load metamodels lazily and cache
            if path1 not in metamodel_cache:
                mm1 = MetaModel(path1)
                metamodel_cache[path1] = set(c.lower() for c in mm1.get_elements())
            if path2 not in metamodel_cache:
                mm2 = MetaModel(path2)
                metamodel_cache[path2] = set(c.lower() for c in mm2.get_elements())

            # Compute similarity
            sim = jaccard(metamodel_cache[path1], metamodel_cache[path2])
            if sim > threshold:
                pairs.append((path1, path2))
    return pairs

def similarities(models, similarities_csv, threshold=0.7):
    """
    Compute similarities between models.
    Uses similarities_csv as cache to skip previously computed pairs.
    """
    paths = models['Path']

    # Load existing pairs if cache exists
    if os.path.isfile(similarities_csv):
        existing_df = pd.read_csv(similarities_csv)
        existing_pairs = set(zip(existing_df['m1'], existing_df['m2']))
        print(f"Loaded cached similarities from {os.path.abspath(similarities_csv)} ({len(existing_pairs)} pairs)")
    else:
        existing_pairs = set()

    # Compute only missing similarities
    pairs = get_duplicates(paths, threshold, existing_pairs=existing_pairs)
    result = register_pairs(pairs, similarities_csv)
    # Save new pairs to cache
    if pairs:
        print(f"Added {len(pairs)} new similarity pairs to {os.path.abspath(similarities_csv)}")
    else:
        print("No new pairs to compute — all similarities already cached.")
    
    return result
    

