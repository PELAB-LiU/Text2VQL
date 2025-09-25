from openai import OpenAI
import textwrap
from string import Template
import argparse
import pickle

from text2vql.util.metamodel import MetaModel

from text2vql.seed.seed_yakindu import SEED


CALL_TEMPLATE = Template("""            
Given the following meta-model:
$seed_metamodel
Some example queries in $expertise are:
$seed_queries

Now write $number different queries (with `$feature`) for the following meta-model and follow the same format as the examples:
$new_metamodel
""")

QUERY_TEMPLATE = Template("""
$idx. $nl_description
Signature: $signature
```$lang
$query
```""")

class ChatGPTAgent:
    def __init__(self, seed, langcode, max_output_tokens=3000, temperature=0.4, model=None, effort="minimal"):
        self.model = model
        self.max_output_tokens = max_output_tokens
        self.temperature = temperature
        self.client = OpenAI()
        self.effort = effort
        self.language = langcode
        self.expertise = seed.language[langcode]
        self.seed = seed

        self.call_template = Template(CALL_TEMPLATE.safe_substitute(seed_metamodel=seed.metamodel.metamodel_info, expertise=self.expertise))
        self.query_template = Template(QUERY_TEMPLATE.safe_substitute(lang=langcode))



    def multiply(self, value: int) -> int:
        # regular method
        return value * 0

    @property
    def template(self):
        return self.call_template
    
    @property
    def template(self):
        return self.call_template

    def populate_template(self, seed_queries, feature, number, target_domain):
        seedq = self.populate_queries(seed_queries)

        return self.call_template.safe_substitute(seed_queries=seedq, feature=feature, number=number, new_metamodel=target_domain.metamodel_info)

    def populate_queries(self, queries):
        seed_queries = []
        for idx, example in enumerate(queries):
            desc = example.description
            query = example[self.language]
            seed = self.query_template.safe_substitute(idx=idx+1, nl_description=desc, signature=query.signature, query=query.query)
            
            seed_queries.append(seed)
        return '\n'.join(seed_queries)

    def make_call_text(self, category, target_domain):
        category_data = self.seed[category]


        return self.populate_template(
            seed_queries=category_data.examples, 
            feature=category_data.feature[self.language], 
            number=5, 
            target_domain=target_domain
        )
    
    def make_call_json(self, category, target_domain):
        query = self.make_call_text(category, target_domain)
        return {
            "model": self.model,
            "instructions": f"You are an expert in {self.expertise}.",
            "input": query,
            "reasoning": {
                "effort": self.effort #minimal, low, medium
            }, 
            #messages=[{"role": "system", "content": },
            #          {"role": "user", "content": query}],
            "max_output_tokens": self.max_output_tokens
        }

    def __call__(self, category, target_domain):
        params = self.make_call_json(category, target_domain)

        response = self.client.responses.create(**params)

        with open("cache/chat_response.pkl", "wb") as f:
            pickle.dump(response, f)
        return params["input"], response.output_text

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Show ChatGPT call text.')
    parser.add_argument('--lang', type=str, default='vql', help='one of [vql, ocj, java]')
    parser.add_argument('--feat', type=str, default='normal', help='one of [normal, disjunction, type, find, aggregate, negation]')
    args = parser.parse_args()

    ai = ChatGPTAgent(SEED, args.lang)
    target = MetaModel('test_metamodel/railway.ecore')
    print(ai.make_call_text(args.feat, target))

#OCL type is acting up