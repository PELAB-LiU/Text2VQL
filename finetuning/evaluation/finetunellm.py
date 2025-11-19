from dataclasses import dataclass, field
from typing import Optional

import math

import torch
import transformers
from datasets import load_dataset
from peft import TaskType, LoraConfig, get_peft_model
from transformers import AutoModelForCausalLM, AutoTokenizer, Trainer, default_data_collator, HfArgumentParser, \
    EarlyStoppingCallback

from evaluation.templates import PROMPT_QUERY, QUERY

IGNORE_INDEX = -100


LORA_TARGET_MODULES = {
    "Salesforce/codegen2-1B": {
        "target_modules": ["qkv_proj"],
        "ff_modules": ["out_proj", "fc_in", "fc_out"]
    },
    "Salesforce/codegen2-3_7B": {
        "target_modules": ["qkv_proj"],
        "ff_modules": []
    },
    "Salesforce/codegen2-7B": {
        "target_modules": ["qkv_proj"],
        "ff_modules": []
    },
    "deepseek-ai/deepseek-coder-6.7b-base": {
        "target_modules": ["q_proj", "v_proj", "o_proj", "k_proj"],
        "ff_modules": []
    },
    "deepseek-ai/deepseek-coder-7b-base-v1.5": {
        "target_modules": ["q_proj", "v_proj", "o_proj", "k_proj"],
        "ff_modules": []
    },
    "deepseek-ai/deepseek-coder-1.3b-base": {
        "target_modules": ["q_proj", "v_proj", "o_proj", "k_proj"],
        "ff_modules": []
    },
    "codellama/CodeLlama-7b-hf": {
        "target_modules": ["q_proj", "v_proj", "o_proj", "k_proj"],
        "ff_modules": []
    },
    "meta-llama/Llama-2-7b-hf": {
        "target_modules": ["q_proj", "v_proj", "o_proj", "k_proj"],
        "ff_modules": []
    }
}


def load_model_and_tokenizer(args):
    if args.fp16_model:
        model = AutoModelForCausalLM.from_pretrained(args.model_name_or_path,
                                                     trust_remote_code=True, dtype=torch.float16)
    else:
        model = AutoModelForCausalLM.from_pretrained(args.model_name_or_path,
                                                     trust_remote_code=True)
    tokenizer = AutoTokenizer.from_pretrained(args.model_name_or_path)

    if args.training_method == "lora":
        if args.model_name_or_path in LORA_TARGET_MODULES:
            peft_config = LoraConfig(task_type=TaskType.CAUSAL_LM,
                                     r=8,
                                     lora_alpha=16,
                                     lora_dropout=0.1,
                                     bias="none",
                                     target_modules=LORA_TARGET_MODULES[args.model_name_or_path]["target_modules"])
        else:
            peft_config = LoraConfig(task_type=TaskType.CAUSAL_LM,
                                     r=8,
                                     lora_alpha=16,
                                     lora_dropout=0.1,
                                     bias="none")
        model = get_peft_model(model, peft_config)
        print("Params")
        model.print_trainable_parameters()

    if getattr(tokenizer, "pad_token_id") is None:
        tokenizer.pad_token_id = tokenizer.eos_token_id
        model.config.pad_token_id = model.config.eos_token_id

    tokenizer.padding_side = "left"

    return model, tokenizer

def compute_max_lengths(dataset, tokenizer, lang, round_to_power_of_2: bool = False):
    """
    Computes the maximum lengths for inputs and targets in the dataset.
    Optionally rounds up lengths to the next power of 2.
    """
    max_input_len = 0
    max_target_len = 0

    for split in dataset:
        for example in dataset[split]:
            target = QUERY.safe_substitute(lang=lang, query=example['pattern'])
            tokenized_target = tokenizer(target, add_special_tokens=False)
            max_target_len = max(max_target_len, len(tokenized_target["input_ids"]) + 1)  # +1 for EOS

            prompt = PROMPT_QUERY[lang].safe_substitute(
                metamodel=example['definition'],
                description=example['descript'],
                signature=example['signat']
            )
            tokenized_prompt = tokenizer(prompt, add_special_tokens=False)
            max_input_len = max(max_input_len, len(tokenized_prompt["input_ids"]))

    if round_to_power_of_2:
        def next_power_of_2(x):
            return 1 if x == 0 else 2 ** math.ceil(math.log2(x))

        max_input_len = next_power_of_2(max_input_len)
        max_target_len = next_power_of_2(max_target_len)

    return max_input_len, max_target_len


@dataclass
class ModelArguments:
    model_name_or_path: Optional[str] = field(default="Salesforce/codegen2-7B")
    training_method: Optional[str] = field(default="lora")
    fp16_model: bool = field(default=True)


@dataclass
class DataArguments:
    data_path: str = field(default="PELAB-LiU/Text2VQL", metadata={"help": "Path to the training data."})
    max_target_length: int = field(default=256)
    max_input_length: int = field(default=256)
    where_data: str = field(default="disk", metadata={"help": "Only hf or disk."})
    data_path_local_train: str = field(default="./text2vql_train.jsonl",
                                       metadata={"help": "Path to the training data."})
    data_path_local_test: str = field(default="./text2vql_test.jsonl",
                                      metadata={"help": "Path to the training data."})
    lang: str = field(default="vql", metadata={"help": "Target language"})


@dataclass
class TrainingArguments(transformers.TrainingArguments):
    cache_dir: Optional[str] = field(default=None)
    optim: str = field(default="adamw_torch")
    per_device_train_batch_size: int = field(default=1)
    per_device_eval_batch_size: int = field(default=1)
    gradient_accumulation_steps: int = field(default=32)
    fp16: bool = field(default=True)
    save_strategy: str = field(default="epoch")
    logging_steps: int = field(default=1)
    learning_rate: float = field(default=3e-4)
    seed: int = field(default=123)
    max_grad_norm: float = field(default=1.)
    output_dir: str = field(default="models/codegen2-7b")
    eval_strategy: str = field(default="epoch")
    load_best_model_at_end: bool = field(default=True)
    save_total_limit: int = field(default=1)
    num_train_epochs: int = field(default=10)


def preprocess_function(example, tokenizer, max_target_length, max_input_length, lang):
    """
    # we tokenize, pad and truncate the samples in the following way:
    #   <pad><pad>...### Instruction:\n<intent>\n### Answer:\n<snippet><eos>
    #
    #   - prompt tokens `<pad><pad>...<intent + \n>` are ignored in the computation of the loss (-100 labels)
    #   - `<eos>` delimits the snippet and allows the model to have more focused predictions at inference
    """
    target = QUERY.safe_substitute(lang=lang, query=example['pattern'])
    tokenized_target = tokenizer(target,
                                 truncation=True,
                                 max_length=max_target_length - 1,
                                 add_special_tokens=False)
    tokenized_target["input_ids"] = tokenized_target["input_ids"] + [tokenizer.eos_token_id]
    tokenized_target["attention_mask"] = tokenized_target["attention_mask"] + [1]

    prompt = PROMPT_QUERY[lang].safe_substitute(
        metamodel=example['definition'],
        description=example['descript'],
        signature=example['signat']
    )
    max_prompt_len = (max_input_length + max_target_length) - \
                     len(tokenized_target["input_ids"])
    model_inputs = tokenizer(prompt,
                             truncation=True,
                             padding="max_length",
                             max_length=max_prompt_len)

    model_inputs["labels"] = [-100] * len(model_inputs["input_ids"]) + tokenized_target["input_ids"]
    model_inputs["input_ids"] = model_inputs["input_ids"] + tokenized_target["input_ids"]
    model_inputs["attention_mask"] = model_inputs["attention_mask"] + tokenized_target["attention_mask"]
    return model_inputs


def train(model_args, data_args, training_args):
    if data_args.where_data == "hf":
        dataset = load_dataset(data_args.data_path)
    elif data_args.where_data == "disk":
        # data is in disk
        data_files = {"train": data_args.data_path_local_train,
                      "test": data_args.data_path_local_test}
        dataset = load_dataset("json", data_files=data_files)
    else:
        raise ValueError(f"{data_args.where_data} not supported")

    model, tokenizer = load_model_and_tokenizer(model_args)

    max_input_len, max_target_len = compute_max_lengths(dataset, tokenizer, data_args.lang)
    print(f"Computed max_input_length: {max_input_len}, max_target_length: {max_target_len}")

    if not torch.cuda.is_available():
        raise RuntimeError("No GPU available. Finetuning without a GPU is practically impossible.")
    
    dataset = dataset.map(lambda x: preprocess_function(x, tokenizer,
                                                        data_args.max_target_length,
                                                        data_args.max_input_length,
                                                        data_args.lang),
                          remove_columns=dataset["train"].column_names,
                          desc="Generating samples features.")

    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=dataset["train"],
        eval_dataset=dataset["test"],
        processing_class=tokenizer,
        data_collator=default_data_collator,
        callbacks=[EarlyStoppingCallback(early_stopping_patience=2)]
    )

    trainer.train()


def main():
    parser = HfArgumentParser((ModelArguments, DataArguments, TrainingArguments))
    model_args, data_args, training_args = parser.parse_args_into_dataclasses()
    train(model_args, data_args, training_args)



"""
Running:

Planned:
    
Done:
./finetune.sh codellama/CodeLlama-7b-hf codellama-7b
./finetune.sh deepseek-ai/deepseek-coder-1.3b-base deepseek-coder-1.3b
./finetune.sh deepseek-ai/deepseek-coder-7b-base-v1.5 deepseek-coder-7b
./finetune.sh Qwen/Qwen3-1.7B-Base qwen3-1.7b
./finetune.sh Qwen/Qwen3-8B-Base qwen3-8b
./finetune.sh Qwen/Qwen2.5-Coder-7B qwen2.5-coder-7b
./finetune.sh Qwen/Qwen2.5-Coder-1.5B qwen2.5-coder-1.5b
"""
if __name__ == '__main__':
    main()
