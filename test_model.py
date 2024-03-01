import torch
from peft import PeftModel
from tqdm import tqdm
from transformers import AutoModelForCausalLM, AutoTokenizer

# from test_model_ff import PROMPT_WITH_HEADER_CODE
from text2vql.metamodel import MetaModel
from train import PROMPT_CODE

PROMPT_WITH_HEADER_CODE = """{metamodel}
//{nl}
{header}
"""

PROMPT_WITH_HEADER = """Below there is the specification of a meta-model
{metamodel}
Write the patterns using the Viatra Query Language of the following natural language specification:
{nl}
Patterns:
{header}
"""

PATH = "codegen2-3_7b/checkpoint-273"
BASE_MODEL = "Salesforce/codegen2-3_7B"
METAMODEL = MetaModel('seed_metamodels/family.ecore')
model = AutoModelForCausalLM.from_pretrained(BASE_MODEL,
                                             trust_remote_code=True,
                                             torch_dtype=torch.float16).cuda()
model = PeftModel.from_pretrained(model, PATH).eval()
tokenizer = AutoTokenizer.from_pretrained(PATH)

prompt1 = PROMPT_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                             nl="Family that have two members whose names have 5 characters")
prompt2 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                         nl="Person that is brother of himself",
                                         header="pattern brotherOfHimself(person : Person) {")
prompt3 = PROMPT_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                             nl="People whose uncle and brother are the same person")
prompt4 = PROMPT_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                             nl="All people whose fathers are old (that are greater then 60)")
prompt5 = PROMPT_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                             nl="Retrieve all people whose father names are Jack Sparrow")
prompt6 = PROMPT_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                             nl="Retrieve all pairs of people that share fathers")
prompt7 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                         nl="Retrieve all pairs of people that have the same father",
                                         header="pattern peopleSameFathers(person1 : Person, person2 : Person) {")
prompt8 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                         nl="Retrieve all people whose that have either one father, or have one uncle whose age is 29",
                                         header="pattern peopleFatherOrUncle(person : Person) {")
prompts = [prompt1, prompt2, prompt3, prompt4, prompt5, prompt6, prompt7, prompt8]

for prompt in tqdm(prompts, desc='main loop'):
    print(prompt)
    sample = tokenizer([prompt], return_tensors="pt")

    with torch.no_grad():
        generated_sequences = model.generate(
            input_ids=sample["input_ids"].cuda(),
            attention_mask=sample["attention_mask"].cuda(),
            do_sample=False,
            max_new_tokens=128,
            num_return_sequences=10,
            num_beams=10,
            pad_token_id=tokenizer.eos_token_id,
            eos_token_id=tokenizer.eos_token_id
        )

    generated_sequences = generated_sequences.cpu().numpy()
    generated_new_tokens = generated_sequences[:, sample["input_ids"].shape[1]:]

    for new_tokens in generated_new_tokens[0:1]:
        generated = tokenizer.decode(generated_new_tokens[0], skip_special_tokens=True)
        print(generated)
    print('-' * 100)
