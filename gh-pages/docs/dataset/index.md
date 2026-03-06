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

## Meta-model preprocessing

### Index metamodels

Parameters:
* `--db`: Database file to use for storing the domain index. Default: `dataset.db`.
* `--metamodels_datasets`: Coma separated list for folders where EMF meta-models are stored relative to `dataset_construction`. These will be indexed. 

```bash
python -m text2vql.util.parse_dataset
```

### Compute similarities

Compute similarities between indexed domain models.

```bash
python -m text2vql.util.compute_similarities
```

### Compute similarities

Compute similarities between indexed domain models.

```bash
python -m text2vql.util.compute_similarities
```
:::warning
Missing commands: `compute components`, `sample components`, 
:::


### Repair EMF meta-models and compile jars

```
cd ../java/ecore2jar
./generate.sh ../dataset_construction/metamodels/1-sample ../dataset_construction/metamodels/2-jars 
```
Or something like this.

### Readback compilation resutls to database

Update database of compiled domain models. 

```bash
python -m text2vql.util.readback_java
```

## Dataset expansion

Generate ChatGPT promts and run prompts.

Parameters:
* `--domains`:  number of domains to use in prompts. Default is `3` to prevent accidental overuse of ChatGPT.

```bash
python -m text2vql.datasetgen.dataset --domains 600
```

### Pull Responses

Pull responses from OpenAI. 

Parameters:
* `--files`: A coma separated list of files to pull from OpenAI.

```bash
python -m text2vql.datasetgen.batchpull --files ...
```

### Parse responses

Parameters:
* `--files`: A coma separated list of *local* files containing the responses, including the generated queries.

```bash
python -m text2vql.datasetgen.parse --files ...
```

## Filtering dataset


:::info
In a separate shell, start the java query checker server and keep it running or the next steps.
```
cd ../java/generator
./gradlew syntax?
```
:::
Run syntax checker. 

```bash
python -m text2vql.filtering.finetuningdataset
```

Generate test and train dataset from correct generated examples.

```bash
python -m text2vql.filtering.finetuningdataset
```