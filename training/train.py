from dataclasses import dataclass, field
from typing import Optional

import torch
import transformers
from datasets import load_dataset
from peft import TaskType, LoraConfig, get_peft_model
from transformers import AutoModelForCausalLM, AutoTokenizer, Trainer, default_data_collator, HfArgumentParser, \
    EarlyStoppingCallback

IGNORE_INDEX = -100
PROMPT_NL = """Below there is the specification of a meta-model
{metamodel}
Write the patterns using the Viatra Query Language of the following natural language specification:
{nl}
Patterns:
"""

PROMPT_CODE = """{metamodel}
//{nl}
"""

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
                                                     trust_remote_code=True, torch_dtype=torch.float16)
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
        model.print_trainable_parameters()

    if getattr(tokenizer, "pad_token_id") is None:
        tokenizer.pad_token_id = tokenizer.eos_token_id
        model.config.pad_token_id = model.config.eos_token_id

    tokenizer.padding_side = "left"

    return model, tokenizer


@dataclass
class ModelArguments:
    model_name_or_path: Optional[str] = field(default="Salesforce/codegen2-7B")
    training_method: Optional[str] = field(default="lora")
    nl_or_code: str = field(default="code")
    fp16_model: bool = field(default=True)


@dataclass
class DataArguments:
    data_path: str = field(default="PELAB-LiU/Text2VQL", metadata={"help": "Path to the training data."})
    max_target_length: int = field(default=256)
    max_input_length: int = field(default=256)


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
    evaluation_strategy: str = field(default="epoch")
    load_best_model_at_end: bool = field(default=True)
    save_total_limit: int = field(default=1)
    num_train_epochs: int = field(default=10)


def preprocess_function(example, tokenizer, max_target_length, max_input_length, nl_or_code):
    """
    # we tokenize, pad and truncate the samples in the following way:
    #   <pad><pad>...### Instruction:\n<intent>\n### Answer:\n<snippet><eos>
    #
    #   - prompt tokens `<pad><pad>...<intent + \n>` are ignored in the computation of the loss (-100 labels)
    #   - `<eos>` delimits the snippet and allows the model to have more focused predictions at inference
    """
    tokenized_target = tokenizer(example['pattern'],
                                 truncation=True,
                                 max_length=max_target_length - 1,
                                 add_special_tokens=False)
    tokenized_target["input_ids"] = tokenized_target["input_ids"] + [tokenizer.eos_token_id]
    tokenized_target["attention_mask"] = tokenized_target["attention_mask"] + [1]

    PROMPT = PROMPT_NL if nl_or_code == "nl" else PROMPT_CODE

    prompt = PROMPT.format(
        metamodel=example['metamodel_definition'],
        nl=example['nl']
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
    dataset = load_dataset(data_args.data_path)
    print(dataset)
    model, tokenizer = load_model_and_tokenizer(model_args)

    dataset = dataset.map(lambda x: preprocess_function(x, tokenizer,
                                                        data_args.max_target_length,
                                                        data_args.max_input_length,
                                                        model_args.nl_or_code),
                          remove_columns=dataset["train"].column_names,
                          desc="Generating samples features.")

    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=dataset["train"],
        eval_dataset=dataset["test"],
        tokenizer=tokenizer,
        data_collator=default_data_collator,
        callbacks=[EarlyStoppingCallback(early_stopping_patience=2)]
    )

    trainer.train()


def main():
    parser = HfArgumentParser((ModelArguments, DataArguments, TrainingArguments))
    model_args, data_args, training_args = parser.parse_args_into_dataclasses()
    train(model_args, data_args, training_args)


if __name__ == '__main__':
    main()
