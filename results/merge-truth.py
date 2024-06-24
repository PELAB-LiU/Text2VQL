import pandas as pd


ai = pd.read_csv("../dataset_construction/test_metamodel/test_queries.csv").set_index('id')
res = pd.read_csv("profiles/profiles_truth_raw.csv").set_index('id')
merded = ai.join(res)
merded.to_csv("profiles/profiles_truth.csv")

