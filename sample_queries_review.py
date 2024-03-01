import argparse
import random
import sqlite3

import pandas as pd


def main(args):
    random.seed(args.seed)

    cnx = sqlite3.connect(args.db)
    df = pd.read_sql_query("SELECT * FROM pairs", cnx)
    df_ids = pd.read_csv(args.valid_ids, header=None, names=['id'])
    list_valid_ids = list(df_ids['id'])

    sample = set(random.sample(list_valid_ids, args.n))

    df = df[df['id'].isin(sample)]
    df.to_csv('sample.csv')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--db', type=str, default='dataset.db', help='database file')
    parser.add_argument('--valid_ids', type=str, default='valid_ids.txt')
    parser.add_argument('--n', type=int, default=10)
    parser.add_argument('--seed', default=123, type=int)
    args = parser.parse_args()
    main(args)
