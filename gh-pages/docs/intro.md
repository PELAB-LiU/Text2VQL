---
sidebar_position: 1
---

# Text2VQL
## Teaching a Model Query Language to Open-Source Language Models with ChatGPT

While large language models (LLMs) like ChatGPT has demonstrated impressive capabilities in addressing various software engineering tasks, their use in a model-driven engineering (MDE) context is still in an early stage. Since the technology is proprietary and accessible solely through an API, its use may be incompatible with the strict protection of intellectual properties in industrial models. While there are open-source LLM alternatives, they often lack the power of proprietary models and require extensive data fine-tuning to realize their full potential. Furthermore, open-source datasets tailored for MDE tasks are scarce, posing challenges for training such models effectively.
In this work, we introduce Text2VQL, a framework that generates graph queries captured in the VIATRA Query Language (VQL) from natural language specifications using open-source LLMs. Initially, we create a high-quality synthetic dataset comprising pairs of queries and their corresponding natural language descriptions using ChatGPT and VIATRA parser. Leveraging this dataset, we use parameter-efficient tuning to specialize three open-source LLMs, namely, DeepSeek Coder 1b, DeepSeek Coder 7b, and CodeLlama 7b for VQL query generation. Our experimental evaluation demonstrates that the fine-tuned models outperform the base models in query generation, highlighting the usefulness of our synthetic dataset. Moreover, one of the fine-tuned models achieves performance comparable to ChatGPT.

## Demo

The project includes a small tutorial that demonstrates how to use the fine-tuned large language models from the Text2MQL framework to translate natural language queries into model queries.

### Setup

```sh
git clone https://github.com/PELAB-LiU/Text2VQL.git
git checkout extension
cd Text2VQL/finetuning
pip install -r requirements.txt
pip install --upgrade torch # Sometimes it is necessary
```

### Running the Demo

Open the `evaluation/demo.ipynb` notebook. 

The first cell loads the required dependencies for the demo:

```python
from external import TEXT2VQL_ROOT as ROOT #Load text2vql project to system path.

import os

from transformers import AutoTokenizer, AutoModelForCausalLM
from peft import PeftModel

from text2vql.seed.seed_yakindu import TEXT2VQL_ROOT
from text2vql.util.metamodel import MetaModel
from templates import COMPLETION_QUERY, QUERY
```

The second cell configures the LLM. Set the `base_model_id` to the desired open-source base model and `adapter_id` to the matching fine-tuning adapter. to the corresponding fine-tuned adapter. Note that the adapter must match the selected base model. Finally, set the language to `vql`, `ocl`, or `java`.

The code will then load the base model and the adapter for the selected language.

```python
base_model_id = "codellama/CodeLlama-7b-hf"
adapter_id = "PELAB-LiU/Text2MQL-CodeLlama-7b"
lang = "ocl"

tokenizer = AutoTokenizer.from_pretrained(base_model_id)
base_model = AutoModelForCausalLM.from_pretrained(base_model_id, device_map="auto")

model = PeftModel.from_pretrained(base_model, adapter_id, subfolder=lang)
```

In the final cell, you can load a meta-model from an Ecore file, provide a natural language prompt, and specify the query header. The program will then generate and print the corresponding query.

````
```
abstract class Pseudostate extends Vertex {
}
abstract class Vertex {
	reference Transition[0..*] incomingTransitions;
	reference Transition[0..*] outgoingTransitions;
}
class Region {
	reference Vertex[0..*] vertices;
	attribute EString[0..1] name;
}
class Transition {
	reference Vertex[1..1] target;
	reference Vertex[0..1] source;
}
class Statechart extends CompositeElement {
}
class Entry extends Pseudostate {
}
class Synchronization extends Pseudostate {
}
class State extends RegularState, CompositeElement {
}
abstract class RegularState extends Vertex {
}
abstract class CompositeElement {
	reference Region[0..*] regions;
}
class Choice extends Pseudostate {
}
class Exit extends Pseudostate {
}
class FinalState extends RegularState {
}

```
Find states with at least 2 outgoing transition.
Set<RegularState>
```ocl
RegularState.allInstances()->select(s |
    s.outgoingTransitions->size() >= 2
)
```
````

