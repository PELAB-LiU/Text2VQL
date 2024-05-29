import random

import torch
from peft import PeftModel
from tqdm import tqdm
from transformers import AutoModelForCausalLM, AutoTokenizer

from seed_metamodels.seed_sample_yakindu import SEED_METAMODEL, ALL_PAIRS
# from test_model_ff import PROMPT_WITH_HEADER_CODE
from text2vql.metamodel import MetaModel
from text2vql.template import get_formatted_nl_query
from train import PROMPT_CODE


BASE_MODEL = "bigcode/starcoder"
METAMODEL = MetaModel('test_metamodel/railway.ecore')
model = AutoModelForCausalLM.from_pretrained(BASE_MODEL,
                                             trust_remote_code=True,
                                             torch_dtype=torch.float16,
                                             device_map="auto")
tokenizer = AutoTokenizer.from_pretrained(BASE_MODEL)

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
nl_description = "Retrieve all segments whose lengths are less or equal than zero"
instruction = INSTRUCTION_NL_QUERY.format(
    example_metamodel=SEED_METAMODEL.get_metamodel_info(),
    example_queries=seed_queries,
    new_metamodel=RAILWAY_METAMODEL.get_metamodel_info(),
    nl_description=nl_description
)

prompts = [instruction]

for prompt in tqdm(prompts, desc='main loop'):
    print(prompt)
    sample = tokenizer([prompt], return_tensors="pt")

    with torch.no_grad():
        generated_sequences = model.generate(
            input_ids=sample["input_ids"].cuda(),
            attention_mask=sample["attention_mask"].cuda(),
            do_sample=True,
            max_new_tokens=256,
            # num_return_sequences=5,
            # num_beams=5,
            pad_token_id=tokenizer.eos_token_id,
            eos_token_id=tokenizer.eos_token_id
        )

    generated_sequences = generated_sequences.cpu().numpy()
    generated_new_tokens = generated_sequences[:, sample["input_ids"].shape[1]:]

    for new_tokens in generated_new_tokens[0:1]:
        generated = tokenizer.decode(generated_new_tokens[0], skip_special_tokens=True)
        print(generated)
    print('-' * 100)
