from text2vql.metamodel import MetaModel

SEED_METAMODEL = MetaModel('test_metamodels/yakindu_simplified.ecore')

NL1 = "Transitions with their sources and targets"
QUERY1 = """pattern transition(t : Transition, src : Vertex, trg : Vertex) {
	Transition.source(t, src);
	Transition.target(t, trg);
}"""

NL2 = "All entries with their regions"
QUERY2 = """pattern entryInRegion(r1 : Region, e1 : Entry) {
	Region.vertices(r1, e1);
}"""

NL3 = "All regions that have several entries"
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

NL5 = "All entries that do not have outgoing transitions"
QUERY5 = """//auxiliary pattern
pattern transition(t : Transition, src : Vertex, trg : Vertex) {
	Transition.source(t, src);
	Transition.target(t, trg);
}

pattern entryWithoutOutgoingTransitions(e : Entry) {
    neg find transition(_, e, _);
}"""

NL6 = "Entries that have multiple outgoing transitions"
QUERY6 = """pattern entryWithMultipleOutgoingTransitions(e : Entry) {
    Entry.outgoingTransitions(e, t1);
	Entry.outgoingTransitions(e, t2);
	t1!=t2;
}"""

NL7 = "Exit states with at least one outgoing transition"
QUERY7 = """pattern exitStateWithOutgoingTransition(e : Exit) {
    Exit.outgoingTransitions(e, _);
}"""

NL8 = "Regions that have no states"
QUERY8 = """//auxiliary pattern
pattern stateInRegion(region: Region, state: State) {
	Region.vertices(region, state);
}

pattern noStateInRegion(region: Region) {
	neg find StateInRegion(region, _);
}"""

NL9 = "States that are either entry or synchronization state"
QUERY9 = """pattern entryOrSynchronizedState(s : Pseudostate) {
    Entry(s);
} or {
    Synchronization(s);
}"""

PAIRS = [(QUERY3, NL3), (QUERY4, NL4),
         (QUERY5, NL5), (QUERY6, NL6), (QUERY7, NL7), (QUERY8, NL8),
         (QUERY9, NL9)]
SEED_SAMPLE = (SEED_METAMODEL, PAIRS)
