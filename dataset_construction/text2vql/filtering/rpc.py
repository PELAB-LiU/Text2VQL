import requests
import json 
import sqlite3
import os.path as path
import os

from text2vql.util.args import makeParser
from text2vql.seed.util import AttrDict
from text2vql.util.processor import  ConcurrentPipelineProcessor,Iterable2Queue,Queue2Iterable

class JavaHTTPSyntaxCheck:
    def __init__(self, db, endpoint="http://localhost:63028", timeout=60):
        self.timeout = timeout
        self.endpoint = endpoint
        self.db = db

    def getUncheckedQueries(self, conn):
        cursor = conn.cursor()

        cursor.execute("""SELECT c.id, c.metamodel, c.lang, c.signat, c.pattern, cl.cluster 
                          FROM chatgpt c 
                          LEFT JOIN clusters cl ON c.metamodel = cl.model 
                          WHERE c.syntax IS NULL """)
        return cursor.fetchall()
        
    def update(slef, conn, id, syntax, diagnostics):
        cursor = conn.cursor()
        cursor.execute("UPDATE chatgpt SET syntax = ?, diagnostics = ? WHERE id = ?", (syntax, diagnostics, id))
        conn.commit()

    def evaluate(self, entry):
        query, domain, lang, cluster = entry

        domain_file_mane = path.splitext(path.basename(domain))[0]
        resp = requests.post(f"{self.endpoint}/{lang}", json={
            "query": query,
            "metamodel": f"metamodels/2-jars/{cluster}_{domain_file_mane}.ecore",
            "jar": f"metamodels/2-jars/{cluster}_{domain_file_mane}.jar",
            "wd": os.getcwd()
        }, timeout=self.timeout)
        resp.raise_for_status()
        data = resp.json()
        return AttrDict(data)
    
    def evaluate2(self, unchecked):
        return unchecked, self.evaluate((unchecked[4], unchecked[1], unchecked[2], unchecked[5]))
    
    def processUnchecked(self):
        with sqlite3.connect(self.db) as conn:
            with ConcurrentPipelineProcessor(self.evaluate2, Iterable2Queue(self.getUncheckedQueries(conn)), 10) as sim:
                for unchecked, response in Queue2Iterable(sim.output()):
                    self.update(conn, unchecked[0], response.isCorrect, response.diagnostics)
#            for uncheked in self.getUncheckedQueries(conn):
#                response = self.evaluate((uncheked[4], uncheked[1], uncheked[2], uncheked[5]))
#                self.update(conn, uncheked[0], response.isCorrect, response.diagnostics)

if __name__ == "__main__":
    parser = makeParser()
    parser.add_argument('--dataset', type=str, default='vql', help='one of [vql, ocj, java]')
    parser.add_argument('--feat', type=str, default='normal', help='one of [normal, disjunction, type, find, aggregate, negation]')
    args = parser.parse_args()


    checker = JavaHTTPSyntaxCheck(args.db)
    checker.processUnchecked()
    