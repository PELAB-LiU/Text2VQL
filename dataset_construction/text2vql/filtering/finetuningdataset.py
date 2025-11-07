import sqlite3
import pandas as pd
from datasets import Dataset

from text2vql.util.metamodel import MetaModel
from text2vql.util.args import makeParser

class FinetuningDatasetGenerator:
    def __init__(self, db, lang, generation=None):
        self.db = db
        self.lang = lang
        self.generation = generation

    def getSamples(self):
        with sqlite3.connect(self.db) as conn:
            args = (self.lang,)
            query = """
                SELECT chatgpt.metamodel, metamodels.definition, chatgpt.descript, chatgpt.signat, chatgpt.pattern
                FROM chatgpt
                JOIN metamodels ON chatgpt.metamodel = metamodels.model
                WHERE chatgpt.lang = ? AND chatgpt.syntax = 1
            """
            if self.generation is not None:
                args += (self.generation,)
                query = f"{query} AND generation = ?"

            return pd.read_sql_query(query, conn, params=args)

    def saveDataset(self, basename, split=0.2, seed=123):
        df = self.getSamples()
        dataset = Dataset.from_pandas(df)
        
        #dataset.to_json(args.output)
        #dataset.to_csv(args.output_csv)

        dataset = dataset.train_test_split(test_size=split, seed=seed)

        dataset["train"].to_json(f"{basename}_{self.lang}_train.jsonl")
        dataset["test"].to_json(f"{basename}_{self.lang}_test.jsonl")

if __name__ == '__main__':
    parser = makeParser()
    parser.add_argument('--lang', type=str, default='vql', help='Target language for finetuning dataset. One of vql, ocl or java.')
    parser.add_argument('--basename', type=str, default='./text2vql', help='Base name for saving the finetuning dataset. This code is ugly.')
    args = parser.parse_args()

    gen = FinetuningDatasetGenerator(args.db, args.lang, None)
    gen.saveDataset(args.basename)
    