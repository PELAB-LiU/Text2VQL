# Text2VQL: Dataset construction

The objective of this phase is to create a valid dataset consisting of pairs comprising a VQL 
query and its corresponding natural language description, 
including the target meta-model over which  the query is defined.
```bash
cd dataset_construction
```

## Installation

To run all these dataset construction scripts, Python 3.8 is needed together with several libraries.
To install everything, we will use [conda](https://docs.anaconda.com/free/miniconda/).
```bash
conda env create -f environment_dataset_construction.yml
conda activate text2vql-dataset-creation
```

## Dataset construction

Download datasets of Ecore models. To generate the synthetic dataset, we will sample meta-models that come from
public sources (Atlanmod, GitHub, and a [publicly available dataset](https://zenodo.org/records/2585432))

```bash
./download_datasets.sh
```

Create empty SQLite database. The generated queries as well as the meta-models will be indexed in such database.

```bash
sqlite3 dataset.db < schema.sql
```

Parse datasets and the test (railway) meta-model and insert into database. In the original paper, 
the three datasets and the test_metamodel were parsed and stored. For testing purposes, we recommend parsing just one 
dataset (e.g., just ecore555, the first line).

```bash
python parse_dataset.py --metamodels_dataset ecore555
python parse_dataset.py --metamodels_dataset repo-atlanmod
python parse_dataset.py --metamodels_dataset repo-ecore-all
python parse_dataset.py --metamodels_dataset test_metamodel
```

Compute similar metamodels. The idea of this step is to identify which meta-models are duplicated.

```bash
python compute_similarities.py
```

Select representative meta-models. For each cluster of duplicates, select one meta-model. Thus, we obtain
a deduplicated dataset of meta-models.

```bash
python select_representative_metamodels.py
```

Run script to generate pairs (nl, vql). The `sample` argument indicates how many meta-models are considered
when generating the queries. In the original paper, this parameter was set to 500. However, to speed up the process and for
testing purposes, we set it to 10.

```bash
export OPENAI_API_KEY=<OPENAI_KEY>
python generate_dataset.py --sample 10
```

Before filtering the invalid generated queries, make sure that the eclipse-vnc docker container is running. 
If not, follow the instructions [here](../eclipse-rdp/README.md). To this end, a java script is used as there is no implementation
of the VQL grammar in Python. As a result, this script will generate `valid_ids.txt` which contains the ids of queries that are syntactically valid.

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

Finally, you can generate the final dataset (optional, it can also be pushed to HuggingFace using option `--hf_dataset`).

```bash
python generate_final_dataset.py
```

Four files will be generated: `final_dataset.jsonl` (full dataset in a jsonl format), 
`../results/profiles/final_dataset.csv` (dataset in a csv format), `train.jsonl` (training split in jsonl format), 
and `test.jsonl` (training split in jsonl format). The csv will be used in the results phase. 
The training and testing splits can be used in the fine-tuning step.


## The dataset generated for the Text2VQL paper

The final dataset used to train the models presented in the paper was uploaded to HuggingFace 
and can be found [here](https://huggingface.co/datasets/PELAB-LiU/Text2VQL).




