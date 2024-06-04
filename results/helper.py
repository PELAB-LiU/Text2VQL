import pandas as pd


def merge(basename):
    ai = pd.read_csv("ai/{bn}.csv".format(bn=basename)).set_index('id')
    res = pd.read_csv("eval/{bn}_eval.csv".format(bn=basename)).set_index('id')
    merded = ai.join(res)
    merded.to_csv("merged/{bn}_full.csv".format(bn=basename))


merge("chatgpt-FT")
merge("chatgpt_fs")
merge("chatgpt_fs_random")
merge("chatgpt_fs_temp04")
merge("chatgpt_zs_one")

merge("codellama-CodeLlama-7b-hf_fs")
merge("codellama-CodeLlama-7b-hf_fs_random")
merge("codellama-CodeLlama-7b-hf_lora")

merge("deepseek-ai-deepseek-coder-6.7b-base_fs")
merge("deepseek-ai-deepseek-coder-6.7b-base_fs_random")
merge("deepseek-ai-deepseek-coder-6.7b-base_lora")

merge("deepseek-ai-deepseek-coder-1.3b-base_fs_random")
merge("deepseek-ai-deepseek-coder-1.3b-base_lora")
