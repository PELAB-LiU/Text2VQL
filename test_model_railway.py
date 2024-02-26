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

PATH = "models/codegen2-3_7b/checkpoint-1228"
BASE_MODEL = "Salesforce/codegen2-3_7B"
METAMODEL = MetaModel('test_metamodel/railway.ecore')
model = AutoModelForCausalLM.from_pretrained(BASE_MODEL,
                                             trust_remote_code=True,
                                             torch_dtype=torch.float16,
                                             device_map="auto")

model = PeftModel.from_pretrained(model, PATH).eval()

tokenizer = AutoTokenizer.from_pretrained(PATH)


prompt2 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                         nl="Retrieve all segments whose lengths are less or equal than zero",
                                         header="pattern posLength(segment : Segment){")

prompt4 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                         nl="All switches that are not monitored by any sensor",
                                         header="pattern switchMonitored(sw : Switch){")

prompt5 = PROMPT_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                             nl="Retrieve all elements")

prompt6 = PROMPT_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                             nl="Retrieve all elements thar are sensors")



prompts = [prompt2, prompt4, prompt5, prompt6]

for prompt in tqdm(prompts, desc='main loop'):
    print(prompt)
    sample = tokenizer([prompt], return_tensors="pt")

    with torch.no_grad():
        generated_sequences = model.generate(
            input_ids=sample["input_ids"].cuda(),
            attention_mask=sample["attention_mask"].cuda(),
            do_sample=False,
            max_new_tokens=256,
            num_return_sequences=5,
            num_beams=5,
            pad_token_id=tokenizer.eos_token_id,
            eos_token_id=tokenizer.eos_token_id
        )

    generated_sequences = generated_sequences.cpu().numpy()
    generated_new_tokens = generated_sequences[:, sample["input_ids"].shape[1]:]

    for new_tokens in generated_new_tokens[0:1]:
        generated = tokenizer.decode(generated_new_tokens[0], skip_special_tokens=True)
        print(generated)
    print('-' * 100)
