
# Text2Vql

## Dataset construction

Download datasets of Ecore models.

```bash
./download_ecore555.sh
```

Create empty SQLite database.

```bash
sqlite3 dataset.db < schema.sql
```


Run script to create dataset.

```bash
python generate_dataset.py
```

## Fine-tuning OS models

## Performance