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

OR_NL3 = "States that are either entry states or have incoming transition"
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

NORMAL_NL5 = "Regions where their name contains normal"
NORMAL_QUERY5 = """pattern regionWithNameNormal(region : Region) {
    Region.name(region, name);
    check(name.contains("normal"));
}"""

NORMAL_PAIRS = [(NORMAL_QUERY1, NORMAL_NL1), (NORMAL_QUERY2, NORMAL_NL2), (NORMAL_QUERY3, NORMAL_NL3),
                (NORMAL_QUERY4, NORMAL_NL4), (NORMAL_QUERY5, NORMAL_NL5)]

TYPE_NL1 = "All vertices that are regular states"
TYPE_QUERY1 = """pattern regularState(state: Vertex){
    RegularState(state);
}"""

TYPE_NL2 = "All pseudostates that are entries"
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

FIND_NL3 = "Vertex that has an incoming transition from a different region and an outgoing transition to a Pseudostate"
FIND_QUERY3 = """pattern regionAndPseudostate(vertex: Vertex){
    Vertex.outgoingTransitions(vertex, transition1);
    find toPseudoState(transition1);
    Vertex.incomingTransitions(vertex, transition2);
    find differentRegion(transition2);
}

//Auxiliary pattern
pattern toPseudoState(transition: Transition){
    Transition.target(transition, state);
    Pseudostate(state);
}

//Auxiliary pattern
pattern differentRegion(transition: Transition){
    Region.vertices(region1, vertex1);
    Transition.source(transition, vertex1);
    Transition.target(transition, vertex2);
    Region.vertices(region2,vertex2);
}"""

FIND_NL4 = "All final states that have an incoming transition from a Pseudostate"
FIND_QUERY4 = """pattern regionWithPseudoToRegular(vertex: FinalState){
    Vertex.incomingTransitions(vertex, transition);
    FinalState(vertex);
    find pseudoToRegular(transition);
}

//Auxiliary pattern
pattern pseudoToRegular(transition: Transition){
    Transition.source(transition, source);
    Pseudostate(source);
    Transition.target(transition, target);
    RegularState(target);
}"""

FIND_PAIRS = [(FIND_QUERY1, FIND_NL1), (FIND_QUERY2, FIND_NL2), (FIND_QUERY3, FIND_NL3), (FIND_QUERY4, FIND_NL4)]

AGG_NL1 = "Regions with at least 4 vertices."
AGG_QUERY1 = """pattern regionWith4OrMoreVertices(region: Region) {
    cnt == count find verticesInRegion(region, _);
    check(cnt>=4);
}

//Auxiliary pattern
pattern verticesInRegion(region: Region, vertex: Vertex){
    Region.vertices(region, vertex);
}"""

AGG_NL2 = "Give me all vertices with at most 3 outgoing transition."
AGG_QUERY2 = """pattern vertexWith3OrLessOutgoingTransitions(vertex: Vertex){
    cnt == count find outgoingTransitions(vertex, _);
    check(cnt<=3);
}

//Auxiliary pattern
pattern outgoingTransitions(vertex: Vertex, transition: Transition){
    Vertex.outgoingTransitions(vertex, transition);
}"""

AGG_NL3 = "Get the number of states"
AGG_QUERY3 = """pattern countStates(cnt: java Integer){
    cnt == count find state(_);
}

//Auxiliary pattern
pattern state(state: Vertex){
    State(state);
}"""

AGG_NL4 = "Vertex with the least incoming transitions"
AGG_QUERY4 = """pattern vertexLeastIncoming(vertex: Vertex, minlength: java Integer) {
    minlength == min find countIncomingTransitions(_, #);
    find countIncomingTransitions(vertex, current);
    current == minlength;
}	

//auxiliary pattern
pattern countIncomingTransitions(vertex : Vertex, cnt: java Integer) {
    cnt == count find incomingHelper(vertex, _);
}

//auxiliary pattern
pattern incomingHelper(vertex: Vertex, transition: Transition){
    Vertex.incomingTransitions(vertex, transition);
}"""

AGG_NL5 = "Check if there are at least 5 entries in the model."
AGG_QUERY5 = """pattern atLeast5Entries(){
    cnt == count find entry(_);
    check(cnt>=5);
}

//Auxiliary pattern
pattern entry(entry: Entry){
    Entry(entry);
}"""

AGG_PAIRS = [(AGG_QUERY1, AGG_NL1), (AGG_QUERY2, AGG_NL2), (AGG_QUERY3, AGG_NL3),
             (AGG_QUERY4, AGG_NL4), (AGG_QUERY5, AGG_NL5)]

NOT_PAIRS = [(FIND_QUERY1, FIND_NL1), (FIND_QUERY2, FIND_NL2),
             (NORMAL_QUERY4, NORMAL_NL4), (NORMAL_QUERY2, NORMAL_NL2)]

ALL_PAIRS = FIND_PAIRS + OR_PAIRS + TYPE_PAIRS + NORMAL_PAIRS + AGG_PAIRS + NOT_PAIRS
