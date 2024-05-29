import argparse
from collections import defaultdict

import pandas as pd
import torch
from peft import PeftModel
from tqdm import tqdm
from transformers import AutoModelForCausalLM, AutoTokenizer

# from test_model_ff import PROMPT_WITH_HEADER_CODE
from text2vql.metamodel import MetaModel

from transformers.trainer_utils import set_seed

set_seed(1234)

PROMPT_WITH_HEADER_CODE = """{metamodel}
//{nl}
{header}
"""

PATH = "models/deepseek-coder-mlength-1.5/checkpoint-921"
BASE_MODEL = "deepseek-ai/deepseek-coder-7b-base-v1.5"
# "deepseek-ai/deepseek-coder-6.7b-base"
METAMODEL = MetaModel('test_metamodel/railway.ecore')
test_dataset = pd.read_csv('test_metamodel/test_queries.csv', sep=',')


def main(args):
    model = AutoModelForCausalLM.from_pretrained(args.base_model,
                                                 trust_remote_code=True,
                                                 torch_dtype=torch.float16,
                                                 device_map="auto")

    model = PeftModel.from_pretrained(model, args.checkpoint).eval()
    tokenizer = AutoTokenizer.from_pretrained(args.checkpoint)

    outputs = defaultdict(list)

    for _, row in tqdm(test_dataset.iterrows(), desc='Iterating test dataset', total=test_dataset.shape[0]):
        nl_description = row['nl']
        header = row['header']
        prompt = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                                nl=nl_description,
                                                header=header)
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
            outputs[k].append(header + '\n' + generated)
            print(header + '\n' + generated)
            print('-' * 100)

    for k, v in outputs.items():
        test_dataset[f'{k}_output'] = outputs[k]
    test_dataset.to_csv(f'{args.base_model.replace("/", "-")}_lora.csv', index=False)


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='Run trained models')
    parser.add_argument('--times', type=int, default=5)
    parser.add_argument('--temperature', type=float, default=0.4)
    parser.add_argument('--base_model', default="deepseek-ai/deepseek-coder-6.7b-base")
    parser.add_argument('--checkpoint', default="models/deepseek-coder-mlength/checkpoint-921")

    args = parser.parse_args()
    main(args)
