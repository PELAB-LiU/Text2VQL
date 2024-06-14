
# Text2VQL: Training and running the models

## Hardware and libraries

All the experiments were run in an Ubuntu machine that has an NVIDIA A5000 GPU (24Gb). The fine-tuning procedure
filled almost all the GPU capacity. Therefore, for fine-tuning, you need a GPU with at least 24Gb of memory.
On the other hand, the evaluation/inference took less memory and we believe that having 8-12Gbs should be enough.

The fast way to install all the libraries is to load the environment.

```bash
conda env create -f environment_training.yml
conda activate text2vql-training
pip install openai==0.28.1
```

However, we are not sure that this will work for all machines. Therefore, you may need to install each dependency
separately. Particularly, you will need to install pytorch (we used 2.1.0) + cuda (we used 12.1) together with several pip libraries.

```bash
conda install pytorch==2.1.0 torchvision==0.16.0 torchaudio==2.1.0 pytorch-cuda=12.1 -c pytorch -c nvidia
```
```bash
pip install -r requirements.txt
```

## Fine-tuning OS models

To fine-tune the models using our Text2VQL dataset, `train.py` is your script. The script assumes that the dataset is 
in a HF repository. Note also that `train.py` contains a lot of arguments representing the hyperparameters of the
training phase. In the paper, we run the fine-tuning with the following arguments:

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

```bash
python test_model_railway.py --base_model deepseek-ai/deepseek-coder-6.7b-base --checkpoint PELAB-LiU/deepseek-coder-6.7b-base-Text2VQL-LoRA
python test_model_railway.py --base_model deepseek-ai/deepseek-coder-1.3b-base --checkpoint PELAB-LiU/deepseek-coder-1.3b-base-Text2VQL-LoRA
python test_model_railway.py --base_model codellama/CodeLlama-7b-hf --checkpoint PELAB-LiU/CodeLlama-7b-hf-Text2VQL-LoRA
```

To run the baselines of the paper (i.e., in-context learning model) and get the results, you can employ the `test_os_base.py` script.
```bash
python test_os_base.py --base_model deepseek-ai/deepseek-coder-6.7b-base --mode random
python test_os_base.py --base_model codellama/CodeLlama-7b-hf --mode random
python test_os_base.py --base_model deepseek-ai/deepseek-coder-1.3b-base --mode random
```

Finally, to run ChatGPT over our test meta-model with an in-context learning approach, we used the following command: 
```bash
export OPENAI_API_KEY="KEY"
python test_railway_openai.py --prompt_type fs --mode random
```
