import argparse
import random
from collections import defaultdict

import pandas as pd
import torch
from tqdm import tqdm
from transformers import AutoModelForCausalLM, AutoTokenizer
from transformers.trainer_utils import set_seed

from seed_metamodels.seed_sample_yakindu import FIND_PAIRS, OR_PAIRS, TYPE_PAIRS, NORMAL_PAIRS, SEED_METAMODEL, \
    AGG_PAIRS, NOT_PAIRS
from text2vql.metamodel import MetaModel

set_seed(1234)

INSTRUCTION_NL_QUERY_ZS = """{new_metamodel}

//{nl_description}
{header}
"""

INSTRUCTION_NL_QUERY_HEADER = """{example_metamodel}

{example_queries}

{new_metamodel}

//{nl_description}
{header}
"""

test_dataset = pd.read_csv('test_metamodel/test_queries.csv', sep=',')
RAILWAY_METAMODEL = MetaModel('test_metamodel/railway.ecore')


def get_seed_queries(mode):
    seed_queries = []

    if mode == 'one':
        queries = [FIND_PAIRS[0], OR_PAIRS[0], TYPE_PAIRS[0], NORMAL_PAIRS[0], AGG_PAIRS[0], NOT_PAIRS[0]]
    elif mode == 'random':
        queries = []
        for pairs in [FIND_PAIRS, OR_PAIRS, TYPE_PAIRS, NORMAL_PAIRS, AGG_PAIRS, NOT_PAIRS]:
            queries += random.sample(pairs, 1)
    else:
        raise ValueError('Not supported')

    for j, (q, nl) in enumerate(queries):
        str_q_nl = '//' + nl + '\n' + q + '\n//end of main pattern'
        seed_queries.append(str_q_nl)
    seed_queries = '\n'.join(seed_queries)
    return seed_queries


def main(args):
    model = AutoModelForCausalLM.from_pretrained(args.base_model,
                                                 trust_remote_code=True,
                                                 torch_dtype=torch.float16,
                                                 device_map="auto")
    tokenizer = AutoTokenizer.from_pretrained(args.base_model)
    outputs = defaultdict(list)

    for _, row in tqdm(test_dataset.iterrows(), desc='Iterating test dataset', total=test_dataset.shape[0]):
        nl_description = row['nl']
        header = row['header']

        prompt = INSTRUCTION_NL_QUERY_HEADER.format(
            example_metamodel=SEED_METAMODEL.get_metamodel_info(),
            example_queries=get_seed_queries(args.mode),
            new_metamodel=RAILWAY_METAMODEL.get_metamodel_info(),
            nl_description=nl_description,
            header=header
        )

        sample = tokenizer([prompt], return_tensors="pt")

        with torch.no_grad():
            generated_sequences = model.generate(
                input_ids=sample["input_ids"].cuda(),
                attention_mask=sample["attention_mask"].cuda(),
                do_sample=True,
                max_new_tokens=512,
                num_return_sequences=args.times,
                temperature=args.temperature,
                pad_token_id=tokenizer.eos_token_id,
                eos_token_id=tokenizer.eos_token_id
            )

        generated_sequences = generated_sequences.cpu().numpy()
        generated_new_tokens = generated_sequences[:, sample["input_ids"].shape[1]:]

        for k, new_tokens in enumerate(generated_new_tokens):
            generated = tokenizer.decode(new_tokens, skip_special_tokens=True)
            generated = generated.split('//end of main pattern')[0]
            outputs[k].append(header + '\n' + generated)
            print(header + '\n' + generated)
            print('-' * 100)

    for k, v in outputs.items():
        test_dataset[f'{k}_output'] = outputs[k]
    test_dataset.to_csv(f'{args.base_model.replace("/", "-")}_fs_{args.mode}.csv', index=False)
    print(f'Saved {args.base_model.replace("/", "-")}_fs_{args.mode}.csv')


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='Run fs OS models')
    parser.add_argument('--times', type=int, default=5)
    parser.add_argument('--temperature', type=float, default=0.4)
    parser.add_argument('--base_model', default="deepseek-ai/deepseek-coder-6.7b-base")
    parser.add_argument('--mode', default="one", choices=["one", "random"])

    args = parser.parse_args()
    main(args)
