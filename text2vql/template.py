INSTRUCTION = """Given the following meta-model:
{example_metamodel}
Some example queries in Viatra Query Language are:
{example_queries}

Now write {number} queries for the following meta-model:
{new_metamodel}
"""

QUERY = """
{idx}. {nl_description}

```viatraql
{query}
```"""


def get_instruction(example_metamodel, example_queries, number, new_metamodel):
    return INSTRUCTION.format(
        example_metamodel=example_metamodel,
        example_queries=example_queries,
        number=number,
        new_metamodel=new_metamodel
    )


def get_formatted_query(idx, nl_description, query):
    return QUERY.format(
        idx=idx,
        nl_description=nl_description,
        query=query
    )
