
from text2vql.datasetgen.chatgpt import ChatGPTAgent
from text2vql.seed.seed_yakindu import SEED
from text2vql.util.args import makeParser
from text2vql.util.metamodel import MetaModel

from datetime import datetime
import sqlite3 

def get_domain_jars(conn, limit=None):
    cursor = conn.cursor()
    if limit is None:
        cursor.execute("SELECT c.model, c.cluster FROM clusters c JOIN domains d ON c.model = d.model WHERE d.compiled = 1")
    else:
        cursor.execute("SELECT c.model, c.cluster FROM clusters c JOIN domains d ON c.model = d.model WHERE d.compiled = 1 LIMIT ?", (limit,))
    return cursor.fetchall()

class CampaignBase:
    def __init__(self, db, lang, feat, maxdomains=None, model="gpt-5-nano", reasoning="minimal"):
        self.db = db
        self.lang = lang
        self.feat = feat
        self.limit = 10000
        self.maxdomains = maxdomains
        if lang=='java':
            self.limit = int(self.limit * 1.5)
        self.agent = ChatGPTAgent(SEED, lang, self.limit, model=model, effort=reasoning)

    def list_requests(self):
        requests = []
        with sqlite3.connect(self.db) as conn: 
            campaignid = f"cp_{self.lang}_{self.feat}"
            for target, cluster in get_domain_jars(conn, self.maxdomains):
                callid = f"{campaignid}_{cluster}"
                metamodel = MetaModel(target)

                print(f"{target} {cluster}")
                params = self.agent.make_call_json(self.feat, metamodel)

                request = {
                    "custom_id": callid, 
                    "method": "POST", 
                    "url": "/v1/responses", 
                    "body": params
                }

                requests.append(request)
        return requests


    def run(self):
        with sqlite3.connect(self.db) as conn: 
            for target, cluster in get_domain_jars(conn, self.maxdomains):
                metamodel = MetaModel(target)
                print(f"{target} {cluster}")
                query, response = self.agent(self.feat, metamodel)

                with open(f"logs/rsl_{self.lang}_{self.feat}_{cluster}.md", "a") as f:
                    f.write(f"\n# [ChatGPT Response Log Entry ({datetime.now().isoformat()})]\n")
                    f.write(f"\n## [ChatGPT Response Log Entry (Query)]\n")
                    f.write(query)
                    f.write(f"\n## [ChatGPT Response Log Entry (Response)]\n")
                    f.write(response)

# Runnable version to test this class
if __name__ == "__main__":
    parser = makeParser()
    parser.add_argument('--lang', type=str, default='vql', help='one of [vql, ocj, java]')
    parser.add_argument('--feat', type=str, default='normal', help='one of [normal, disjunction, type, find, aggregate, negation]')
    args = parser.parse_args()

    ai = CampaignBase(args.db, args.lang, args.feat, maxdomains=3)
    ai.run()
