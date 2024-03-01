INSTRUCTION_NL_QUERY = """Given the following meta-model:
{example_metamodel}
Some example queries in Viatra Query Language are:
{example_queries}

Now write {number} similar queries (with the same VQL constructs) for the following meta-model and follow the same format as the one in the example:
{new_metamodel}
"""

NL_QUERY = """
{idx}. {nl_description}

```viatraql
{query}
```"""

NL = "{idx}. {nl_description}"


def get_instruction_nl_queries(example_metamodel, example_queries, number, new_metamodel):
    return INSTRUCTION_NL_QUERY.format(
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
