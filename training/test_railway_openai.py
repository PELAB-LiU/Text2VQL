import argparse
import os
import random
import re

import openai
import pandas as pd
from tqdm import tqdm

from seed_metamodels.seed_sample_yakindu import FIND_PAIRS, OR_PAIRS, TYPE_PAIRS, NORMAL_PAIRS, AGG_PAIRS, NOT_PAIRS, \
    SEED_METAMODEL
from text2vql.metamodel import MetaModel
from text2vql.template import get_formatted_nl_query

random.seed(1234)

openai.api_key = os.getenv("OPENAI_API_KEY")

INSTRUCTION_NL_QUERY_ZS = """Given the following meta-model:
{new_metamodel}

Write the query in Viatra Query Language that satisfies the following natural language description:
{nl_description}
The query must have the following header. You can use auxiliary patterns if needed. The query must be written between ```viatraql and ```.
{header}
"""

INSTRUCTION_NL_QUERY_HEADER = """Given the following meta-model:
{example_metamodel}
Some example queries in Viatra Query Language are:
{example_queries}

Now given the following meta-model:
{new_metamodel}

Write the query in Viatra Query Language that satisfies the following natural language description:
{nl_description}
The query must have the following header. You can use auxiliary patterns if needed.
{header}
"""

RAILWAY_METAMODEL = MetaModel('test_metamodel/railway.ecore')
test_dataset = pd.read_csv('test_metamodel/test_queries.csv', sep=',')


def get_seed_queries(mode):
    if mode == 'one':
        seed_queries = []

        for j, (q, nl) in enumerate(
                [FIND_PAIRS[0], OR_PAIRS[0], TYPE_PAIRS[0], NORMAL_PAIRS[0], AGG_PAIRS[0], NOT_PAIRS[0]]):
            seed_queries.append(get_formatted_nl_query(j + 1, nl, q))
        seed_queries = '\n'.join(seed_queries)
    else:
        seed_queries = []
        sampled_queries = []
        for pairs in [FIND_PAIRS, OR_PAIRS, TYPE_PAIRS, NORMAL_PAIRS, AGG_PAIRS, NOT_PAIRS]:
            sampled_queries += random.sample(pairs, 1)

        for j, (q, nl) in enumerate(sampled_queries):
            seed_queries.append(get_formatted_nl_query(j + 1, nl, q))
        seed_queries = '\n'.join(seed_queries)
    return seed_queries


def main(args):
    for i in range(args.times):
        outputs = []

        for _, row in tqdm(test_dataset.iterrows(), desc='Iterating test dataset', total=test_dataset.shape[0]):
            nl_description = row['nl']
            header = row['header']

            instruction_zs_header = INSTRUCTION_NL_QUERY_ZS.format(
                new_metamodel=RAILWAY_METAMODEL.get_metamodel_info(),
                nl_description=nl_description,
                header=header
            )
            instruction_fs_header = INSTRUCTION_NL_QUERY_HEADER.format(
                example_metamodel=SEED_METAMODEL.get_metamodel_info(),
                example_queries=get_seed_queries(args.mode),
                new_metamodel=RAILWAY_METAMODEL.get_metamodel_info(),
                nl_description=nl_description,
                header=header
            )

            instruction = instruction_zs_header if args.prompt_type == 'zs' else instruction_fs_header

            response = openai.ChatCompletion.create(
                model="gpt-3.5-turbo",
                messages=[{"role": "system", "content": "You are an expert in Viatra Query Language."},
                          {"role": "user", "content": instruction}],
                max_tokens=3000,
                temperature=args.temperature
            )
            full_response = response['choices'][0]['message']['content']

            query_pattern = re.compile(r'```viatraql\n(.*?)\n```', re.DOTALL)
            out_query = re.findall(query_pattern, full_response)[0]

            outputs.append(out_query)

        test_dataset[f'chatgpt_{args.prompt_type}_{i}'] = outputs

    test_dataset.to_csv(f'chatgpt_{args.prompt_type}_{args.mode}.csv', index=False)


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='ChatGPT baseline')
    parser.add_argument('--prompt_type', type=str, default='zs', choices=['zs', 'fs'])
    parser.add_argument('--times', type=int, default=5)
    parser.add_argument('--temperature', type=float, default=0.4)
    parser.add_argument('--mode', default='one', choices=['one', 'random'])

    args = parser.parse_args()
    main(args)
