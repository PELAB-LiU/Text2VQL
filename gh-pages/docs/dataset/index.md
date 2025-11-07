---
sidebar_position: 3
---

# Dataset construction

Open the `dataset_construction` folder in VSCode (in the devcontainer.)

You may need to first open the root `Text2VQL` folder. VSCode will ask if you wish to open it in a devcontainer, accept it and wait for VSCode to build the image and create the container. 
When completed you can open the `dataset_construction` from the current windows.

## Download ecore metamodels

```bash
./download_datasets.sh
```

## Initialize the database

```bash
python -m text2vql.util.init_db
```

:::danger
Code does not support this (yet). 
:::

## Index metamodels

```bash
python -m text2vql.util.parse_dataset
```

## Compute similarities

```bash
python -m text2vql.util.compute_similarities
```

:::warning
Missing commands
:::