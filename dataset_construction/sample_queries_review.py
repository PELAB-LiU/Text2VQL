import argparse

from datasets import load_dataset


def main(args):
    dataset = load_dataset(args.dataset)
    print(dataset)
    samples = dataset["train"].shuffle(seed=123).select(range(0, 100))
    samples.to_csv('queries_to_review.csv')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--dataset', type=str, default='antolin/text2vql')
    args = parser.parse_args()
    main(args)
