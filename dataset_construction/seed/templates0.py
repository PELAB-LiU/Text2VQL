import textwrap

TEMPLATE = {
    "java": {
        "_comment": "signature is either like public List<Vertex> query(Resource resource) or public List<Tuple> query(Resource resource) with public record Tuple(Vertex vertex)",
        "template":  textwrap.dedent(
                        """\
                        Given the following metamodel: 
                        
                        {metamodel}

                        Provide a java function {signature} to retrieve the following data:

                        {description}

                        Do not use frameworks other than plain java and EMF. Assume that all classes from the provided metamodels are imported.
                        You may write helper functions to the current class.
                        Only show the resulting java class and omit package declaration.
                        """),
    }
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

Provide a java class with a function "public List<Vertex> query(Resource resource)" to retrieve the following data:

Vertices that are either entry states or have incoming transition

Do not use frameworks other than plain java and EMF. Assume that all classes from the provided metamodels are imported.
You may write helper functions to the current class.
Only show the resulting java class and omit package declaration.

"""