import argparse
import os
import sqlite3
import time

import openai
import pandas as pd
from tqdm import tqdm

from seed_metamodels.seed_sample_yakindu import SEED_METAMODEL, OR_PAIRS, NORMAL_PAIRS, FIND_PAIRS, TYPE_PAIRS
from text2vql.postprocessor import postprocess_nl_queries
from text2vql.template import get_formatted_nl_query, get_instruction_nl_queries

openai.api_key = os.getenv("OPENAI_API_KEY")


def save_to_db(db, metamodel, pairs):
    conn = sqlite3.connect(db)
    cursor = conn.cursor()

    for i, (nl, q) in enumerate(pairs):
        tuple_query = (nl,
                       q,
                       metamodel)
        cursor.execute("INSERT INTO pairs (nl, pattern, metamodel) VALUES (?, ?, ?)",
                       tuple_query)
    conn.commit()
    conn.close()


def call_gpt(seed_metamodel, seed_nl_queries, metamodel_des, metamodel_id, requested_queries=10, max_tokens=3000,
             temperature=0.4, model="gpt-3.5-turbo", tries=3):
    seed_queries = []
    for j, (q, nl) in enumerate(seed_nl_queries):
        seed_queries.append(get_formatted_nl_query(j + 1, nl, q))
    seed_queries = '\n'.join(seed_queries)
    instruction = get_instruction_nl_queries(seed_metamodel.get_metamodel_info(), seed_queries, requested_queries,
                                             metamodel_des)
    print(f'Trying to generate {requested_queries} queries for metamodel {metamodel_id}')
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


def get_metamodels(db, min_elements=30, max_elements=60):
    conn = sqlite3.connect(db)
    query = f"""select m.id, m.definition from representatives join metamodels m on representatives.id = m.id 
    where m.elements >= {min_elements} and m.elements <= {max_elements}"""
    df = pd.read_sql_query(query, conn)
    return df


def main(args):
    df = get_metamodels(args.db, args.min_elements, args.max_elements)
    df = df.sample(n=args.sample, random_state=42)
    for _, row in tqdm(df.iterrows(), desc='Generating dataset', total=df.shape[0]):
        output_pairs = call_gpt(SEED_METAMODEL, OR_PAIRS, row['definition'], row['id'], requested_queries=10)
        output_pairs += call_gpt(SEED_METAMODEL, NORMAL_PAIRS, row['definition'], row['id'], requested_queries=10)
        output_pairs += call_gpt(SEED_METAMODEL, FIND_PAIRS, row['definition'], row['id'], requested_queries=10)
        output_pairs += call_gpt(SEED_METAMODEL, TYPE_PAIRS, row['definition'], row['id'], requested_queries=10)
        save_to_db(args.db, row['id'], output_pairs)


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='Generate dataset by calling GPT')
    parser.add_argument('--db', type=str, default='dataset.db', help='database file')
    parser.add_argument('--max_elements', type=int, default=100, help='max number of elements in metamodel')
    parser.add_argument('--min_elements', type=int, default=30, help='min number of elements in metamodel')
    parser.add_argument('--sample', type=int, default=300, help='number of metamodels to sample')

    args = parser.parse_args()
    main(args)
