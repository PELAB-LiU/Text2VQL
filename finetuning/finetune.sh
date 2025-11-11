#!/bin/bash

# Usage: ./run_finetune.sh <model_name_or_path> <output_prefix>
# Example: ./run_finetune.sh qwen/qwen2.5-coder-1.5b qwen-1.5

MODEL_NAME=$1
OUTPUT_PREFIX=$2

if [ -z "$MODEL_NAME" ] || [ -z "$OUTPUT_PREFIX" ]; then
  echo "Usage: $0 <model_name_or_path> <output_prefix>"
  exit 1
fi

LANGS=("ocl" "vql" "java")

for LANG in "${LANGS[@]}"; do
  echo "=============================="
  echo "Running fine-tuning for $LANG"
  echo "=============================="

  # Set max_target_length based on language
  if [ "$LANG" == "java" ]; then
    MAX_TARGET_LENGTH=1024
  else
    MAX_TARGET_LENGTH=512
  fi
  
  python -m evaluation.finetunellm \
    --model_name_or_path "$MODEL_NAME" \
    --lang "$LANG" \
    --output_dir "${OUTPUT_PREFIX}-${LANG}" \
    --data_path_local_train "text2vql_${LANG}_${LANG}_train.jsonl" \
    --data_path_local_test "text2vql_${LANG}_${LANG}_test.jsonl" \
    --max_input_length 2048 \
    --max_target_length $MAX_TARGET_LENGTH
  
  echo ""
done