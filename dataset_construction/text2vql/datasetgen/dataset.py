from datetime import datetime

from text2vql.util.args import makeParser
from text2vql.datasetgen.batchgen import Batcher
from text2vql.datasetgen.campaign import CampaignBase

if __name__ == "__main__":
    parser = makeParser()
    parser.add_argument('--lang', type=str, default='vql,ocl,java', help='coma separated list of [vql, ocj, java]')
    parser.add_argument('--feat', type=str, default='normal,disjunction,type,find,aggregate,negation', help='one of [normal, disjunction, type, find, aggregate, negation]')
    parser.add_argument('--domains', type=int, default=3, help='Maximum number of domains to use per language per feature')
    parser.add_argument('--model', type=str, default='gpt-5-nano', help='ChatGPT model')
    parser.add_argument('--reasoning', type=str, default='minimal', help='ChatGPT reasoning level')
    parser.add_argument('--mdlog', type=bool, default=False, help='Create markdown log of requests')
    args = parser.parse_args()


    for lang in args.lang.split(","):
        print(lang)
        batch = Batcher()
        for feat in args.feat.split(","):
            ai = CampaignBase(args.db, lang, feat, maxdomains=args.domains, model=args.model, reasoning=args.reasoning)
            batch.add(ai)
        local_name = f"chatgpt/batch_{lang}_{datetime.now().isoformat()}.jsonl"
        #batch.prepare(local_name)
        batch.send_save_start(local_name)

    