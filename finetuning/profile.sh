#!/bin/bash

# Usage: ./run_finetune.sh <model_name_or_path> <output_prefix>
# Example: ./run_finetune.sh qwen/qwen2.5-coder-1.5b qwen-1.5

MODEL_NAME=$1
OUTPUT_PREFIX=$2

if [ -z "$MODEL_NAME" ] || [ -z "$OUTPUT_PREFIX" ]; then
  echo "Usage: $0 <model_name_or_path> <output_prefix>"
  exit 1
fi

LANGS=("java" "ocl" "vql")

for LANG in "${LANGS[@]}"; do
  echo "=============================="
  echo "Profile for $LANG"
  echo "=============================="
  
  python -m evaluation.profiledata \
    --model_name_or_path "$MODEL_NAME" \
    --lang "$LANG" \
    --data_path_local_train "text2vql_${LANG}_${LANG}_train.jsonl" \
    --data_path_local_test "text2vql_${LANG}_${LANG}_test.jsonl" 
  
  echo ""
done
