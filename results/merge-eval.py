import pandas as pd

import os
import argparse

def merge(basename):
    ai = pd.read_csv("ai/{bn}.csv".format(bn=basename)).set_index('id')
    res = pd.read_csv("eval/{bn}_eval.csv".format(bn=basename)).set_index('id')
    merded = ai.join(res)
    merded.to_csv("merged/{bn}_full.csv".format(bn=basename))

for filename in os.listdir(f"{os.getcwd()}/ai"):
    basename = os.path.splitext(filename)[0]
    merge(basename)