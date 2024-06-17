
# Text2VQL: Training and running the models

After establishing a syntactically valid NL-VQL dataset, the next step involves training smaller open-source LLMs.

```bash
cd training
```

## Hardware and libraries

All the experiments were run in an Ubuntu machine that has an NVIDIA A5000 GPU (24GB). The fine-tuning procedure
filled almost all the GPU capacity. Therefore, for fine-tuning, you need a GPU with at least 24GB of memory.
On the other hand, the evaluation/inference took less memory and we believe that having 12GBs may be enough.

The fast way to install all the libraries is to load the environment using [conda](https://docs.anaconda.com/free/miniconda/).

```bash
conda env create -f environment_training.yml
conda activate text2vql-training
pip install openai==0.28.1
```

However, we are not sure that this will work for all machines. Therefore, you may need to install each dependency
separately. Particularly, you will need to install pytorch (we used 2.1.0) + cuda (we used 12.1) together with several pip libraries.

```bash
conda create --name text2vql-training python=3.8
conda activate text2vql-training
conda install pytorch==2.1.0 torchvision==0.16.0 torchaudio==2.1.0 pytorch-cuda=12.1 -c pytorch -c nvidia
pip install -r requirements.txt
```

## Fine-tuning OS models

To fine-tune the models using our Text2VQL dataset, `train.py` is your script. The script assumes that the dataset is 
in a HF repository. Note also that `train.py` contains a lot of arguments representing the hyperparameters of the
training phase. In the paper, we run the fine-tuning with the following arguments (`CUDA_VISIBLE_DEVICES=0` is to restrict 
the training to just the GPU 0): 

#todo command to load the dataset from disk

```bash
CUDA_VISIBLE_DEVICES=0 python train.py --model_name_or_path deepseek-ai/deepseek-coder-6.7b-base --output_dir adapter-deepseek-coder-6.7b-base --max_input_length 512 --max_target_length 256
CUDA_VISIBLE_DEVICES=0 python train.py --model_name_or_path deepseek-ai/deepseek-coder-1.3b-base --output_dir adapter-deepseek-coder-1.3b-base --max_input_length 1024 --max_target_length 256
CUDA_VISIBLE_DEVICES=0 python train.py --model_name_or_path codellama/CodeLlama-7b-hf --output_dir adapter-CodeLlama-7b-hf --max_input_length 512 --max_target_length 256
```

The fine-tuned models will be stored in the `--output_dir` path. We have released the models that we trained
(only for research purposes):

* [PELAB-LiU/deepseek-coder-6.7b-base-Text2VQL-LoRA](https://huggingface.co/PELAB-LiU/deepseek-coder-6.7b-base-Text2VQL-LoRA)
* [PELAB-LiU/deepseek-coder-1.3b-base-Text2VQL-LoRA](https://huggingface.co/PELAB-LiU/deepseek-coder-1.3b-base-Text2VQL-LoRA)
* [PELAB-LiU/CodeLlama-7b-hf-Text2VQL-LoRA](https://huggingface.co/PELAB-LiU/CodeLlama-7b-hf-Text2VQL-LoRA)

Therefore, to run the next section, you do not need to perform the fine-tuning phase as the models will be loaded from
HF.

## Generation

Once the models have been trained, you can evaluate them. To do so, you can use the `test_model_railway.py` script.
The argument `--checkpoint` could indicate either a HF repo or a local folder (best checkpoint of the `--output_dir` 
of the previous script). 

```bash
CUDA_VISIBLE_DEVICES=0 python test_model_railway.py --base_model deepseek-ai/deepseek-coder-6.7b-base --checkpoint PELAB-LiU/deepseek-coder-6.7b-base-Text2VQL-LoRA
CUDA_VISIBLE_DEVICES=0 python test_model_railway.py --base_model deepseek-ai/deepseek-coder-1.3b-base --checkpoint PELAB-LiU/deepseek-coder-1.3b-base-Text2VQL-LoRA
CUDA_VISIBLE_DEVICES=0 python test_model_railway.py --base_model codellama/CodeLlama-7b-hf --checkpoint PELAB-LiU/CodeLlama-7b-hf-Text2VQL-LoRA
```

The output of this script is a `csv`:
```python
f'../results/ai/{args.base_model.replace("/", "-")}_lora.csv'
```
that contains the following columns
```csv
id,construct,train_benchmark,nl,header,truth,0_output,1_output,2_output,3_output,4_output
```
where:
* `id` is the identifier of the test query.
* `construct` indicates the main query construct.
* `train_benchmark` whether it belongs to the train benchmark.
* `nl` is the natural description of the query.
* `truth` is the actual VQL query.
* `i_output` are the five suggested queries.

To run the baselines of the paper (i.e., in-context learning model) and get the results, you can employ the `test_os_base.py` script.
```bash
CUDA_VISIBLE_DEVICES=0 python test_os_base.py --base_model deepseek-ai/deepseek-coder-6.7b-base --mode random
CUDA_VISIBLE_DEVICES=0 python test_os_base.py --base_model codellama/CodeLlama-7b-hf --mode random
CUDA_VISIBLE_DEVICES=0 python test_os_base.py --base_model deepseek-ai/deepseek-coder-1.3b-base --mode random
```

The output of this script is a `csv`:
```python
f'../results/ai/{args.base_model.replace("/", "-")}_fs_{args.mode}.csv'
```
whose format is the same as the previous script.

Finally, to run ChatGPT over our test meta-model with an in-context learning approach, we used the following command: 
```bash
export OPENAI_API_KEY=<KEY>
python test_railway_openai.py --prompt_type fs --mode random
```
The output of this script is a `csv`:
```python
f'../results/ai/chatgpt_{args.prompt_type}_{args.mode}.csv'
```
whose format is similar to the previous ones.
