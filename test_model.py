import torch
from peft import PeftModel
from transformers import AutoModelForCausalLM, AutoTokenizer

from text2vql.metamodel import MetaModel
from train import PROMPT

PROMPT_WITH_HEADER = """Below there is the specification of a meta-model
{metamodel}
Write the patterns using the Viatra Query Language of the following natural language specification:
{nl}
Patterns:
{header}
"""

PATH = "bloom1b1/checkpoint-278"
BASE_MODEL = "bigscience/bloom-1b1"
METAMODEL = MetaModel('test_metamodels/family.ecore')
model = AutoModelForCausalLM.from_pretrained(BASE_MODEL,
                                             trust_remote_code=True,
                                             torch_dtype=torch.float16).cuda()
model = PeftModel.from_pretrained(model, PATH).eval()
tokenizer = AutoTokenizer.from_pretrained(PATH)

#prompt = PROMPT.format(metamodel=METAMODEL.get_metamodel_info(), nl="Family that have two members whose names have 5 characters")
prompt = PROMPT_WITH_HEADER.format(metamodel=METAMODEL.get_metamodel_info(),
                                   nl="People that are brothers of themselves",
                                   header="pattern brothersOfThemselves(person : Person) {")
print(prompt)
sample = tokenizer([prompt], return_tensors="pt")

with torch.no_grad():
    generated_sequences = model.generate(
        input_ids=sample["input_ids"].cuda(),
        attention_mask=sample["attention_mask"].cuda(),
        do_sample=False,
        max_new_tokens=128,
        num_return_sequences=10,
        num_beams=10
    )


generated_sequences = generated_sequences.cpu().numpy()
generated_new_tokens = generated_sequences[:, sample["input_ids"].shape[1]:]

for new_tokens in generated_new_tokens[0:5]:
    generated = tokenizer.decode(generated_new_tokens[0], skip_special_tokens=True)
    print('-'*100)
    print(generated)
