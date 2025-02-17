from text2vql.metamodel import MetaModel
import textwrap

OCL_SEED = {
    "metamodel": MetaModel('seed/yakindu_simplified.ecore'),
    "commons": [
        {
            "description":"",
            "query": textwrap.dedent("""\
                Vertex.allInstances()->select(state | state.oclIsTypeOf(Entry) or state.oclIsTypeOf(FinalState))->collect(state | Tuple{s=state})
                """)
        }
    ] 
}

"""
Given the following metamodel: 

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

Translate the following Viatra query to OCL:

pattern entryOrFinalState(s : Vertex) {
    Entry(s);
} or {
    FinalState(s);
}

Output template:

def: [name of query](): Tuple([literals according to query header]) = [query body]

The output must satisfy the following constraints:
* The output should be an OCL query, not an invariant.
* The OCL pattern shoud return a Tuple matching the header of the viatra query.
"""