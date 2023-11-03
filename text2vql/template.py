INSTRUCTION_NL_QUERY = """Given the following meta-model:
{example_metamodel}
Some example queries in Viatra Query Language are:
{example_queries}

Note that one query could have some auxiliary patterns. Now write {number} queries for the following meta-model:
{new_metamodel}
"""

NL_QUERY = """
{idx}. {nl_description}

```viatraql
{query}
```"""

INSTRUCTION_NL = """Given the following meta-model:
{example_metamodel}
Some example of queries that people could write are:
{example_queries}

Come up with {number} queries for the following meta-model. Be sure that the queries are within the scope of the meta-model.
{new_metamodel}
"""

NL = "{idx}. {nl_description}"


def get_instruction_nl_queries(example_metamodel, example_queries, number, new_metamodel):
    return INSTRUCTION_NL_QUERY.format(
        example_metamodel=example_metamodel,
        example_queries=example_queries,
        number=number,
        new_metamodel=new_metamodel
    )


def get_instruction_nl(example_metamodel, example_queries, number, new_metamodel):
    return INSTRUCTION_NL.format(
        example_metamodel=example_metamodel,
        example_queries=example_queries,
        number=number,
        new_metamodel=new_metamodel
    )


def get_formatted_nl_query(idx, nl_description, query):
    return NL_QUERY.format(
        idx=idx,
        nl_description=nl_description,
        query=query
    )


def get_formatted_nl(idx, nl_description):
    return NL.format(
        idx=idx,
        nl_description=nl_description
    )
