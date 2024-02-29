from generate_dataset import call_gpt
from seed_metamodels.seed_sample_yakindu import AGG_PAIRS, SEED_METAMODEL, FIND_PAIRS
from text2vql.metamodel import MetaModel

pairs = call_gpt(SEED_METAMODEL, FIND_PAIRS,
                 MetaModel('seed_metamodels/relational.ecore').get_metamodel_info(),
                 'test', requested_queries=5, max_tokens=3000,
                 temperature=0.4, model="gpt-3.5-turbo", tries=3, depth=2, verbose=False)

for i, (nl, q) in enumerate(pairs):
    print(f'Query {i + 1}')
    print(f'NL: {nl}')
    print(f'Pattern: {q}')
    print('--' * 50)
