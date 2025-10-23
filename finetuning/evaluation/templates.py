import textwrap
from string import Template

# Current finetuning relies on token prediction, thus the prompt for finetuning and the expected continuation (query) should be consistent with the evaluation prompt 

###########################################################################
VQL_QUERY_FULL = Template("""            
```
$metamodel
```
$description
```vql
$query
```
""")
VQL_QUERY_PROMPT = Template("""            
```
$metamodel
```
$description
""")
VQL_QUERY_COMPLETION = Template("""            
```
$metamodel
```
$description
```vql
$header
""")
###########################################################################
OCL_QUERY_FULL = Template("""            
```
$metamodel
```
$description
$signature
```ocl
$query
```
""")
OCL_QUERY_PROMPT = Template("""            
```
$metamodel
```
$description
$signature
""")
OCL_QUERY_COMPLETION = Template("""            
```
$metamodel
```
$description
$header
```ocl
""")
###########################################################################
JAVA_QUERY_FULL = Template("""            
```
$metamodel
```
$description
```java
$query
```
""")
JAVA_QUERY_PROMPT = Template("""            
```
$metamodel
```
$description
""")
JAVA_QUERY_COMPLETION = Template("""            
```
$metamodel
```
$description
```java
$header
""")
###########################################################################
QUERY = Template("""            
```$lang
$query
```
""")
###########################################################################
FULL_QUERY = {
    'vql':VQL_QUERY_FULL,
    'ocl': OCL_QUERY_FULL,
    'java': JAVA_QUERY_FULL
}
PROMPT_QUERY = {
    'vql':VQL_QUERY_PROMPT,
    'ocl': OCL_QUERY_PROMPT,
    'java': JAVA_QUERY_PROMPT
}
COMPLETION_QUERY = {
    'vql':VQL_QUERY_COMPLETION,
    'ocl': OCL_QUERY_COMPLETION,
    'java': JAVA_QUERY_COMPLETION
}