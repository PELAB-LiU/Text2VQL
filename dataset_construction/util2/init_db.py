import os
import sqlite3

def init_db(database, schema):
    db_exists = os.path.exists(database)

    # Connect to the database (creates the file if it doesn't exist)
    conn = sqlite3.connect(database)
    cursor = conn.cursor()

    if not db_exists:
        print("Database not found. Creating new database and initializing schema...")

        # Read schema from file
        with open(schema, "r", encoding="utf-8") as f:
            schema_sql = f.read()

        cursor.executescript(schema_sql)
        conn.commit()
    else:
        print("Database already exists. Skipping schema creation.")

    conn.close()
