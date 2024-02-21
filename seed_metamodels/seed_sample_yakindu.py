from text2vql.metamodel import MetaModel

SEED_METAMODEL = MetaModel('seed_metamodels/yakindu_simplified.ecore')

OR_NL1 = "Vertices that are either entry or final state"
OR_QUERY1 = """pattern entryOrFinalState(s : Vertex) {
    Entry(s);
} or {
    FinalState(s);
}"""

OR_NL2 = "Retrieve all vertices that have incoming or outgoing transitions"
OR_QUERY2 = """pattern incomingOrOutgoing(vertex: Vertex){
    Vertex.incomingTransitions(vertex,_);
} or {
    Vertex.outgoingTransitions(vertex,_);
}"""

OR_NL3 = "State that are either entry state or has incoming transition"
OR_QUERY3 = """pattern entryOrIncoming(vertex: Vertex){
    Entry(vertex);
} or {
    Vertex.incomingTransitions(vertex,_);
}"""

OR_NL4 = "Give me all transitions with the same source or same target"
OR_QUERY4 = """pattern sameSourceOrTarget(transition1: Transition, transition2: Transition){
    Transition.source(transition1,source);
    Transition.source(transition2,source);
} or {
    Transition.target(transition1,target);
    Transition.target(transition2,target);
}"""

OR_PAIRS = [(OR_QUERY1, OR_NL1), (OR_QUERY2, OR_NL2), (OR_QUERY3, OR_NL3), (OR_QUERY4, OR_NL4)]

NORMAL_NL1 = "Transitions with their sources and targets"
NORMAL_QUERY1 = """pattern transition(transition : Transition, source : Vertex, vertex : Vertex) {
    Transition.source(transition, source);
    Transition.target(transition, vertex);
}"""

NORMAL_NL2 = "All entries with their regions"
NORMAL_QUERY2 = """pattern entryInRegion(region : Region, entry : Entry) {
    Region.vertices(region, entry);
}"""

NORMAL_NL3 = "Regions that have several vertexes"
NORMAL_QUERY3 = """pattern regionWithSeveralEntries(region : Region) {
    Region.vertices(region, vertex1);
    Region.vertices(region, vertex2);
    vertex1 != vertex2;
}"""

NORMAL_NL4 = "Entries that have multiple outgoing transitions"
NORMAL_QUERY4 = """pattern entryWithMultipleOutgoingTransitions(entry : Entry) {
    Entry.outgoingTransitions(entry, transition1);
    Entry.outgoingTransitions(entry, transition2);
    transition1 != transition2;
}"""

NORMAL_PAIRS = [(NORMAL_QUERY1, NORMAL_NL1), (NORMAL_QUERY2, NORMAL_NL2), (NORMAL_QUERY3, NORMAL_NL3),
                (NORMAL_QUERY4, NORMAL_NL4)]

TYPE_NL1 = "RegularState is a vertex"
TYPE_QUERY1 = """pattern regularState(state: Vertex){
    RegularState(state);
}"""

TYPE_NL2 = "Entry is a Pseudostate"
TYPE_QUERY2 = """pattern entry(entry: Pseudostate){
    Entry(entry);
}"""

TYPE_PAIRS = [(TYPE_QUERY1, TYPE_NL1), (TYPE_QUERY2, TYPE_NL2)]

FIND_NL1 = "Entries that do not have outgoing transitions"
FIND_QUERY1 = """pattern entryWithoutOutgoingTransitions(e : Entry) {
    Entry(e);
    neg find hasOutgoingTransition(e);
}

//auxiliary pattern
pattern hasOutgoingTransition(vertex : Vertex) {
    Vertex.outgoingTransitions(vertex,_);
}"""

FIND_NL2 = "Regions that have no states"
FIND_QUERY2 = """pattern noStateInRegion(region: Region) {
    neg find StateInRegion(region, _);
}

//auxiliary pattern
pattern stateInRegion(region: Region, state: State) {
    Region.vertices(region, state);
}"""

FIND_PAIRS = [(FIND_QUERY1, FIND_NL1), (FIND_QUERY2, FIND_NL2)]

ALL_PAIRS = FIND_PAIRS + OR_PAIRS + TYPE_PAIRS + NORMAL_PAIRS
