import re


def postprocess_nl_queries(input_string):
    # Define regular expressions for description and queries
    description_pattern = re.compile(r'\d+\.\s+(.*?)(?=\n```viatraql|\Z)', re.DOTALL)
    query_pattern = re.compile(r'```viatraql\n(.*?)\n```', re.DOTALL)
    # Find all matches for descriptions and queries
    descriptions = re.findall(description_pattern, input_string)
    queries = re.findall(query_pattern, input_string)
    # Iterate through descriptions and queries to create pairs
    pairs = []
    for i in range(min(len(descriptions), len(queries))):
        description = descriptions[i].strip()
        query = queries[i].strip()
        pairs.append((description, query))
    return pairs


def postprocess_nl(input_string):
    # Define regular expressions for description and queries
    description_pattern = re.compile(r'\d+\.\s+(.*?)\n', re.DOTALL)
    descriptions = re.findall(description_pattern, input_string)
    # Iterate through descriptions and queries to create pairs
    output = []
    for i in range(len(descriptions)):
        description = descriptions[i].strip()
        output.append(description)
    return output
