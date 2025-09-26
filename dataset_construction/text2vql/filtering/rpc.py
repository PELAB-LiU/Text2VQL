import requests
import json 

from text2vql.util.args import makeParser

class JavaHTTPSyntaxCheck:
    def __init__(self, endpoint="http://localhost:63028", timeout=60):
        self.timeout = timeout
        self.endpoint = endpoint

    def evaluate(self, entry):
        query, domain, lang = entry
        
        payload = json.dumps({
            "query": query,
            "metamodel": domain
        })
        resp = requests.post(f"{self.endpoint}/{lang}", json=payload, timeout=self.timeout)
        resp.raise_for_status()
        data = resp.json()

        return data
    
    def __call__(self, entry):
        try:
            return self.evaluate(entry)
        except Exception as e:
            return e

if __name__ == "__main__":
    parser = makeParser()
    parser.add_argument('--dataset', type=str, default='vql', help='one of [vql, ocj, java]')
    parser.add_argument('--feat', type=str, default='normal', help='one of [normal, disjunction, type, find, aggregate, negation]')
    args = parser.parse_args()


    checker = JavaHTTPSyntaxCheck()
    for int in range(10):
        print(checker((int,"Hello","vql")))