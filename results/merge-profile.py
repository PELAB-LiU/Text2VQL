import pandas as pd


ai = pd.read_csv("profiles/final_dataset.csv").set_index('id')
res = pd.read_csv("profiles/profiles_raw.csv").set_index('id')
merded = ai.join(res)
merded.to_csv("profiles/profiles.csv")

