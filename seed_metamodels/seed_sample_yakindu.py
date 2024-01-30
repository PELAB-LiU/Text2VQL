from text2vql.metamodel import MetaModel

SEED_METAMODEL = MetaModel('seed_metamodels/yakindu_simplified.ecore')

NL1 = "Transitions with their sources and targets"
QUERY1 = """pattern transition(t : Transition, src : Vertex, trg : Vertex) {
	Transition.source(t, src);
	Transition.target(t, trg);
}"""

NL2 = "All entries with their regions"
QUERY2 = """pattern entryInRegion(r1 : Region, e1 : Entry) {
	Region.vertices(r1, e1);
}"""

NL3 = "Regions that have several entries"
QUERY3 = """pattern regionWithSeveralEntries(r1 : Region) {
    Region.vertices(r1, e1);
    Region.vertices(r1, e2);
    e1 != e2;
}"""

NL4 = "All transitions that point to an entry"
QUERY4 = """pattern transitionToEntry(t : Transition) {
    Entry(e);
    Transition.target(t, e);
}"""

NL5 = "Entries that do not have outgoing transitions"
QUERY5 = """pattern entryWithoutOutgoingTransitions(e : Entry) {
    neg find transition(_, e, _);
}

//auxiliary pattern
pattern transition(t : Transition, src : Vertex, trg : Vertex) {
	Transition.source(t, src);
	Transition.target(t, trg);
}"""

NL6 = "Entries that have multiple outgoing transitions"
QUERY6 = """pattern entryWithMultipleOutgoingTransitions(e : Entry) {
    Entry.outgoingTransitions(e, t1);
	Entry.outgoingTransitions(e, t2);
	t1!=t2;
}"""

NL7 = "All exit states with at least one outgoing transition"
QUERY7 = """pattern exitStateWithOutgoingTransition(e : Exit) {
    Exit.outgoingTransitions(e, _);
}"""

NL8 = "Regions that have no states"
QUERY8 = """pattern noStateInRegion(region: Region) {
	neg find stateInRegion(region, _);
}

//auxiliary pattern
pattern stateInRegion(region: Region, state: State) {
	Region.vertices(region, state);
}"""

NL9 = "States that are either entry or synchronization state"
QUERY9 = """pattern entryOrSynchronizedState(s : Pseudostate) {
    Entry(s);
} or {
    Synchronization(s);
}"""

NL10 = "Statecharts that have at least one region"
QUERY10 = """pattern statechartWithRegion(sc : Statechart) {
    Statechart.regions(sc, _);
}"""

PAIRS = [(QUERY3, NL3), (QUERY4, NL4),
         (QUERY5, NL5), (QUERY6, NL6), (QUERY7, NL7), (QUERY8, NL8),
         (QUERY9, NL9)]
COMPLEX_PAIRS = [(QUERY9, NL9), (QUERY8, NL8), (QUERY5, NL5), (QUERY3, NL3), (QUERY6, NL6)]
SIMPLE_PAIRS = [(QUERY4, NL4), (QUERY7, NL7), (QUERY1, NL1), (QUERY2, NL2), (QUERY10, NL10)]
SEED_SAMPLE = (SEED_METAMODEL, PAIRS)
