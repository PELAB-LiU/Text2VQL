import sqlite3
import random
import pandas as pd
from typing import List, Optional, Union
import logging
import argparse

from text2vql.util.args import makeParser

def getData(db, lang, n):
    sql = """
        SELECT 
            c.id, 
            c.metamodel,
            m.definition, 
            c.descript, 
            c.pattern, 
            c.signat
        FROM chatgpt AS c
        JOIN metamodels AS m
          ON c.metamodel = m.model
        WHERE c.lang = ? 
          AND c.syntax = 1
        ORDER BY RANDOM()
        LIMIT ?
    """
    with sqlite3.connect(db) as conn:
        return pd.read_sql_query(sql, conn, params=(lang, n))

def save(db, file, langs, n):
    with pd.ExcelWriter(file, engine="openpyxl") as writer:
        for lang in langs:
            df = getData(db, lang, n)
            df.to_excel(writer, index=False, sheet_name=lang)  

if __name__ == "__main__":
    parser = makeParser()
    parser.add_argument('--count', type=str, default='50', help='Number of queries to select')
    parser.add_argument('--xlsx', type=str, default='samples.xlsx', help='location of samples to write')
    args = parser.parse_args()

    save(args.db, args.xlsx, ['vql','ocl','java'], args.count) 