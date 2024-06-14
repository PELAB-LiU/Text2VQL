# Text2VQL: Dataset construction

## Installation

```bash
conda env create -f environment_dataset_construction.yml
```

## Dataset construction

Download datasets of Ecore models.

```bash
./download_datasets.sh
```

Create empty SQLite database.

```bash
sqlite3 dataset.db < schema.sql
```

Parse datasets and the test meta-model and insert into database. In the original paper, 
the three datasets and the test_metamodel were parsed and stored. For testing purposes, we recommend parsing just one 
dataset (e.g., just ecore555).

```bash
python parse_dataset.py --metamodels_dataset ecore555|repo-atlanmod|repo-ecore-all|test_metamodel
```

Compute similar metamodels. The idea of this step is to compute which meta-models are duplicated.

```bash
python compute_similarities.py
```

Select representative meta-models. For each cluster of duplicates, select one meta-model. Thus, we obtain
a deduplicated dataset of meta-models.

```bash
python select_representative_metamodels.py
```

Run script to generate pairs (nl, vql). The `sample` argument indicates how many meta-models are considered
when generating the queries. In the original paper, this parameter was set to 500. However, to speed the process and for
testing purposes, we set it to 5.

```bash
export OPENAI_API_KEY=OPENAI_KEY
python generate_dataset.py --sample 5
```

Make sure that the eclipse-vnc docker container is running. If not, follow the instructions [here](../eclipse-rdp/README.md).

Filter invalid queries
```bash
docker exec -it \
    -w /config/eclipse-workspace/se.liu.ida.sas.pelab.vqlsyntaxcheck \
    eclipse-vnc java -cp "/opt/eclipse/plugins/*:jdbc/*" org.eclipse.xtend.core.compiler.batch.Main \
    -d xtend-gen -useCurrentClassLoader src
docker exec -it \
    -e JDBC_URL=/config/text2vql/dataset_construction/dataset.db \
    -e PROJECT_PATH=/config/text2vql/dataset_construction/ \
    -e OUTPUT=/config/text2vql/dataset_construction/valid_ids.txt \
    -w /config/eclipse-workspace/se.liu.ida.sas.pelab.vqlsyntaxcheck \
    eclipse-vnc ant clean build EvaluateDatabase
```

Generate the dataset to HF (optional, it can also be pushed to HuggingFace using option `--hf_dataset`).

```bash
python generate_final_dataset.py
```

## The dataset generated for the Text2VQL paper

The final dataset used to train the models presented in the paper was uploaded to HuggingFace 
and can be found [here](https://huggingface.co/datasets/PELAB-LiU/Text2VQL).


