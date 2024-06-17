# Results reported in the Text2VQL paper

## Dependencies
todo
## Results structure
todo

## RQ1: Quality of the generated queries

```bash
python present_results_rq1.py
```

## RQ2: Complexity and VQL coverage 

Log language elements encountered in the dataset.
```bash
docker exec -it \
    -w /config/eclipse-workspace/se.liu.ida.sas.pelab.vqlsyntaxcheck \
    eclipse-vnc java -cp "/opt/eclipse/plugins/*:jdbc/*" org.eclipse.xtend.core.compiler.batch.Main \
    -d xtend-gen -useCurrentClassLoader src
docker exec -it \
    -e MODE=AGG \
    -e CSV=/config/text2vql/results/profiles/final_dataset.csv \
    -e COL=pattern \
    -w /config/eclipse-workspace/se.liu.ida.sas.pelab.vqlsyntaxcheck \
    eclipse-vnc ant clean build ProfileMain
```

Evaluate query complexity in the dataset.
```bash
docker exec -it \
    -w /config/eclipse-workspace/se.liu.ida.sas.pelab.vqlsyntaxcheck \
    eclipse-vnc java -cp "/opt/eclipse/plugins/*:jdbc/*" org.eclipse.xtend.core.compiler.batch.Main \
    -d xtend-gen -useCurrentClassLoader src
docker exec -it \
    -e MODE=IND \
    -e CSV=/config/text2vql/results/profiles/final_dataset.csv \
    -e COL=pattern \
    -e OUT=/config/text2vql/results/profiles/profiles_raw.csv \
    -w /config/eclipse-workspace/se.liu.ida.sas.pelab.vqlsyntaxcheck \
    eclipse-vnc ant clean build ProfileMain

python merge-profile.py
```

```bash
python present_results_rq2.py
```

## RQ3: Fine-tuning with the synthetic dataset

(Optional.) Generate instance models in the Railway domain for testing.
This will generate 300 models will a seed model, and an additional 300 random model. 
Warning: This may replace the original models we used. Expected runtime is around 3-6 hours.

```bash
docker exec -it \
    -w /config/refinery \
    eclipse-vnc ./generate_models.sh /config/text2vql/results/models 
```

Run query test
```bash
docker exec -it \
    -w /config/dataset_construction/java/se.liu.ida.sas.pelab.vqlsyntaxcheck \
    eclipse-vnc java -cp "/opt/eclipse/plugins/*:jdbc/*" org.eclipse.xtend.core.compiler.batch.Main \
    -d xtend-gen -useCurrentClassLoader src
# docker exec -it \
#    -e METAPATH=foo.ecore \
#    -e INPUT=ai_out.csv \
#    -e OUTPUT=ai_out_eval.csv \
#    -e INSTANCEDIR=models612 \
#    -e AI=<idx>_output \
#    -w /config/dataset_construction/java/se.liu.ida.sas.pelab.vqlsyntaxcheck \
#    eclipse-vnc ant clean build CSVBasedEvaluation
docker exec -it \
    -e INSTANCEDIR=/config/text2vql/results/testmodels \
    -e META_PATH=/config/text2vql/results/railway.ecore \
    -e INDIR=/config/text2vql/results/ai/ \
    -e OUTDIR=/config/text2vql/results/eval/ \
    -w /config/util \
    compare.sh

python merge-eval.py
#TODO uploaad test.sh (Script for full eval)
```

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
