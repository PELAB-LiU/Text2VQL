from openai import OpenAI
from text2vql.util.args import makeParser
from text2vql.seed.seed_yakindu import SEED
from text2vql.datasetgen.campaign import CampaignBase
from text2vql.seed.util import AttrDict

import json
import os
import pickle
import json
from datetime import datetime

class Puller:
    def __init__(self):
        self.client = OpenAI()
    
    def pull(self, id):
        status = self.client.batches.retrieve(id)
        if status.output_file_id is not None:
            return status, self.client.files.content(status.output_file_id).text
        else:
            return status, None

    def pulloutput(self, fileid):
        return self.client.files.content(fileid).text
    
if __name__ == "__main__":
    parser = makeParser()
    parser.add_argument('--files', type=str, help='Coma separated list of output files')
    args = parser.parse_args()

    ai = Puller()
    for file in args.files.split(','):
        time = datetime.now().isoformat()
        results = ai.pulloutput(file)
        
        with open(f"chatgpt/response_{time}.jsonl", "w") as f:
            f.write(results)

        data = [AttrDict(json.loads(line)) for line in results.strip().split("\n")]
        with open(f"logs/response_{time}.md", "w") as f:
            for resp in data:
                f.write(f"\n# [ChatGPT Response Log Entry ({resp.response.body.status})]\n")
                outputs = resp.response.body.output
                message = next((msg for msg in outputs if msg.type=="message"), None)
                text = message.content[0].text
                f.write(text)
                    
