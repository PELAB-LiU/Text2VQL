from text2vql.metamodel import MetaModel
import textwrap

#
# Structure
#
#
OCL_SEED = {
    "metamodel": MetaModel('seed/yakindu_simplified.ecore'),
    "or": {
        "examples": [
            {
                "description": "Vertices that are either entry or final state",
                "vql": {
                    "signature": "pattern entryOrFinalState(s : Vertex)",
                    "query": textwrap.dedent(
                        """\
                        pattern entryOrFinalState(s : Vertex) {
                            Entry(s);
                        } or {
                            FinalState(s);
                        }""")
                },
                "ocl": {
                    "signature": "Set(Vertex)",
                    "query": textwrap.dedent(
                        """\
                        Vertex.allInstances()->select(state | 
                            state.oclIsKindOf(Entry) or 
                            state.oclIsKindOf(FinalState)
                        )
                        """)
                }
            },
            {
                "description": "Retrieve all vertices that have incoming or outgoing transitions",
                "vql": {
                    "signature": "pattern incomingOrOutgoing(vertex: Vertex)",
                    "query": textwrap.dedent(
                        """\
                        pattern incomingOrOutgoing(vertex: Vertex){
                            Vertex.incomingTransitions(vertex,_);
                        } or {
                            Vertex.outgoingTransitions(vertex,_);
                        }""")
                },
                "ocl": {
                    "signature": "Set(Vertex)",
                    "query": textwrap.dedent(
                        """\
                        Vertex.allInstances()->select(vertex | 
                            vertex.incomingTransitions->notEmpty() or 
                            vertex.outgoingTransitions->notEmpty()
                        )
                        """)
                }
            },
            {
                "description": "Vertices that are either entry states or have incoming transition",
                "vql": {
                    "signature": "pattern entryOrIncoming(vertex: Vertex)",
                    "query": textwrap.dedent(
                        """\
                        pattern entryOrIncoming(vertex: Vertex){
                            Entry(vertex);
                        } or {
                            Vertex.incomingTransitions(vertex,_);
                        }""")
                },
                "ocl": {
                    "signature": "Set(Vertex)",
                    "query": textwrap.dedent(
                        """\
                        Vertex.allInstances()->select(v | 
                            v.incomingTransitions->notEmpty() or
                            v.oclIsKindOf(Entry)
                        )
                        """)
                }
            },
            {
                "description": "Give me all transitions with the same source or same target",
                "vql": {
                    "signature": "pattern sameSourceOrTarget(transition1: Transition, transition2: Transition)",
                    "query": textwrap.dedent(
                        """\
                        pattern sameSourceOrTarget(transition1: Transition, transition2: Transition){
                            Transition.source(transition1,source);
                            Transition.source(transition2,source);
                        } or {
                            Transition.target(transition1,target);
                            Transition.target(transition2,target);
                        }""")
                },
                "ocl": {
                    "signature": "Bag(Tuple(transition1: Transition,transition2: Transition))",
                    "query": textwrap.dedent(
                        """\
                        Transition.allInstances()->collect(t1 |
                            Transition.allInstances()->select(t2 |
                                t1.source = t2.source or
                                t1.target = t2.target
                            ) -> collect(t2 |
                                Tuple {
                                    transition1 = t1,
                                    transition2 = t2
                                }
                            )
                        )
                        """)
                }
            }
        ]
    },
    "normal": {
        "examples": [
            {
                "description": "Transitions with their sources and targets",
                "vql": {
                    "signature": "pattern transition(transition : Transition, source : Vertex, vertex : Vertex)",
                    "query": textwrap.dedent(
                        """\
                        pattern transition(transition : Transition, source : Vertex, vertex : Vertex) {
                            Transition.source(transition, source);
                            Transition.target(transition, vertex);
                        }
                        """)
                },
                "ocl": {
                    "signature": "Bag(Tuple(transition: Transition,source: Vertex,vertex: Vertex))",
                    "query": textwrap.dedent(
                        """\
                        Transition.allInstances()->collect(t |
                            Tuple {
                                transition = t,
                                source = t.source,
                                vertex = t.target
                            }
                        )
                        """)
                }
            },
            {
                "description": "All entries with their regions",
                "vql": {
                    "signature": "pattern entryInRegion(region : Region, entry : Entry)",
                    "query": textwrap.dedent(
                        """\
                        pattern entryInRegion(region : Region, entry : Entry) {
                            Region.vertices(region, entry);
                        }
                        """)
                },
                "ocl": {
                    "signature": "Bag(Tuple(region: Region,entry: Entry))",
                    "query": textwrap.dedent(
                        """\
                        Region.allInstances()->collect(r |
                            r.vertices->select(v | v.oclIsKindOf(Entry))->collect(e |
                                Tuple {
                                    region = r,
                                    entry = e.oclAsType(Entry)
                                }
                            )
                        )
                        """)
                }
            },
            {
                "description": "Regions that have several vertexes",
                "vql": {
                    "signature": "pattern regionWithSeveralEntries(region : Region)",
                    "query": textwrap.dedent(
                        """\
                        pattern regionWithSeveralEntries(region : Region) {
                            Region.vertices(region, vertex1);
                            Region.vertices(region, vertex2);
                            vertex1 != vertex2;
                        }
                        """)
                },
                "ocl": {
                    "signature": "Set(Region)",
                    "query": textwrap.dedent(
                        """\
                        Region.allInstances()->select(r | r.vertices->size() >= 2)
                        """)
                }
            },
            {
                "description": "Entries that have multiple outgoing transitions",
                "vql": {
                    "signature": "pattern entryWithMultipleOutgoingTransitions(entry : Entry)",
                    "query": textwrap.dedent(
                        """\
                        pattern entryWithMultipleOutgoingTransitions(entry : Entry) {
                            Entry.outgoingTransitions(entry, transition1);
                            Entry.outgoingTransitions(entry, transition2);
                            transition1 != transition2;
                        }
                        """)
                },
                "ocl": {
                    "signature": "Set(Entry)",
                    "query": textwrap.dedent(
                        """\
                        Entry.allInstances()->select(e | e.outgoingTransitions->size() >= 2)
                        """)
                }
            },
            {
                "description": "Regions where their name contains normal",
                "vql": {
                    "signature": "pattern regionWithNameNormal(region : Region)",
                    "query": textwrap.dedent(
                        """\
                        pattern regionWithNameNormal(region : Region) {
                            Region.name(region, name);
                            check(name.contains("normal"));
                        }
                        """)
                },
                "ocl": {
                    "signature": "",
                    "query": textwrap.dedent(
                        """\
                        Region.allInstances()->select(r |
                            r.name.indexOf('normal')>0
                        )
                        """)
                }
            }
        ]
    },
    "type": {
        "_comment": "Note: oclIsTypeoOf and oclIsKindOf is not the same. (oclIsKindOf is the equivalent of java instanceof)",
        "examples": [
            {
                "description": "All vertices that are regular states",
                "vql": {
                    "signature": "pattern regularState(state: Vertex)",
                    "query": textwrap.dedent(
                        """\
                        pattern regularState(state: Vertex){
                            RegularState(state);
                        }
                        """)
                },
                "ocl": {
                    "signature": "Set(Vertex)",
                    "query": textwrap.dedent(
                        """\
                        Vertex.allInstances()->select(v | v.oclIsKindOf(RegularState))
                        """)
                }
            },
            {
                "description": "All pseudostates that are entries",
                "vql": {
                    "signature": "pattern entry(entry: Pseudostate)",
                    "query": textwrap.dedent(
                        """\
                        pattern entry(entry: Pseudostate){
                            Entry(entry);
                        }
                        """)
                },
                "ocl": {
                    "signature": "Set(Pseudostate)",
                    "query": textwrap.dedent(
                        """\
                        Pseudostate.allInstances()->select(p | p.oclIsKindOf(Entry))
                        """)
                }
            }
        ]
    },
    "find": {
        "examples": [
            {
                "description": "Vertex that has an incoming transition from a different region and an outgoing transition to a Pseudostate",
                "vql": {
                    "signature": "pattern regionAndPseudostate(vertex: Vertex)",
                    "query": textwrap.dedent(
                        """\
                        pattern regionAndPseudostate(vertex: Vertex){
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
                            region1 != region2;
                        }
                        """)
                },
                "ocl": {
                    "signature": "Set(Vertex)",
                    "query": textwrap.dedent(
                        """\
                        let differentRegion: Set(Transition) =
                            Transition.allInstances()-> select(transition |
                                Region.allInstances()->exists(r1 |
                                    r1.vertices->includes(transition.source) and
                                        Region.allInstances()->exists(r2 |
                                            r2.vertices->includes(transition.target) and
                                            r1 <> r2
                                        )
                                )
                            ) in
                        
                        let toPseudoState: Set(Transition) = 
                            Transition.allInstances()-> select(transition |
                                transition.target.oclIsKindOf(Pseudostate)
                            ) in
                        
                        Vertex.allInstances()->select(vertex |
                            differentRegion->includes(vertex.incomingTransitions) and
                            toPseudoState->includes(vertex.outgoingTransitions)
                        )
                        """)
                }
            },
            {
                "description": "All final states that have an incoming transition from a Pseudostate",
                "vql": {
                    "signature": "pattern regionWithPseudoToRegular(vertex: FinalState)",
                    "query": textwrap.dedent(
                        """\
                        pattern regionWithPseudoToRegular(vertex: FinalState){
                            Vertex.incomingTransitions(vertex, transition);
                            find pseudoToRegular(transition);
                        }

                        //Auxiliary pattern
                        pattern pseudoToRegular(transition: Transition){
                            Transition.source(transition, source);
                            Pseudostate(source);
                            Transition.target(transition, target);
                            RegularState(target);
                        }
                        """)
                },
                "ocl": {
                    "signature": "Set(FinalState)",
                    "query": textwrap.dedent(
                        """\
                        let pseudoToRegular : Set(Transition) = 
                            Transition.allInstances()->select(t |
                                t.source.oclIsKindOf(Pseudostate) and 
                                t.target.oclIsKindOf(RegularState)
                            ) in 
                        FinalState.allInstances()->select(fs |
                            fs.incomingTransitions->exists(transition |
                                pseudoToRegular->includes(transition)
                            )
                        )
                        """)
                }
            }
        ]
    },
    "aggregate": {
        "examples": [
            {
                "description": "Regions with at least 4 vertices.",
                "vql": {
                    "signature": "pattern regionWith4OrMoreVertices(region: Region)",
                    "query": textwrap.dedent(
                        """\
                        pattern regionWith4OrMoreVertices(region: Region) {
                            cnt == count Region.vertices(region, _);
                            check(cnt>=4);
                        }
                        """)
                },
                "ocl": {
                    "signature": "Set(Region)",
                    "query": textwrap.dedent(
                        """\
                        Region.allInstances()->select(r | 
                            r.vertices->size() >= 4
                        )
                        """)
                }
            },
            {
                "description": "Give me all vertices with at most 3 outgoing transition.",
                "vql": {
                    "signature": "pattern vertexWith3OrLessOutgoingTransitions(vertex: Vertex)",
                    "query": textwrap.dedent(
                        """\
                        pattern vertexWith3OrLessOutgoingTransitions(vertex: Vertex){
                            cnt == count Vertex.outgoingTransitions(vertex, _);
                            check(cnt<=3);
                        }
                        """)
                },
                "ocl": {
                    "signature": "Set(Vertex)",
                    "query": textwrap.dedent(
                        """\
                        Vertex.allInstances()->select(v |
                            v.outgoingTransitions->size() <= 3
                        )
                        """)
                }
            },
            {
                "description": "Get the number of states",
                "vql": {
                    "signature": "pattern countStates(cnt: java Integer)",
                    "query": textwrap.dedent(
                        """\
                        pattern countStates(cnt: java Integer){
                            cnt == count State(_);
                        }
                        """)
                },
                "ocl": {
                    "signature": "Integer",
                    "query": textwrap.dedent(
                        """\
                        State.allInstances()->size()
                        """)
                }
            },
            {
                "description": "Vertex with the least incoming transitions",
                "vql": {
                    "signature": "pattern vertexLeastIncoming(vertex: Vertex, minlength: java Integer)",
                    "query": textwrap.dedent(
                        """\
                        pattern vertexLeastIncoming(vertex: Vertex, minlength: java Integer) {
                            minlength == min find countIncomingTransitions(_, #);
                            find countIncomingTransitions(vertex, current);
                            current == minlength;
                        }	

                        //auxiliary pattern
                        pattern countIncomingTransitions(vertex : Vertex, cnt: java Integer) {
                            cnt == count Vertex.incomingTransitions(vertex, _);
                        }
                        """)
                },
                "ocl": {
                    "signature": "Bag(Tuple(vertex: Vertex,cnt: Integer))",
                    "query": textwrap.dedent(
                        """\
                        let allVertices : Set(Vertex) = Vertex.allInstances(), 
                            vertexToCount : Bag(Tuple(vertex: Vertex, cnt: Integer)) = 
                                allVertices->collect(v | Tuple{vertex = v, cnt = v.incomingTransitions->size()}) in
                        let minlength : Integer = vertexToCount->collect(t | t.cnt)->min() in
                        vertexToCount->select(t | t.cnt = minlength)
                        """)
                }
            },
            {
                "description": "Check if there are at least 5 entries in the model.",
                "vql": {
                    "signature": "pattern atLeast5Entries()",
                    "query": textwrap.dedent(
                        """\
                        pattern atLeast5Entries(){
                            cnt == count Entry(_);
                            check(cnt>=5);
                        }
                        """)
                },
                "ocl": {
                    "signature": "Boolean",
                    "query": textwrap.dedent(
                        """\
                        Entry.allInstances()->size() >= 5
                        """)
                }
            }
        ]
    },
    "negation": {
        "examples": [
            {
                "description": "Entries that do not have outgoing transitions",
                "vql": {
                    "signature": "pattern entryWithoutOutgoingTransitions(e : Entry)",
                    "query": textwrap.dedent(
                        """\
                        pattern entryWithoutOutgoingTransitions(e : Entry) {
                            neg Vertex.outgoingTransitions(e,_);
                        }
                        """)
                },
                "ocl": {
                    "signature": "Set(Entry)",
                    "query": textwrap.dedent(
                        """\
                        Entry.allInstances()->select(e | e.outgoingTransitions->isEmpty())
                        """)
                }
            },
            {
                "description": "Regions that have no states",
                "vql": {
                    "signature": "pattern noStateInRegion(region: Region)",
                    "query": textwrap.dedent(
                        """\
                        pattern noStateInRegion(region: Region) {
                            neg Region.vertices(region, _);
                        }
                        """)
                },
                "ocl": {
                    "signature": "Set(Region)",
                    "query": textwrap.dedent(
                        """\
                        Region.allInstances()->select(r |
                            r.vertices->forAll(v | not v.oclIsKindOf(State))
                        )
                        """)
                }
            },
        ]
    }, 
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