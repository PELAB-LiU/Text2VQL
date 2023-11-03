import argparse
import glob
import os
import sqlite3
import time

import openai
from tqdm import tqdm

from seed_sample import SEED_METAMODEL, PAIRS
from text2vql.metamodel import MetaModel
from text2vql.postprocessor import postprocess_nl_queries
from text2vql.template import get_formatted_nl_query, get_instruction_nl_queries

openai.api_key = os.getenv("OPENAI_API_KEY")


def save_to_db(db, metamodel, pairs):
    conn = sqlite3.connect(db)
    cursor = conn.cursor()

    # define the data to be inserted as a tuple
    tuple_metamodel = (metamodel.path,
                       metamodel.path,
                       str(metamodel.domain))

    # execute the insert statement
    cursor.execute("INSERT INTO metamodels (id, path, domain) VALUES (?, ?, ?)", tuple_metamodel)

    for i, (nl, q) in enumerate(pairs):
        tuple_query = (nl,
                       q,
                       metamodel.path)
        cursor.execute("INSERT INTO pairs (nl, pattern, metamodel) VALUES (?, ?, ?)",
                       tuple_query)
    conn.commit()
    conn.close()


def call_gpt(seed_metamodel, seed_nl_queries, metamodel, requested_queries=10, max_tokens=3000,
             temperature=0.4, model="gpt-3.5-turbo", tries=3):
    seed_queries = []
    for j, (q, nl) in enumerate(seed_nl_queries):
        seed_queries.append(get_formatted_nl_query(j + 1, nl, q))
    seed_queries = '\n'.join(seed_queries)
    instruction = get_instruction_nl_queries(seed_metamodel.get_metamodel_info(), seed_queries, requested_queries,
                                             metamodel.get_metamodel_info())
    print(f'Trying to generate {requested_queries} queries for metamodel {metamodel.path}')
    for _ in range(tries):
        try:
            response = openai.ChatCompletion.create(
                model=model,
                messages=[{"role": "user", "content": instruction}],
                max_tokens=max_tokens,
                temperature=temperature
            )
            response = response['choices'][0]['message']['content']
            generated_pairs = postprocess_nl_queries(response)
            break
        except:
            generated_pairs = []
            time.sleep(5)
    return generated_pairs


def get_metamodels(dataset_folder, threshold_size=30):
    metamodels = []
    # for each *.ecore
    for file in tqdm(glob.glob(os.path.join(dataset_folder, "*.ecore")), desc="Parsing metamodels"):
        try:
            metamodel = (MetaModel(file, int(file.split('_')[1])))
            info = metamodel.get_metamodel_info()
            if len(info.strip()) == 0:
                continue
            metamodels.append(metamodel)
        except:
            continue
    metamodels = [m for m in metamodels if m.number_of_elements() <= threshold_size]
    return metamodels


def main(args):
    metamodels = get_metamodels(args.metamodels_dataset)

    candidates = []
    for i in range(10):
        aux = [m for m in metamodels if m.domain == i]
        if aux:
            candidates += aux[0:5]
    for m in tqdm(candidates, desc="Generating dataset"):
        output_pairs = call_gpt(SEED_METAMODEL, PAIRS, m)
        save_to_db(args.db, m, output_pairs)


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='Generate dataset by calling GPT')
    parser.add_argument('--db', type=str, default='dataset.db', help='database file')
    parser.add_argument('--metamodels_dataset', type=str, default='ecore555',
                        help='metamodels dataset folder')

    args = parser.parse_args()
    main(args)
