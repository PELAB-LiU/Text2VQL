import argparse
import os
import random

import openai
import pandas as pd
from tqdm import tqdm

from finetune_openai import generate_input_message
from text2vql.metamodel import MetaModel

random.seed(1234)

openai.api_key = os.getenv("OPENAI_API_KEY")

RAILWAY_METAMODEL = MetaModel('test_metamodel/railway.ecore')
test_dataset = pd.read_csv('test_metamodel/test_queries.csv', sep=',')


def main(args):
    for i in range(args.times):
        outputs = []

        for _, row in tqdm(test_dataset.iterrows(), desc='Iterating test dataset', total=test_dataset.shape[0]):
            nl_description = row['nl']
            header = row['header']

            instruction = generate_input_message(RAILWAY_METAMODEL.get_metamodel_info(), nl_description, header)

            response = openai.ChatCompletion.create(
                model=args.model_id,
                messages=[{"role": "system", "content": "You are an expert in Viatra Query Language."},
                          {"role": "user", "content": instruction}],
                max_tokens=3000,
                temperature=args.temperature
            )
            out_query = response['choices'][0]['message']['content']
            print(out_query)
            outputs.append(out_query)

        test_dataset[f'chatgpt-FT_{i}'] = outputs

    test_dataset.to_csv(f'chatgpt-FT.csv', index=False)


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='ChatGPT fine-tuned')
    parser.add_argument('--model_id', type=str, default='ft:gpt-3.5-turbo-0125:link-ping-university::91xtmTMM')
    parser.add_argument('--times', type=int, default=5)
    parser.add_argument('--temperature', type=float, default=0.4)

    args = parser.parse_args()
    main(args)
