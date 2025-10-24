#!/bin/bash

# Usage: ./run_finetune.sh <model_name_or_path> <output_prefix> [checkpoint]
# Example: ./run_finetune.sh qwen/qwen2.5-coder-1.5b qwen-1.5
# Example with checkpoint: ./run_finetune.sh qwen/qwen2.5-coder-1.5b qwen-1.5 checkpoint_dir

MODEL_NAME=$1
CHECKPOINT=$2  # Optional parameter

if [ -z "$MODEL_NAME" ] ; then
  echo "Usage: $0 <model_name_or_path> [checkpoint]"
  exit 1
fi

LANGS=("java" "ocl" "vql")

for LANG in "${LANGS[@]}"; do
  echo "============================================="
  echo "Prompting LLM with default test set for $LANG"
  echo "============================================="
  
  # Always run the first Python command
  python -m evaluation.finetuned \
    --lang "$LANG" \
    --basemodel "$MODEL_NAME"

  # Only run this if checkpoint is provided
  if [ -n "$CHECKPOINT" ]; then
    echo "Running evaluation with checkpoint for $LANG"
    python -m evaluation.finetuned \
      --lang "$LANG" \
      --basemodel "$MODEL_NAME" \
      --checkpoint "${CHECKPOINT}-${LANG}"
  fi
  
  echo ""
done