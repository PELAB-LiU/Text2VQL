import argparse
import sqlite3

import pandas as pd
from datasets import Dataset

from text2vql.metamodel import MetaModel


def main(args):
    # load sql table to pandas dataframe
    cnx = sqlite3.connect(args.db)
    df = pd.read_sql_query("SELECT * FROM pairs", cnx)
    contexts = []
    for i, row in df.iterrows():
        metamodel_path = row['metamodel']
        # TODO: id and path are the same
        metamodel = MetaModel(metamodel_path)
        contexts.append(metamodel.get_metamodel_info())
    df['metamodel_definition'] = contexts
    dataset = Dataset.from_pandas(df)
    dataset.push_to_hub(args.hf_dataset, private=True)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--db', type=str, default='dataset.db', help='database file')
    parser.add_argument('--hf_dataset', type=str, required=True)
    args = parser.parse_args()
    main(args)
