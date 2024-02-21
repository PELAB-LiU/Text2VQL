import random
import time

import openai

from generate_dataset import call_gpt
from seed_metamodels.seed_sample_yakindu import ALL_PAIRS, SEED_METAMODEL, FIND_PAIRS, OR_PAIRS, TYPE_PAIRS
from text2vql.metamodel import MetaModel
from text2vql.postprocessor import postprocess_nl_queries
from text2vql.template import get_formatted_nl_query, get_instruction_nl_queries

random.seed(123)

pairs = random.sample(ALL_PAIRS, 5)
seed_queries = []
for j, (q, nl) in enumerate(OR_PAIRS):
    seed_queries.append(get_formatted_nl_query(j + 1, nl, q))
seed_queries = '\n'.join(seed_queries)
instruction = get_instruction_nl_queries(SEED_METAMODEL.get_metamodel_info(), seed_queries, 5,
                                         MetaModel('seed_metamodels/relational.ecore').get_metamodel_info())

call_gpt(SEED_METAMODEL, OR_PAIRS,
         MetaModel('seed_metamodels/relational.ecore').get_metamodel_info(),
         'test', requested_queries=5, max_tokens=3000,
         temperature=0.4, model="gpt-3.5-turbo", tries=3, depth=2, verbose=True,
         include_metamodel_prompt=False)
