from openai import OpenAI
from text2vql.util.args import makeParser
from text2vql.seed.seed_yakindu import SEED
from text2vql.datasetgen.campaign import CampaignBase
import json
import os
import pickle
from datetime import datetime
from pathlib import Path

class Batcher:
    def __init__(self):
        self.campaigns = []
        self.requests = []
        self.client = OpenAI()
    
    def add(self, campaign):
        self.campaigns.append(campaign)

    def prepare(self, file, mdlog=True):
        self.requests = [rq for campaign in self.campaigns for rq in campaign.list_requests()]
        
        if mdlog:
            with open(f"logs/batchlog_{datetime.now().isoformat()}.md", "w") as f:
                for rq in self.requests:
                    f.write(f"\n# [ChatGPT Request Log Entry ()]\n")
                    f.write(rq["body"]["input"])
        
        with open(file, 'w') as outfile:
            for entry in self.requests:
                json.dump(entry, outfile)
                outfile.write('\n')

    def send_save_start(self, file, mdlog=False):
        if not Path(file).exists():
            self.prepare(file, mdlog)

        with open(file, 'w') as outfile:
            for entry in self.requests:
                json.dump(entry, outfile)
                outfile.write('\n')

        batch_input_file = self.client.files.create(
            file=open(file, "rb"),
            purpose="batch"
        )

        base, ext = os.path.splitext(file)
        new_file = f"{base}_{batch_input_file.id}{ext}"
        os.rename(file, new_file)

        batchobject = self.client.batches.create(
            input_file_id=batch_input_file.id,
            endpoint="/v1/responses",
            completion_window="24h",
            metadata={
                "description": "Test run for Text2VQL barch generation."
            }
        )

        with open(f"logs/batchobj_{batchobject.id}.pkl", "wb") as f:
            pickle.dump(batchobject, f)
        
if __name__ == "__main__":
    parser = makeParser()
    parser.add_argument('--lang', type=str, default='vql', help='one of [vql, ocj, java]')
    parser.add_argument('--feat', type=str, default='normal', help='one of [normal, disjunction, type, find, aggregate, negation]')
    args = parser.parse_args()


    ai = CampaignBase(args.db, args.lang, args.feat, maxdomains=3)
    batch = Batcher()
    batch.add(ai)

    batch.prepare()
    batch.send_save_start("logs/testbat.jsonl")