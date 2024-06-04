import argparse

import pandas as pd


def main(args):
    results = pd.read_csv(args.csv_results)
    print(f'For {args.csv_results}')
    print(f'Pass@5:{len(results[results["has_correct"]]) / len(results):.2f}')
    print(f'Correct:{len(results[results["has_correct"]])} out of {len(results)}')


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='Present results')
    parser.add_argument('--csv_results', type=str)

    args = parser.parse_args()
    main(args)
