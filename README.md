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
python parse_datasets.py --metamodels_dataset ecore555|repo-atlanmod|repo-ecore-all
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

## Performance