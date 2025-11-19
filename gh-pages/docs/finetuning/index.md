---
sidebar_position: 4
---

# Finetuning

Finetuning has to be performed in an environment with GPU. 
We used a jupyter environment with cuda and an NVIDIA A100. GPUs with 24GB VRAM should be sufficient. GPUs with less than 16 GB VRAM will not be sufficient.


:::info
The process relies on files in other directories of the project. Have the whole repository available in the environemnt. 
:::

:::info[Working directory]
Work in `finetuning`.
:::

## Run finetuning

We provide a helper script `finetune.sh` that performs the finetuning for `VQL`, `OCL` and `Java` if the input files are provided correctly. It is only designed to work with LLMs supported by the python code.

```bash
./finetune.sh codellama/CodeLlama-7b-hf codellama-7b
./finetune.sh deepseek-ai/deepseek-coder-1.3b-base deepseek-coder-1.3b
./finetune.sh deepseek-ai/deepseek-coder-7b-base-v1.5 deepseek-coder-7b
./finetune.sh Qwen/Qwen3-1.7B-Base qwen3-1.7b
./finetune.sh Qwen/Qwen3-8B-Base qwen3-8b
./finetune.sh Qwen/Qwen2.5-Coder-7B qwen2.5-coder-7b
./finetune.sh Qwen/Qwen2.5-Coder-1.5B qwen2.5-coder-1.5b
```

## Prompt LLMs

Initialize an empty database for collecting the responses.

```bash
sqlite3 evaluation.db < ../dataset_construction/schema-eval.sql
```
Run the following commands to prompt the LLMs and save resomnses to the databsae. Default database is `evaluation.db`. If you want to modify, you need to set the `--db` parameter to the location of the databse *inside the `promptllm.sh` script file*. 

```bash
./promptllm.sh codellama/CodeLlama-7b-hf codellama-7b
./promptllm.sh deepseek-ai/deepseek-coder-1.3b-base deepseek-coder-1.3b
./promptllm.sh deepseek-ai/deepseek-coder-7b-base-v1.5 deepseek-coder-7b
./promptllm.sh Qwen/Qwen2.5-Coder-1.5B qwen2.5-coder-1.5b
./promptllm.sh Qwen/Qwen3-8B-Base qwen3-8b 
./promptllm.sh Qwen/Qwen2.5-Coder-7B qwen2.5-coder-7b
./promptllm.sh Qwen/Qwen3-1.7B-Base qwen3-1.7b
```