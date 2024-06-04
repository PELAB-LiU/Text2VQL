
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

Build and start docker environemnt. The container contains an eclipse environment with the build dependencies and necessary tools to compile the project. Furthermore, it creates a vnc server (web-based remote desktop) that allows the inspection and modification of the source code.
```bash
docker build -t eclipse-vnc ../eclipse-rdp
docker run -d \
  --name=eclipse-vnc \
  --device=/dev/dri:/dev/dri \
  --security-opt seccomp=unconfined \
  -p 3000:3000 \
  -p 3001:3001 \
  --shm-size="1gb" \
  -v $(pwd):/config/dataset_construction \
  eclipse-vnc
```


Filter invalid queries
```bash
mkdir java/se.liu.ida.sas.pelab.vqlsyntaxcheck/jdbc
wget https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar \
    -O java/se.liu.ida.sas.pelab.vqlsyntaxcheck/jdbc/slf4j-api-1.7.36.jar
wget https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.44.0.0/sqlite-jdbc-3.44.0.0.jar \
    -O java/se.liu.ida.sas.pelab.vqlsyntaxcheck/jdbc/sqlite-jdbc-3.44.0.0.jar
wget https://repo1.maven.org/maven2/org/apache/commons/commons-csv/1.10.0/commons-csv-1.10.0.jar \
    -O java/se.liu.ida.sas.pelab.vqlsyntaxcheck/jdbc/commons-csv-1.10.0.jar
docker exec -it \
    -w /config/dataset_construction/java/se.liu.ida.sas.pelab.vqlsyntaxcheck \
    eclipse-vnc java -cp "/opt/eclipse/plugins/*" org.eclipse.xtend.core.compiler.batch.Main \
    -d xtend-gen -useCurrentClassLoader src
docker exec -it \
    -e JDBC_URL=/config/dataset_construction/dataset.db \
    -e PROJECT_PATH=/config/dataset_construction/ \
    -e OUTPUT=/config/dataset_construction/valid_ids.txt \
    -w /config/dataset_construction/java/se.liu.ida.sas.pelab.vqlsyntaxcheck \
    eclipse-vnc ant clean build EvaluateDatabase
```

(Optional) Stop and remove container.
```bash
docker container stop eclipse-vnc
docker container rm eclipse-vnc
```

Push the dataset to HF.

```bash
python upload_to_hf.py --hf_dataset antolin/text2vql
```

## Fine-tuning OS models

```bash
CUDA_VISIBLE_DEVICES=0 python train.py --model_name_or_path deepseek-ai/deepseek-coder-6.7b-base --output_dir models/deepseek-coder-mlength --max_input_length 512 --max_target_length 256
CUDA_VISIBLE_DEVICES=0 python train.py --model_name_or_path deepseek-ai/deepseek-coder-1.3b-base --output_dir models/deepseek-coder-1.3b-mlength --max_input_length 1024 --max_target_length 256
CUDA_VISIBLE_DEVICES=0 python train.py --model_name_or_path codellama/CodeLlama-7b-hf --output_dir models/codellama-7b --max_input_length 512 --max_target_length 256
CUDA_VISIBLE_DEVICES=0 python train.py --model_name_or_path bigscience/bloom-7b1 --output_dir models/bloom-7b --max_input_length 512 --max_target_length 256
CUDA_VISIBLE_DEVICES=1 python train.py --model_name_or_path Salesforce/codegen2-7B --output_dir models/codegen2-7b --max_input_length 512 --max_target_length 256
```

## Generation

OS models
```bash
python test_model_railway.py --base_model deepseek-ai/deepseek-coder-6.7b-base --checkpoint models/deepseek-coder-mlength/checkpoint-921
python test_model_railway.py --base_model codellama/CodeLlama-7b-hf --checkpoint models/codellama-7b/checkpoint-921
python test_model_railway.py --base_model deepseek-ai/deepseek-coder-1.3b-base --checkpoint models/deepseek-coder-1.3b-mlength/checkpoint-921/
```

OS models few shot
```bash
python test_os_base.py --base_model deepseek-ai/deepseek-coder-6.7b-base --mode random
python test_os_base.py --base_model codellama/CodeLlama-7b-hf --mode random
python test_os_base.py --base_model deepseek-ai/deepseek-coder-1.3b-base --mode random
```

ChatGPT few shot
```bash
python test_railway_openai.py --prompt_type fs --mode random
```

Report results
```bash
python present_results.py --csv_results results/deepseek-ai-deepseek-coder-6.7b-base_lora_eval.csv --csv_outputs results/deepseek-ai-deepseek-coder-6.7b-base_lora.csv
```