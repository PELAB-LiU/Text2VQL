import sqlite3
import re
from text2vql.util.args import makeParser

# This code was used to check if metamodels were filtered out due to the error fixed in `Commit f6f211a`.
# Output was: 790 enums in 519 metamodels
# Conclusion: it did not affect the sampling significantly (significant error is like filtering out all enums)

if __name__ == '__main__':
    parser = makeParser()
    args = parser.parse_args()

    enums = 0
    models = 0
    with sqlite3.connect(args.db) as conn:
        query = """
            SELECT domains.model, metamodels.definition
            FROM domains
            JOIN metamodels ON domains.model = metamodels.model
            WHERE domains.compiled = 1
            """
        cursor = conn.execute(query)
        for model, definition in cursor.fetchall():
            models += 1
            enums += sum(1 for line in definition.splitlines() if line.strip().startswith("enum"))
        print(f"{enums} enums in {models} metamodels.")
    