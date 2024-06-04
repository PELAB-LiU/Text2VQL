# Results reported in the Text2VQL paper

## Dependencies


## Results structure


## RQ1: Quality of the generated queries

```bash
python present_results_rq1.py
```

## RQ2: Complexity and VQL coverage 

```bash
python present_results_rq2.py
```

## RQ3: Fine-tuning with the synthetic dataset

```bash
python present_results_rq3_1.py --csv_results merged/deepseek-ai-deepseek-coder-6.7b-base_fs_random_full.csv
python present_results_rq3_1.py --csv_results merged/deepseek-ai-deepseek-coder-6.7b-base_lora_full.csv 

python present_results_rq3_1.py --csv_results merged/deepseek-ai-deepseek-coder-1.3b-base_fs_random_full.csv
python present_results_rq3_1.py --csv_results merged/deepseek-ai-deepseek-coder-1.3b-base_lora_full.csv

python present_results_rq3_1.py --csv_results merged/codellama-CodeLlama-7b-hf_fs_random_full.csv
python present_results_rq3_1.py --csv_results merged/codellama-CodeLlama-7b-hf_lora_full.csv 

python present_results_rq3_1.py --csv_results merged/chatgpt_fs_random_full.csv
```

```bash
python present_results_rq3_2.py
```
