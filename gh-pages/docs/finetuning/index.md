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
./finetune.sh deepseek-ai/deepseek-coder-7b-base-v1.5 deepseek-coder-7b
./finetune.sh Qwen/Qwen3-1.7B-Base qwen3-1.7b
./finetune.sh Qwen/Qwen3-8B-Base qwen3-8b
./finetune.sh Qwen/Qwen2.5-Coder-7B qwen2.5-coder-7b
./finetune.sh Qwen/Qwen2.5-Coder-1.5B qwen2.5-coder-1.5b
```

## Prompt LLMs

:::warning
Configure evaluation database
:::

A script is provided to automate the prompting.

```bash
./promptllm.sh  Qwen/Qwen2.5-Coder-1.5B qwen2.5-coder-1.5b
./promptllm.sh  Qwen/Qwen3-8B-Base qwen3-8b 
./promptllm.sh  Qwen/Qwen2.5-Coder-7B qwen2.5-coder-7b
./promptllm.sh  Qwen/Qwen3-1.7B-Base qwen3-1.7b
```