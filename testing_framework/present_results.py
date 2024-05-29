import argparse

import pandas as pd


def main(args):
    results = pd.read_csv(args.csv_results)

    print(f'Pass@5:{len(results[results["has_correct"]]) / len(results)}')
    print(f'Correct:{len(results[results["has_correct"]])} out of {len(results)}')

    outputs = pd.read_csv(args.csv_results)

    for c in set(outputs["construct"]):
        m = outputs[outputs["construct"] == c]
        print(f'Pass@5 {c}:{len(m[m["has_correct"]]) / len(m)}')

    # train benchmark queries
    merged_bench = outputs[outputs["train_benchmark"].notna()]

    print(f'Pass@5 TB:{len(merged_bench[merged_bench["has_correct"]]) / len(merged_bench)}')


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='Present results')
    parser.add_argument('--csv_results', type=str)
    parser.add_argument('--csv_outputs', type=str)

    args = parser.parse_args()
    main(args)
