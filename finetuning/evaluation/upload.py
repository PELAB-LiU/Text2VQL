import os
import argparse
import tempfile
from huggingface_hub import HfApi, Repository, upload_folder
from transformers import AutoModelForCausalLM, AutoTokenizer

from evaluation.finetuned import findCheckpoint

def upload_model_repo(target_repo: str, base_model: str, languages: list):
    """
    Uploads fine-tuned models organized in language subfolders to an existing HF Hub repo.
    Uses current working directory as root.
    """
    local_dir = os.getcwd()
    print(f"Using current working directory as root: {local_dir}")

    # Prepare folder names to match
    lang_subdirs = [{"lang": lang, "root":f"{base_model}-{lang}"} for lang in languages]

    for entry in lang_subdirs:
        lang = entry["lang"]
        local_model_path = findCheckpoint(entry["root"])

        print(f"Processing {local_model_path} -> {target_repo}/{lang}")

        try:
            model = AutoModelForCausalLM.from_pretrained(local_model_path)
            tokenizer = AutoTokenizer.from_pretrained(local_model_path)
        except Exception as e:
            print(f"Skipping {entry["root"]}, could not load model/tokenizer: {e}")
            continue
        
        with tempfile.TemporaryDirectory() as tmp_dir:
            # Save model + tokenizer into temp dir
            model.save_pretrained(tmp_dir)
            tokenizer.save_pretrained(tmp_dir)
            
            upload_folder(
                folder_path=tmp_dir,
                repo_id=target_repo,
                path_in_repo=lang,
                token=os.environ["HF_TOKEN"],
                commit_message=f"Upload {lang} model"
            )

    print("All uploads completed!")

"""
python -m evaluation.upload --base_model "codellama-7b" --target "PELAB-LiU/Text2MQL-CodeLlama-7b"
python -m evaluation.upload --base_model "deepseek-coder-1.3b" --target "PELAB-LiU/Text2MQL-DeepSeek-Coder-1.3b-base"
python -m evaluation.upload --base_model "deepseek-coder-7b" --target "PELAB-LiU/Text2MQL-DeepSeek-Coder-7b-base-v1.5"
python -m evaluation.upload --base_model "qwen3-1.7b" --target "PELAB-LiU/Text2MQL-Qwen3-1.7B-Base"
python -m evaluation.upload --base_model "qwen3-8b" --target "PELAB-LiU/Text2MQL-Qwen3-8B-Base"
python -m evaluation.upload --base_model "qwen2.5-coder-7b" --target "PELAB-LiU/Text2MQL-Qwen2.5-Coder-7B"
python -m evaluation.upload --base_model "qwen2.5-coder-1.5b" --target "PELAB-LiU/Text2MQL-Qwen2.5-Coder-1.5B"
"""
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Upload fine-tuned models to Hugging Face Hub")
    parser.add_argument("--base_model", type=str, required=True, help="Base model name (prefix for folders)")
    parser.add_argument("--lang", type=str, default="vql,ocl,java", help="Comma-separated list of languages to upload (default: vql,ocl,java)")
    parser.add_argument("--target", type=str, required=True, help="PELAB-LiU/Text2MQL-<LLM name>")
    

    args = parser.parse_args()
    upload_model_repo(
        base_model=args.base_model,
        languages=[l.strip() for l in args.lang.split(",")],
        target_repo=args.target
    )