import argparse

import pandas as pd
from sklearn.metrics import cohen_kappa_score

import seaborn as sns
import matplotlib.pyplot as plt


def main(args):
    annotations = pd.read_csv(args.csv_results)
    kappa = cohen_kappa_score(annotations['Reviewer1'], annotations['Reviewer2'])
    print(f"Cohen's Kappa coefficient: {kappa:.2f}")

    df = annotations.copy()

    # Define the mapping of old values to new values
    value_mapping = {'yes': 'Correct', 'ok': 'Acceptable', 'spec': 'Spec. error', 'no': 'Reject'}

    # Replace the values in column 'B' using the defined mapping
    df['Agreed'] = df['Agreed'].replace(value_mapping)

    order = ['Correct', 'Acceptable', 'Spec. error', 'Reject']

    df['review'] = pd.Categorical(df['Agreed'], categories=order, ordered=True)
    df = df.sort_values('Agreed')

    review_counts = df['Agreed'].value_counts().reindex(order)

    # print(review_counts)

    sns.set_style("whitegrid")
    plt.figure(figsize=(6, 6))
    plt.pie(review_counts, labels=review_counts.index, autopct='%1.1f%%')
    plt.title('Category distribution')
    plt.savefig('category_distribution.pdf')
    plt.show()


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='Present results rq1')
    parser.add_argument('--csv_results', default='annotation/annotations.csv', type=str)

    args = parser.parse_args()
    main(args)
