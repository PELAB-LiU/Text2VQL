import os
import unittest

import openai

from text2vql.metamodel import MetaModel
from text2vql.postprocessor import postprocess
from text2vql.template import get_formatted_query, get_instruction

openai.api_key = os.getenv("OPENAI_API_KEY")

METAMODEL = MetaModel('test_metamodels/yakindu_simplified.ecore')
METAMODEL2 = MetaModel('test_metamodels/relational.ecore')
QUERY1 = """pattern transition(t : Transition, src : Vertex, trg : Vertex) {
	Transition.source(t, src);
	Transition.target(t, trg);
}"""
NL1 = "Transitions with their sources and targets"
QUERY2 = """pattern entryInRegion(r1 : Region, e1 : Entry) {
	Region.vertices(r1, e1);
}"""
NL2 = "All entries with their regions"
PAIRS = [(QUERY1, NL1), (QUERY2, NL2)]


class TestMetaModel(unittest.TestCase):
    def test_metamodel(self):
        print(METAMODEL.get_metamodel_info())

    def test_template(self):
        queries = []
        for j, (q, nl) in enumerate(PAIRS):
            queries.append(get_formatted_query(j + 1, nl, q))
        queries = '\n'.join(queries)
        instruction = get_instruction(METAMODEL.get_metamodel_info(), queries, 5, METAMODEL2.get_metamodel_info())
        print(instruction)

        # example request
        response = openai.Completion.create(
            engine="text-davinci-003",
            prompt=[instruction],
            max_tokens=512,
            temperature=0
        )
        response = response["choices"][0]["text"]
        print(postprocess(response))


if __name__ == '__main__':
    unittest.main()
