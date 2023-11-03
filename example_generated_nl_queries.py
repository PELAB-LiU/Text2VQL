import openai

from seed_sample import SEED_SAMPLE
from test_text2vql import METAMODEL2
from text2vql.postprocessor import postprocess_nl_queries
from text2vql.template import get_formatted_nl_query, get_instruction_nl_queries

queries = []
for j, (q, nl) in enumerate(SEED_SAMPLE[1]):
    queries.append(get_formatted_nl_query(j + 1, nl, q))
queries = '\n'.join(queries)
instruction = get_instruction_nl_queries(SEED_SAMPLE[0].get_metamodel_info(), queries, 10, METAMODEL2.get_metamodel_info())
print(instruction)

# example request
response = openai.ChatCompletion.create(
            model="gpt-4",
            messages=[{"role": "user", "content": instruction}],
            max_tokens=2048,
            temperature=0.4
        )
# response = response["choices"][0]["text"]
response = response['choices'][0]['message']['content']

generated_pairs = postprocess_nl_queries(response)
for nl, q in generated_pairs:
    print("NL:", nl)
    print(q)

# Notes:
# problem with the negation (previously defined queries?)
#