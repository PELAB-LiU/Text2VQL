import argparse
import sqlite3

import pandas as pd
from datasets import Dataset
from tqdm import tqdm

from text2vql.metamodel import MetaModel


def main(args):
    # load sql table to pandas dataframe
    cnx = sqlite3.connect(args.db)
    df = pd.read_sql_query("SELECT * FROM pairs", cnx)
    df_ids = pd.read_csv(args.valid_ids, header=None, names=['id'])
    list_valid_ids = set(list(df_ids['id']))
    print(f'Number of valid queries {len(list_valid_ids)}')

    contexts = []
    for i, row in tqdm(df.iterrows()):
        metamodel_path = row['metamodel']
        # TODO: id and path are the same
        metamodel = MetaModel(metamodel_path)
        contexts.append(metamodel.get_metamodel_info())
    df['metamodel_definition'] = contexts
    df = df[df['id'].isin(list_valid_ids)]
    dataset = Dataset.from_pandas(df)

    dataset.to_json(args.output)
    print(dataset)

    if args.hf_dataset:
        dataset = dataset.train_test_split(test_size=0.2)
        dataset.push_to_hub(args.hf_dataset, private=True)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--db', type=str, default='dataset.db', help='database file')
    parser.add_argument('--hf_dataset', type=str, required=False)
    parser.add_argument('--valid_ids', type=str, default='valid_ids.txt')
    parser.add_argument('--output', type=str, default='final_dataset.jsonl')
    args = parser.parse_args()
    main(args)
