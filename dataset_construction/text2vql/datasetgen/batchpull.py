from openai import OpenAI
from text2vql.util.args import makeParser
from text2vql.seed.seed_yakindu import SEED
from text2vql.datasetgen.campaign import CampaignBase
import json
import os
import pickle

class Puller:
    def __init__(self):
        self.client = OpenAI()
    
    def pull(self, id):
        status = self.client.batches.retrieve(id)
        if status.output_file_id is not None:
            return status, self.client.files.content(status.output_file_id).text
        else:
            return status, None
    
if __name__ == "__main__":
    parser = makeParser()
    parser.add_argument('--lang', type=str, default='vql', help='one of [vql, ocj, java]')
    args = parser.parse_args()

    ai = Puller()
    print(ai.pull("batch_68d55dd229cc8190b1b6b6c3477540fc"))