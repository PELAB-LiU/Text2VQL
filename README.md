
# Text2Vql

## Dataset construction

Download datasets of Ecore models.

```bash
./download_datasets.sh
```

Create empty SQLite database.

```bash
sqlite3 dataset.db < schema.sql
```

Parse datasets and insert into database.

```bash
python parse_dataset.py --metamodels_dataset ecore555|repo-atlanmod|repo-ecore-all|test_metamodel
```

Compute similar metamodels.

```bash
python compute_similarities.py
```

Select representative meta-models.

```bash
python select_representative_metamodels.py
```

Run script to generate pairs (nl, vql).

```bash
python generate_dataset.py
```

Push the dataset to HF.

```bash
python upload_to_hf.py --hf_dataset antolin/text2vql
```

## Fine-tuning OS models

```bash
 CUDA_VISIBLE_DEVICES=0 python train.py --model_name_or_path deepseek-ai/deepseek-coder-6.7b-base --output_dir models/deepseek-coder-mlength-1b --max_input_length 512 --max_target_length 256
```

## Generation

ChatGPT few shot
```bash
python test_railway_openai.py --prompt_type fs --mode random
```

Report results
```bash
python present_results.py --csv_results results/deepseek-ai-deepseek-coder-6.7b-base_lora_eval.csv --csv_outputs results/deepseek-ai-deepseek-coder-6.7b-base_lora.csv
```