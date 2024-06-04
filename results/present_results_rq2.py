import argparse

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from plotnine import *
from sklearn.metrics import cohen_kappa_score


def main(args):
    profiles = pd.read_csv(args.csv_results)

    # plot effective lines of code
    plot = (
            ggplot(profiles, aes(x='lines')) +  # Specify the column to be plotted on the y-axis
            geom_bar()  # Add boxplot layer
            + labs(x='# effective lines of code', y='# queries')
    )

    # Display the boxplot
    print(plot)
    plot.save("lines.pdf")

    for c in ['auxiliary', 'or', 'find', 'neg_find']:
        interest = profiles[profiles[c] == 1]
        print(f"{c} 1 occurrence {len(interest)}")
        interest = profiles[profiles[c] == 2]
        print(f"{c} 2 occurrences {len(interest)}")
        interest = profiles[profiles[c] > 2]
        print(f"{c} >2 occurrences {len(interest)}")


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='Present results rq2')
    parser.add_argument('--csv_results', default='profiles/profiles_raw.csv', type=str)

    args = parser.parse_args()
    main(args)
