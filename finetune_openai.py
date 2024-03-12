import argparse
import json

from datasets import load_dataset
from openai import File, FineTuningJob

INSTRUCTION_FINETUNE_OPENAI = """Given the following meta-model:
{new_metamodel}

Write the query in Viatra Query Language that satisfies the following natural language description:
{nl_description}
The query must have the following header. You can use auxiliary patterns if needed.
{header}
"""


def generate_input_message(metamodel, nl, header):
    return INSTRUCTION_FINETUNE_OPENAI.format(
        new_metamodel=metamodel,
        nl_description=nl,
        header=header
    )


def generate_sample(sample):
    metamodel = sample['metamodel_definition']
    nl = sample['nl']
    pattern = sample['pattern']
    header = pattern.split('\n')[0]

    input_message = generate_input_message(metamodel, nl, header)

    return {
        "messages": [{"role": "system", "content": "You are an expert in Viatra Query Language."},
                     {"role": "user", "content": input_message},
                     {"role": "assistant", "content": pattern}]
    }


def main(args):
    if args.mode == 'generate_file':
        dataset = load_dataset(args.dataset)
        parsed_train = [generate_sample(sample)
                        for sample in dataset["train"].shuffle(seed=123).select(range(0, 5000))]
        parsed_val = [generate_sample(sample) for sample in dataset["test"]]
        with open("train_openai.jsonl", "w") as jsonl_file:
            # Iterate over the list of dictionaries
            for j, item in enumerate(parsed_train):
                save = json.dumps(item)
                if j != len(parsed_train) - 1:
                    jsonl_file.write(save + "\n")
                else:
                    jsonl_file.write(save)

        with open("val_openai.jsonl", "w") as jsonl_file:
            # Iterate over the list of dictionaries
            for j, item in enumerate(parsed_val):
                save = json.dumps(item)
                if j != len(parsed_val) - 1:
                    jsonl_file.write(save + "\n")
                else:
                    jsonl_file.write(save)
    elif args.mode == 'upload_file':
        creation_train = File.create(
            file=open("train_openai.jsonl", "rb"),
            purpose="fine-tune"
        )
        creation_val = File.create(
            file=open("val_openai.jsonl", "rb"),
            purpose="fine-tune"
        )
        print(creation_train)
        print(creation_val)

        with open('train_file_status.json', 'w') as f:
            json.dump(creation_train, f)
        with open('val_file_status.json', 'w') as f:
            json.dump(creation_val, f)

    elif args.mode == 'fine_tune':
        with open('train_file_status.json') as f:
            d = json.load(f)
            id_train = d["id"]
        with open('val_file_status.json') as f:
            d = json.load(f)
            id_val = d["id"]

        training_status = FineTuningJob.create(training_file=id_train,
                                               validation_file=id_val,
                                               model="gpt-3.5-turbo")
        with open('training_status.json', 'w') as f:
            json.dump(training_status, f)

        print(training_status)


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='ChatGPT baseline')
    parser.add_argument('--mode', type=str, choices=['generate_file',
                                                                   'upload_file',
                                                                   'fine_tune'])
    parser.add_argument('--dataset', default='antolin/text2vql')

    args = parser.parse_args()
    main(args)
