import glob
import os
import pandas as pd
from tqdm import tqdm
from util.metamodel import MetaModel

def loadDomains(folder, dataset):
    """
    Scans a folder recursively for .ecore files,
    and returns a DataFrame with columns ['Path', 'Dataset'].

    Parameters
    ----------
    folder : str
        Root folder to search for .ecore files.
    dataset : str
        Dataset label to assign to each found file.

    Returns
    -------
    pandas.DataFrame
        DataFrame with columns Path (str) and Dataset (str).
    """
    metamodels_data = []

    for file in tqdm(
        glob.glob(os.path.join(folder, "**", "*.ecore"), recursive=True),
        desc="Parsing metamodels"
    ):
        try:
            metamodel = MetaModel(file)
            info = metamodel.get_metamodel_info()
            if len(info.strip()) == 0:
                continue
            # Store the file path and dataset name
            metamodels_data.append({"Path": file, "Dataset": dataset})
        except Exception:
            # Skip files that cause errors
            continue

    return pd.DataFrame(metamodels_data, columns=["Path", "Dataset"])

def loadAllDomains(folders, cache=None):
    all_dfs = []

    if cache is not None and not os.path.exists(cache):
        os.makedirs(cache)

    for folder in folders:
        dataset_name = os.path.basename(os.path.normpath(folder))
        cached_csv_path = None
        if cache is not None:
            # Use a CSV file named after the dataset (folder name) with safe filename
            safe_name = dataset_name.replace(" ", "_") + ".csv"
            cached_csv_path = os.path.join(cache, safe_name)

        if cached_csv_path and os.path.isfile(cached_csv_path):
            # Load from cache CSV
            print(f"Used existing CSV cache: {os.path.abspath(cached_csv_path)}")
            df = pd.read_csv(cached_csv_path)
        else:
            # Load fresh and cache if needed
            df = loadDomains(folder, dataset_name)
            if cached_csv_path is not None:
                df.to_csv(cached_csv_path, index=False)
                print(f"Created and saved CSV cache: {os.path.abspath(cached_csv_path)}")

        all_dfs.append(df)

    if all_dfs:
        return pd.concat(all_dfs, ignore_index=True)
    else:
        return pd.DataFrame(columns=["Path", "Dataset"])
