import random
from tqdm import tqdm
import pandas as pd

from util.metamodel import MetaModel

def jaccard(x, y):
    intersection_cardinality = len(x.intersection(y))
    union_cardinality = len(x.union(y))
    return intersection_cardinality / float(union_cardinality)

def select_dissimilar_models(models, provided_models, n=600, threshold=0.7, seed=None):
    """
    Randomly selects up to n models from 'models' that are not similar to each other
    or to any model in 'provided_models'.

    Parameters
    ----------
    models : pd.DataFrame
        DataFrame with a 'Path' column containing candidate models.
    n : int
        Number of models to select.
    provided_models : pd.DataFrame
        DataFrame with a 'Path' column containing models already provided.
    threshold : float, optional
        Similarity threshold above which models are considered too similar.
    seed : int, optional
        Random seed for reproducibility.

    Returns
    -------
    pd.DataFrame
        DataFrame containing selected model paths.
    """
    # Convert to list for indexing and shuffle
    candidate_paths = list(models['Path'])
    if seed is not None:
        random.seed(seed)
    random.shuffle(candidate_paths)

    provided_paths = list(provided_models['Path'])

    # Cache for loaded concepts
    concepts_cache = {}

    def get_concepts(path):
        if path not in concepts_cache:
            mm = MetaModel(path)
            concepts_cache[path] = set(c.lower() for c in mm.get_elements())
        return concepts_cache[path]

    # Start with provided models in selected set (for similarity exclusion)
    selected_paths = set(provided_paths)

    # Output list of chosen models from 'models'
    chosen = []

    for path in tqdm(candidate_paths, desc="Selecting dissimilar models"):
        if len(chosen) >= n:
            break

        too_similar = False
        cand_concepts = get_concepts(path)

        for sel_path in selected_paths:
            sim = jaccard(cand_concepts, get_concepts(sel_path))
            if sim > threshold:
                too_similar = True
                break

        if not too_similar:
            chosen.append(path)
            selected_paths.add(path)

    return pd.DataFrame(chosen, columns=['Path'])
