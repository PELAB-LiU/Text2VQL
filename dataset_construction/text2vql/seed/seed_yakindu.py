from text2vql.util.metamodel import MetaModel
import textwrap
from types import SimpleNamespace
import json
import os
import sys
from text2vql.seed.util import AttrDict

TEXT2VQL_ROOT = os.path.abspath(os.path.dirname(__file__))

while True:
    if os.path.basename(TEXT2VQL_ROOT) == "Text2VQL":
        sys.path.append(os.path.join(TEXT2VQL_ROOT, "dataset_construction"))
        break
    new = os.path.dirname(TEXT2VQL_ROOT)
    if new == TEXT2VQL_ROOT:
        raise FileNotFoundError("Could not find a parent directory named 'Text2VQL'.")
    TEXT2VQL_ROOT = new

#
# Structure
#
#
SEED = AttrDict({
    "metamodel": MetaModel(os.path.join(TEXT2VQL_ROOT,'dataset_construction/seed/yakindu_simplified.ecore')),
    "language": {
        "vql": "Viatra Query Language (VQL)",
        "ocl": "Object Constraint Language (OCL)",
        "java": "Java and Eclipse Modeling Framework (EMF)"
    },
    "disjunction": {
        "feature": {
            "vql": "disjunction",
            "ocl": "boolean logic",
            "java": "boolean logic"
        },
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
                },
                "java": {
                    "signature": "Set<Vertex>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public Set<Vertex> entryOrFinalState(Resource resource){
                                Set<Vertex> result = new HashSet<>();

                                for (EObject root : resource.getContents()) {
                                    collectEntryOrFinal(root, result);
                                }

                                return result;
                            }

                            private void collectEntryOrFinal(EObject eObject, Set<Vertex> result) {
                                if (eObject instanceof Entry || eObject instanceof FinalState) {
                                    result.add((Vertex) eObject);
                                }

                                for (EObject child : eObject.eContents()) {
                                    collectEntryOrFinal(child, result);
                                }
                            }
                        }
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
                },
                "java": {
                    "signature": "Set<Vertex>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public Set<Vertex> incomingOrOutgoing(Resource resource) {
                                Set<Vertex> result = new HashSet<>();

                                for (EObject root : resource.getContents()) {
                                    collectIncomingOrOutgoing(root, result);
                                }

                                return result;
                            }
                        
                            private void collectIncomingOrOutgoing(EObject eObject, Set<Vertex> result) {
                                if (eObject instanceof Vertex) {
                                    Vertex v = (Vertex) eObject;
                                    if (!v.getIncomingTransitions().isEmpty() || !v.getOutgoingTransitions().isEmpty()) {
                                        result.add(v);
                                    }
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectIncomingOrOutgoing(child, result);
                                }
                            }
                        }
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
                },
                "java": {
                    "signature": "Set<Vertex>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public Set<Vertex> entryOrIncoming(Resource resource) {
                                Set<Vertex> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectEntryOrIncoming(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectEntryOrIncoming(EObject eObject, Set<Vertex> result) {
                                if (eObject instanceof Vertex) {
                                    Vertex v = (Vertex) eObject;
                                    if (v instanceof Entry || !v.getIncomingTransitions().isEmpty()) {
                                        result.add(v);
                                    }
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectEntryOrIncoming(child, result);
                                }
                            }
                        }
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
                },
                "java": {
                    "signature": "Set<Match> where Match is a public static record Match(Transition transition1, Transition transition2)",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public static record Match(Transition transition1, Transition transition2) {}
                        
                            public Set<Match> sameSourceOrTarget(Resource resource) {
                                Set<Transition> allTransitions = new HashSet<>();
                                collectTransitions(resource, allTransitions);
                        
                                Set<Match> result = new HashSet<>();
                        
                                Transition[] transitionsArray = allTransitions.toArray(new Transition[0]);
                                int n = transitionsArray.length;
                        
                                for (int i = 0; i < n; i++) {
                                    for (int j = i + 1; j < n; j++) {
                                        Transition t1 = transitionsArray[i];
                                        Transition t2 = transitionsArray[j];
                        
                                        boolean sameSource = t1.getSource() != null 
                                                           && t1.getSource().equals(t2.getSource());
                                        boolean sameTarget = t1.getTarget() != null 
                                                           && t1.getTarget().equals(t2.getTarget());
                        
                                        if (sameSource || sameTarget) {
                                            result.add(new Match(t1, t2));
                                        }
                                    }
                                }
                        
                                return result;
                            }
                        
                            private void collectTransitions(Resource resource, Set<Transition> transitions) {
                                for (EObject root : resource.getContents()) {
                                    collectTransitionsRec(root, transitions);
                                }
                            }
                        
                            private void collectTransitionsRec(EObject eObject, Set<Transition> transitions) {
                                if (eObject instanceof Transition) {
                                    transitions.add((Transition) eObject);
                                }
                                for (EObject child : eObject.eContents()) {
                                    collectTransitionsRec(child, transitions);
                                }
                            }
                        }
                        """)
                }
            }
        ]
    },
    "normal": {
        "feature": {
            "vql": "any language feature",
            "ocl": "any language feature",
            "java": "any language feature"
        },
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
                },
                "java": {
                    "signature": "Set<Match> where Match is a public static record Match(Transition transition, Vertex source, Vertex target)",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public static record Match(Transition transition, Vertex source, Vertex target) {}
                        
                            public Set<Match> transitionMatches(Resource resource) {
                                Set<Match> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectTransitionMatches(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectTransitionMatches(EObject eObject, Set<Match> result) {
                                if (eObject instanceof Transition t) {
                                    Vertex source = t.getSource();
                                    Vertex target = t.getTarget();
                                    if (source != null && target != null) {
                                        result.add(new Match(t, source, target));
                                    }
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectTransitionMatches(child, result);
                                }
                            }
                        }
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
                },
                "java": {
                    "signature": "Set<Match> where Match is a public static record Match(Region region, Entry entry)",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public static record Match(Region region, Entry entry) {}
                        
                            public Set<Match> entryInRegion(Resource resource) {
                                Set<Match> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectEntryInRegion(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectEntryInRegion(EObject eObject, Set<Match> result) {
                                if (eObject instanceof Region r) {
                                    for (Vertex v : r.getVertices()) {
                                        if (v instanceof Entry e) {
                                            result.add(new Match(r, e));
                                        }
                                    }
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectEntryInRegion(child, result);
                                }
                            }
                        }
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
                },
                "java": {
                    "signature": "Set<Region>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public Set<Region> regionWithSeveralEntries(Resource resource) {
                                Set<Region> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectRegionWithSeveralEntries(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectRegionWithSeveralEntries(EObject eObject, Set<Region> result) {
                                if (eObject instanceof Region r) {
                                    int entryCount = 0;
                                    for (Vertex v : r.getVertices()) {
                                        if (v instanceof Entry) {
                                            entryCount++;
                                            if (entryCount >= 2) {
                                                result.add(r);
                                                break;
                                            }
                                        }
                                    }
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectRegionWithSeveralEntries(child, result);
                                }
                            }
                        }
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
                },
                "java": {
                    "signature": "Set<Entry>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public Set<Entry> entryWithMultipleOutgoingTransitions(Resource resource) {
                                Set<Entry> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectEntryWithMultipleOutgoing(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectEntryWithMultipleOutgoing(EObject eObject, Set<Entry> result) {
                                if (eObject instanceof Entry e) {
                                    if (e.getOutgoingTransitions().size() >= 2) {
                                        result.add(e);
                                    }
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectEntryWithMultipleOutgoing(child, result);
                                }
                            }
                        }
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
                },
                "java": {
                    "signature": "Set<Region>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public Set<Region> regionWithNameNormal(Resource resource) {
                                Set<Region> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectRegionWithNameNormal(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectRegionWithNameNormal(EObject eObject, Set<Region> result) {
                                if (eObject instanceof Region r) {
                                    String name = r.getName();
                                    if (name != null && name.contains("normal")) {
                                        result.add(r);
                                    }
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectRegionWithNameNormal(child, result);
                                }
                            }
                        }
                        """)
                }
            }
        ]
    },
    "type": {
        "_comment": "Note: oclIsTypeoOf and oclIsKindOf is not the same. (oclIsKindOf is the equivalent of java instanceof)",
        "feature": {
            "vql": "type constraint",
            "ocl": "type constraint",
            "java": "type constraint"
        },
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
                },
                "java": {
                    "signature": "Set<Vertex>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public Set<Vertex> regularState(Resource resource) {
                                Set<Vertex> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectRegularStates(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectRegularStates(EObject eObject, Set<Vertex> result) {
                                if (eObject instanceof RegularState rs) {
                                    result.add(rs);
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectRegularStates(child, result);
                                }
                            }
                        }
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
                },
                "java": {
                    "signature": "Set<Pseudostate>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public Set<Pseudostate> entry(Resource resource) {
                                Set<Pseudostate> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectEntries(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectEntries(EObject eObject, Set<Pseudostate> result) {
                                if (eObject instanceof Entry e) {
                                    result.add(e);
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectEntries(child, result);
                                }
                            }
                        }
                        """)
                }
            }
        ]
    },
    "find": {
        "feature": {
            "vql": "auxiliary patterns",
            "ocl": "auxiliary variables",
            "java": "auxiliary query functions"
        },
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
                },
                "java": {
                    "signature": "Set<Vertex>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            // Main pattern
                            public Set<Vertex> regionAndPseudostate(Resource resource) {
                                Set<Vertex> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectRegionAndPseudostate(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectRegionAndPseudostate(EObject eObject, Set<Vertex> result) {
                                if (eObject instanceof Vertex vertex) {
                                    
                                    Set<Transition> outgoingToPseudo = toPseudoState(eObject.eResource());
                                    Set<Transition> incomingDifferentRegion = differentRegion(eObject.eResource());
                        
                                    outgoingToPseudo.retainAll(vertex.getOutgoingTransitions());
                                    incomingDifferentRegion.retainAll(vertex.getIncomingTransitions());

                                    if (!outgoingToPseudo.isEmpty() && !incomingDifferentRegion.isEmpty()) {
                                        result.add(vertex);
                                    }
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectRegionAndPseudostate(child, result);
                                }
                            }
                        
                            public Set<Transition> toPseudoState(Resource resource) {
                                Set<Transition> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectToPseudoState(root, result);
                                }
                        
                                return result;
                            }
                            private void collectToPseudoState(EObject eObject, Set<Transition> result) {
                                if (eObject instanceof Transition transition){
                                    Vertex target = transition.getTarget();

                                    if(target instanceof Pseudostate){
                                        result.add(transition);
                                    }
                                }

                                for (EObject child : eObject.eContents()) {
                                    collectToPseudoState(child, result);
                                }
                            }

                            public Set<Transition> differentRegion(Resource resource) {
                                Set<Transition> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectDifferentRegion(root, result);
                                }
                        
                                return result;
                            }
                            private void collectDifferentRegion(EObject eObject, Set<Transition> result) {
                                if (eObject instanceof Transition transition){
                                    Vertex source = transition.getSource();
                                    Vertex target = transition.getTarget();

                                    if (source != null && target != null) {
                                        Region region1 = findRegionContainingVertex(source);
                                        Region region2 = findRegionContainingVertex(target);

                                        if(region1 != null && region2 != null && !region1.equals(region2)){
                                            result.add(transition);
                                        }
                                    }
                                }

                                for (EObject child : eObject.eContents()) {
                                    collectDifferentRegion(child, result);
                                }
                            }
                        
                            private Region findRegionContainingVertex(Vertex vertex) {
                                return vertex.eContainer() instanceof Region region ? region : null;
                            }
                        }
                        """)
                }
            },
            {
                "description": "All final states that have an incoming transition from a Pseudostate",
                "_comment": "Check for a get containing resource for calling other patterns",
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
                },
                "java": {
                    "signature": "Set<FinalState>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public Set<FinalState> regionWithPseudoToRegular(Resource resource) {
                                Set<FinalState> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectRegionWithPseudoToRegular(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectRegionWithPseudoToRegular(EObject eObject, Set<FinalState> result) {
                                if (eObject instanceof FinalState finalState) {
                                    Set<Transition> pseudoToRegular = pseudoToRegular(eObject.eResource());

                                    boolean matches = finalState.getIncomingTransitions().stream()
                                        .anyMatch(t -> pseudoToRegular.contains(t));
                        
                                    if (matches) {
                                        result.add(finalState);
                                    }
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectRegionWithPseudoToRegular(child, result);
                                }
                            }
                        
                            private Set<Transition> pseudoToRegular(Resource resource) {
                                Set<Transition> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectPseudoToRegular(root, result);
                                }
                        
                                return result;
                            }
                            // Auxiliary pattern: pseudoToRegular
                            public void collectPseudoToRegular(EObject eObject, Set<Transition> result) {
                                if(eObject instanceof Transition transition){
                                    Vertex source = transition.getSource();
                                    Vertex target = transition.getTarget();

                                    if (source instanceof Pseudostate && target instanceof RegularState) {
                                        result.add(transition);
                                    }
                                }
                                
                                for (EObject child : eObject.eContents()) {
                                    collectPseudoToRegular(child, result);
                                }
                            }
                        }
                        """)
                }
            }
        ]
    },
    "aggregate": {
        "feature": {
            "vql": "aggregators",
            "ocl": "aggregators",
            "java": "aggregators"
        },
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
                },
                "java": {
                    "signature": "Set<Region>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                                public Set<Region> regionWith4OrMoreVertices(Resource resource) {
                                    Set<Region> result = new HashSet<>();
                            
                                    for (EObject root : resource.getContents()) {
                                        collectRegionWith4OrMoreVertices(root, result);
                                    }
                            
                                    return result;
                                }
                            
                                private void collectRegionWith4OrMoreVertices(EObject eObject, Set<Region> result) {
                                    if (eObject instanceof Region region) {
                                        if (region.getVertices().size() >= 4) {
                                            result.add(region);
                                        }
                                    }
                            
                                    for (EObject child : eObject.eContents()) {
                                        collectRegionWith4OrMoreVertices(child, result);
                                    }
                                }
                        }
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
                },
                "java": {
                    "signature": "Set<Vertex>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public Set<Vertex> vertexWith3OrLessOutgoingTransitions(Resource resource) {
                                Set<Vertex> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectVertexWith3OrLessOutgoingTransitions(root, result);
                                }
                        
                                return result;
                            }
                            
                            private void collectVertexWith3OrLessOutgoingTransitions(EObject eObject, Set<Vertex> result) {
                                if (eObject instanceof Vertex v) {
                                    if (v.getOutgoingTransitions().size() <= 3) {
                                        result.add(v);
                                    }
                                }                            
                                for (EObject child : eObject.eContents()) {
                                    collectVertexWith3OrLessOutgoingTransitions(child, result);
                                }
                            }
                        }
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
                },
                "java": {
                    "signature": "int",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public int countStates(Resource resource) {
                                int count = 0;
                        
                                for (EObject root : resource.getContents()) {
                                    count += countStatesInEObject(root);
                                }
                        
                                return count;
                            }
                        
                            private int countStatesInEObject(EObject eObject) {
                                int count = 0;
                        
                                if (eObject instanceof State) {
                                    count++;
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    count += countStatesInEObject(child);
                                }
                        
                                return count;
                            }
                        }
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
                },
                "java": {
                    "signature": "Set<Match> where Match is a public static record Match(Vertex vertex, int minlength)",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public static record Match(Vertex vertex, int minlength) {}
                        
                            public static record CountIncomingMatch(Vertex vertex, int count) {}
                        
                            public Set<Match> vertexLeastIncoming(Resource resource) {
                                Set<CountIncomingMatch> allVertices = countIncomingTransitions(resource);
                                
                                int minIncoming = allVertices.stream()
                                        .mapToInt(CountIncomingMatch::count)
                                        .min()
                                        .orElse(0);
                        
                                Set<Match> result = new HashSet<>();
                                for (CountIncomingMatch v : allVertices) {
                                    if (v.count() == minIncoming) {
                                        result.add(new Match(v.vertex(), minIncoming));
                                    }
                                }
                        
                                return result;
                            }

                            public Set<CountIncomingMatch> countIncomingTransitions(Resource resource) {
                                Set<CountIncomingMatch> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectCountIncomingTransitions(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectCountIncomingTransitions(EObject eObject, Set<CountIncomingMatch> result) {
                                if (eObject instanceof Vertex v) {
                                    result.add(new CountIncomingMatch(v, v.getIncomingTransitions().size()));
                                }

                                for (EObject child : eObject.eContents()) {
                                    collectCountIncomingTransitions(child, result);
                                }
                            }
                        }
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
                },
                "java": {
                    "signature": "boolean",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public boolean atLeast5Entries(Resource resource) {
                                int count = 0;
                        
                                for (EObject root : resource.getContents()) {
                                    count += countEntries(root);
                                    if (count >= 5) {
                                        return true;
                                    }
                                }
                        
                                return false;
                            }
                        
                            private int countEntries(EObject eObject) {
                                int count = 0;
                        
                                if (eObject instanceof Entry) {
                                    count++;
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    count += countEntries(child);
                                }
                        
                                return count;
                            }
                        }
                        """)
                }
            }
        ]
    },
    "negation": {
        "feature": {
            "vql": "negation",
            "ocl": "negation",
            "java": "negation"
        },
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
                },
                "java": {
                    "signature": "Set<Entry>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public Set<Entry> entryWithoutOutgoingTransitions(Resource resource) {
                                Set<Entry> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectEntryWithoutOutgoing(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectEntryWithoutOutgoing(EObject eObject, Set<Entry> result) {
                                if (eObject instanceof Entry e) {
                                    if (e.getOutgoingTransitions().isEmpty()) {
                                        result.add(e);
                                    }
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectEntryWithoutOutgoing(child, result);
                                }
                            }
                        }
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
                },
                "java": {
                    "signature": "Set<Region>",
                    "query": textwrap.dedent(
                        """\
                        public class Query {
                            public Set<Region> noStateInRegion(Resource resource) {
                                Set<Region> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectNoStateInRegion(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectNoStateInRegion(EObject eObject, Set<Region> result) {
                                if (eObject instanceof Region r) {
                                    if (r.getVertices().isEmpty()) {
                                        result.add(r);
                                    }
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectNoStateInRegion(child, result);
                                }
                            }
                        }
                        """)
                }
            },
        ]
    }, 
})