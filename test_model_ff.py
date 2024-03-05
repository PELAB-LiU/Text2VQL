import torch
from transformers import AutoModelForCausalLM, AutoTokenizer

from text2vql.metamodel import MetaModel
from train import PROMPT_CODE

PROMPT_WITH_HEADER_CODE = """{metamodel}
//{nl}
{header}
"""

PATH = "codegen-mono-350-vql2text/checkpoint-55"
METAMODEL = MetaModel('seed_metamodels/family.ecore')
model = AutoModelForCausalLM.from_pretrained(PATH,
                                             trust_remote_code=True).cuda()
tokenizer = AutoTokenizer.from_pretrained(PATH)

prompt1 = PROMPT_CODE.format(metamodel=METAMODEL.get_metamodel_info(), nl="Family that have two members whose names have 5 characters")
prompt2 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                         nl="Person that is brother of himself",
                                         header="pattern brotherOfHimself(person : Person) {")
prompt3 = PROMPT_CODE.format(metamodel=METAMODEL.get_metamodel_info(), nl="People whose uncle and brother are the same person")
prompt4 = PROMPT_CODE.format(metamodel=METAMODEL.get_metamodel_info(), nl="All people whose fathers are old (that are greater then 60)")
prompt5 = PROMPT_CODE.format(metamodel=METAMODEL.get_metamodel_info(), nl="Retrieve all people whose father names are Jack Sparrow")
prompt6 = PROMPT_CODE.format(metamodel=METAMODEL.get_metamodel_info(), nl="Retrieve all pairs of people that share fathers")
prompt7 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                         nl="Retrieve all pairs of people that have the same father",
                                         header="pattern peopleSameFathers(person1 : Person, person2 : Person) {")
prompts = [prompt1, prompt2, prompt3, prompt4, prompt5, prompt6, prompt7]

for prompt in prompts:
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

    for new_tokens in generated_new_tokens[0:1]:
        generated = tokenizer.decode(generated_new_tokens[0], skip_special_tokens=True)
        print(generated)
    print('-'*100)