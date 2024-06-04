import argparse

import pandas as pd
from plotnine import *


def main(args):
    eval = pd.read_csv(args.eval)
    profiles = pd.read_csv(args.profiles)
    merged_df = pd.merge(eval, profiles, on='id', how='inner')

    # print(review_counts)

    # Create the boxplot
    boxplot = (ggplot(merged_df, aes(x='has_correct', y='lines')) + geom_boxplot()
               + labs(x='is correct?',
                      y='# effective lines of code'))

    # Show the plot
    print(boxplot)
    boxplot.save("boxplot_deepseek.pdf")


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser(description='Present results rq3')
    parser.add_argument('--eval', default='eval/deepseek-ai-deepseek-coder-6.7b-base_lora_eval.csv', type=str)
    parser.add_argument('--profiles', default='profiles/profiles_truth.csv', type=str)

    args = parser.parse_args()
    main(args)
