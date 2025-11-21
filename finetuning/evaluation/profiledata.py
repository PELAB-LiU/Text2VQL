from dataclasses import dataclass, field
from typing import Optional
import math
from datasets import load_dataset
from transformers import AutoTokenizer, HfArgumentParser
from evaluation.templates import PROMPT_QUERY, QUERY
import matplotlib.pyplot as plt

def load_tokenizer(args):
    tokenizer = AutoTokenizer.from_pretrained(args.model_name_or_path)
    if getattr(tokenizer, "pad_token_id", None) is None:
        tokenizer.pad_token_id = tokenizer.eos_token_id
    return tokenizer

def compute_lengths(dataset, tokenizer, lang):
    """
    Computes input and target lengths for all examples in the dataset.
    Returns two lists of lengths.
    """
    input_lengths = []
    target_lengths = []

    for split in dataset:
        for example in dataset[split]:
            # Compute target length
            target = QUERY.safe_substitute(lang=lang, query=example['pattern'])
            tokenized_target = tokenizer(target, add_special_tokens=False)
            target_len = len(tokenized_target["input_ids"]) + 1  # +1 for EOS
            target_lengths.append(target_len)

            # Compute input length
            prompt = PROMPT_QUERY[lang].safe_substitute(
                metamodel=example['definition'],
                description=example['descript'],
                signature=example['signat']
            )
            tokenized_prompt = tokenizer(prompt, add_special_tokens=False)
            input_len = len(tokenized_prompt["input_ids"])
            input_lengths.append(input_len)

    return input_lengths, target_lengths

def plot_histogram(lengths, title, xlabel, ylabel="Count", bins=50, output_file=None):
    plt.figure(figsize=(10, 6))
    plt.hist(lengths, bins=bins, color='skyblue', edgecolor='black')
    plt.title(title)
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.grid(axis='y', alpha=0.75)
    if output_file:
        plt.savefig(output_file)
        print(f"Saved histogram to {output_file}")
    plt.close()

@dataclass
class ModelArguments:
    model_name_or_path: Optional[str] = field(default="Salesforce/codegen2-7B")

@dataclass
class DataArguments:
    where_data: str = field(default="disk", metadata={"help": "Only hf or disk."})
    data_path_local_train: str = field(default="./text2vql_train.jsonl",
                                       metadata={"help": "Path to the training data."})
    data_path_local_test: str = field(default="./text2vql_test.jsonl",
                                      metadata={"help": "Path to the training data."})
    lang: str = field(default="vql", metadata={"help": "Target language"})

def precentile(array, cutoff, prefix='Input'):
    print(f"{prefix} (<={cutoff}): {sum(1 for v in array if v <= cutoff) / len(array) * 100}%")

def profile(model_args, data_args):
    if data_args.where_data == "hf":
        dataset = load_dataset(data_args.data_path)
    elif data_args.where_data == "disk":
        data_files = {"train": data_args.data_path_local_train,
                      "test": data_args.data_path_local_test}
        dataset = load_dataset("json", data_files=data_files)
    else:
        raise ValueError(f"{data_args.where_data} not supported")

    print(dataset)
    tokenizer = load_tokenizer(model_args)

    input_lengths, target_lengths = compute_lengths(dataset, tokenizer, data_args.lang)
    print(f"Number of examples: {len(input_lengths)}")

    # Plot histograms
    precentile(input_lengths, 2048)
    plot_histogram(input_lengths, "Histogram of Input Lengths", "Input Length", output_file=f"input_lengths_{data_args.lang}.png")
    if data_args.lang == 'java':
        precentile(target_lengths, 1024, prefix='Output')
    else:
        precentile(target_lengths, 512, prefix='Output')
    plot_histogram(target_lengths, "Histogram of Target Lengths", "Target Length", output_file=f"target_lengths_{data_args.lang}.png")

def main():
    parser = HfArgumentParser((ModelArguments, DataArguments))
    model_args, data_args = parser.parse_args_into_dataclasses()
    profile(model_args, data_args)

"""
#qwen-1.5 java: 
python -m evaluation.profiledata --model_name_or_path qwen/qwen2.5-coder-1.5b --lang java --data_path_local_train text2vql_java_java_train.jsonl --data_path_local_test text2vql_java_java_test.jsonl
#qwen-1.5 ocl: 
python profiledata.py --model_name_or_path qwen/qwen2.5-coder-1.5b --lang ocl --data_path_local_train text2vql_ocl_ocl_train.jsonl --data_path_local_test text2vql_ocl_ocl_test.jsonl
#qwen-1.5 vql: 
python profiledata.py --model_name_or_path qwen/qwen2.5-coder-1.5b --lang vql --data_path_local_train text2vql_vql_vql_train.jsonl --data_path_local_test text2vql_vql_vql_test.jsonl
"""

if __name__ == '__main__':
    main()
