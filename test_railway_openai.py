import os
import random

import openai

from seed_metamodels.seed_sample_yakindu import ALL_PAIRS, SEED_METAMODEL, FIND_PAIRS, OR_PAIRS, TYPE_PAIRS
from text2vql.metamodel import MetaModel
from text2vql.template import get_formatted_nl_query

openai.api_key = os.getenv("OPENAI_API_KEY")

INSTRUCTION_NL_QUERY = """Given the following meta-model:
{example_metamodel}
Some example queries in Viatra Query Language are:
{example_queries}

Now given the following meta-model:
{new_metamodel}

Write the query in Viatra Query Language that satisfies the following natural language description:
{nl_description}
"""
random.seed(123)

seed_queries = []
for j, (q, nl) in enumerate(random.sample(ALL_PAIRS, 5)):
    seed_queries.append(get_formatted_nl_query(j + 1, nl, q))
seed_queries = '\n'.join(seed_queries)

RAILWAY_METAMODEL = MetaModel('test_metamodel/railway.ecore')
nl_description = "Return two distinct routes that require two sensors that monitor two elements connected between them. The first rounte has a semaphore in its exist but the second does not have it in the entry."
instruction = INSTRUCTION_NL_QUERY.format(
    example_metamodel=SEED_METAMODEL.get_metamodel_info(),
    example_queries=seed_queries,
    new_metamodel=RAILWAY_METAMODEL.get_metamodel_info(),
    nl_description=nl_description
)

response = openai.ChatCompletion.create(
                    model="gpt-3.5-turbo",
                    messages=[{"role": "system", "content": "You are an expert in Viatra Query Language."},
                              {"role": "user", "content": instruction}],
                    max_tokens=3000,
                    temperature=0.7
                )
full_response = response['choices'][0]['message']['content']
print(full_response)