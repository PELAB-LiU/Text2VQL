
# Text2VQL: Training and running the models

## Installation

```bash
conda env create -f environment_training.yml
conda activate text2vql-training
pip install openai==0.28.1
```

## Fine-tuning OS models

```bash
CUDA_VISIBLE_DEVICES=0 python train.py --model_name_or_path deepseek-ai/deepseek-coder-6.7b-base --output_dir adapter-deepseek-coder-6.7b-base --max_input_length 512 --max_target_length 256
CUDA_VISIBLE_DEVICES=0 python train.py --model_name_or_path deepseek-ai/deepseek-coder-1.3b-base --output_dir adapter-deepseek-coder-1.3b-base --max_input_length 1024 --max_target_length 256
CUDA_VISIBLE_DEVICES=0 python train.py --model_name_or_path codellama/CodeLlama-7b-hf --output_dir adapter-CodeLlama-7b-hf --max_input_length 512 --max_target_length 256
```

## Generation

OS models
```bash
python test_model_railway.py --base_model deepseek-ai/deepseek-coder-6.7b-base --checkpoint PELAB-LiU/deepseek-coder-6.7b-base-Text2VQL-LoRA
python test_model_railway.py --base_model deepseek-ai/deepseek-coder-1.3b-base --checkpoint PELAB-LiU/deepseek-coder-1.3b-base-Text2VQL-LoRA
python test_model_railway.py --base_model codellama/CodeLlama-7b-hf --checkpoint PELAB-LiU/CodeLlama-7b-hf-Text2VQL-LoRA
```

OS models few shot
```bash
python test_os_base.py --base_model deepseek-ai/deepseek-coder-6.7b-base --mode random
python test_os_base.py --base_model codellama/CodeLlama-7b-hf --mode random
python test_os_base.py --base_model deepseek-ai/deepseek-coder-1.3b-base --mode random
```

ChatGPT few shot
```bash
export OPENAI_API_KEY="KEY"
python test_railway_openai.py --prompt_type fs --mode random
```

Report results
```bash
python present_results.py --csv_results results/deepseek-ai-deepseek-coder-6.7b-base_lora_eval.csv --csv_outputs results/deepseek-ai-deepseek-coder-6.7b-base_lora.csv
```