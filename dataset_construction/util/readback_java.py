import sqlite3
from pathlib import Path

current_dir = os.path.dirname(os.path.abspath(__file__))
if current_dir not in sys.path:
    sys.path.append(current_dir)
from args import parser

def get_domain_jars(conn):
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM samples")
    return cursor.fetchall()

def save_readback(conn, model, value=True):
    cursor = conn.cursor()
    cursor.execute("INSERT OR REPLACE INTO domains (model, compiled) VALUES (?, ?)", (model, value))
    conn.commit()

def readback(db, folder):
    jars_dir = Path(folder)
    with sqlite3.connect(db) as conn: 
        for model, cluster in get_domain_jars(conn):
            ecore_name = Path(model).stem
            jar_file = jars_dir / (f"{cluster}_" + ecore_name+".jar")
            if jar_file.exists():
                save_readback(conn, model)
            else:
                save_readback(conn, model, False)

# Runnable version to load readback to domains
def main():
    args = parser.parse_args()
    db = args.db
    jars = args.jars
    
    readback(db, jars)

if __name__ == "__main__":
    main()