package se.liu.ida.sas.pelab.text2vql.refinery.templates;

import tools.refinery.generator.ModelGenerator;
import tools.refinery.logic.term.truthvalue.TruthValue;
import tools.refinery.store.map.Cursor;
import tools.refinery.store.tuple.Tuple;

public class Result2VQL {
    private final ModelGenerator generator;

    private final Tuple TRUE;
    private final Tuple FALSE;
    public Result2VQL(ModelGenerator generator){
        this.generator = generator;
        Cursor<Tuple, TruthValue> booleans = get("VQLBoolean");
        Tuple tempTrue = null;
        Tuple tempFalse = null;
        while (booleans.move()){
            Tuple current = booleans.getKey();
            if(name(current).equals("TRUE"))
                tempTrue = current;
            if(name(current).equals("FALSE"))
                tempFalse = current;
        }
        TRUE = tempTrue;
        FALSE = tempFalse;
    }
    public String regrow(){
        Cursor<Tuple,TruthValue> patternCursor = get("Pattern");
        StringBuilder builder = new StringBuilder();
        while (patternCursor.move()){
            Tuple patternKey = patternCursor.getKey();
            builder.append("pattern pattern_").append(patternKey.get(0)).append("(TODO){");
            builder.append(System.lineSeparator());

            Cursor<Tuple, TruthValue> bodiesCursor = get("Pattern::bodies", patternKey.get(0));
            boolean first = true;
            while(bodiesCursor.move()){
                Tuple bodiesKey = bodiesCursor.getKey();
                if(!first){
                    builder.append("} or {");
                    builder.append(System.lineSeparator());
                }
                first = false;

                Cursor<Tuple, TruthValue> constraintsCursor = get("PatternBody::constraints",bodiesKey.get(1));
                while(constraintsCursor.move()){
                    Tuple constraint = constraintsCursor.getKey();
                    builder.append(pathExpressionConstraint(constraint.get(1)));
                    builder.append(classifierConstraint(constraint.get(1)));
                    builder.append(patternCompositionConstraint(constraint.get(1)));
                }
            }
            builder.append('}');
            builder.append(System.lineSeparator());
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }
    public String pathExpressionConstraint(int id){
        Cursor<Tuple, TruthValue> cursor = get("PathExpressionConstraint", id);
        StringBuilder builder = new StringBuilder();
        while (cursor.move()){
            Tuple current = cursor.getKey();
            Tuple dst = getFirstDestination("PathExpressionConstraint::sourceType",current.get(0));
            builder.append(name(dst));

            Tuple node = getFirstDestination("PathExpressionConstraint::edge",current.get(0));
            while (node!=null && node!=Tuple.of()){
                Tuple feature = getFirstDestination("FeatureChain::feature",node);
                builder.append('.');
                builder.append(name(feature));
                node = getFirstDestination("FeatureChain::next",node);
            }
            builder.append("(TODO)");
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }
    public String classifierConstraint(int id){
        Cursor<Tuple, TruthValue> cursor = get("ClassifierConstraint", id);
        StringBuilder builder = new StringBuilder();
        while (cursor.move()){
            Tuple current = cursor.getKey();
            Tuple dst = getFirstDestination("ClassifierConstraint::type",current.get(0));
            builder.append(name(dst));
            builder.append("(TODO)");
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }
    public String patternCompositionConstraint(int id){
        Cursor<Tuple, TruthValue> cursor = get("PatternCompositionConstraint", id);
        StringBuilder builder = new StringBuilder();
        while (cursor.move()){
            Tuple current = cursor.getKey();

            boolean isNegative = asBoolean(
                    getFirstDestination("PatternCompositionConstraint::negative",current.get(0)));
            if(isNegative){
                builder.append("neg ");
            }
            Tuple call = getFirstDestination("PatternCompositionConstraint::call",current.get(0));
            builder.append(pathExpressionConstraint(call.get(0)));
            builder.append(classifierConstraint(call.get(0)));
            //builder.append(patterncall(call.get(0)));
            //builder.append(typeCheckConstraint(call.get(0)));
        }
        return builder.toString();
    }
    private Cursor<Tuple, TruthValue> get(String name, Integer... binds){
        Cursor<Tuple, TruthValue> cursor = generator.getPartialInterpretation(
                generator.getProblemTrace().getPartialRelation(name)).getAll();


        return new Cursor<>(){
            private Tuple tuple = null;
            private TruthValue value = null;
            @Override
            public Tuple getKey() {
                return tuple;
            }

            @Override
            public TruthValue getValue() {
                return value;
            }

            @Override
            public boolean isTerminated() {
                return tuple==null || value==null;
            }

            @Override
            public boolean move() {
                while(cursor.move()){
                    Tuple candidate = cursor.getKey();
                    boolean set = true;
                    for(int i=0; i<binds.length; i++){
                        if(binds[i] != null) {
                            set &= (binds[i]==candidate.get(i));
                        }
                    }
                    if(set){
                        tuple = cursor.getKey();
                        value = cursor.getValue();
                        return true;
                    }
                }
                tuple = null;
                value = null;
                return false;
            }
        };
    }
    private Tuple getFirstDestination(String name, Tuple tuple){
        if(tuple.getSize()!=1){
            throw new RuntimeException("Tuple must be size of 1. Got: "+tuple.getSize());
        }
        return getFirstDestination(name, tuple.get(0));
    }
    private Tuple getFirstDestination(String name, int src){
        Cursor<Tuple, TruthValue> cursor = generator.getPartialInterpretation(
                generator.getProblemTrace().getPartialRelation(name)).getAll();
        while(cursor.move()){
            Tuple candidate = cursor.getKey();
            if(candidate.get(0)==src){
                return Tuple.of(candidate.get(1));
            }
        }
        return Tuple.of();
    }
    private Tuple getFirstSource(String name, Tuple tuple){
        if(tuple.getSize()!=1){
            throw new RuntimeException("Tuple must be size of 1. Got: "+tuple.getSize());
        }
        return getFirstSource(name, tuple.get(0));
    }
    private Tuple getFirstSource(String name, int dst){
        Cursor<Tuple, TruthValue> cursor = generator.getPartialInterpretation(
                generator.getProblemTrace().getPartialRelation(name)).getAll();
        while(cursor.move()){
            Tuple candidate = cursor.getKey();
            if(candidate.get(1)==dst){
                return Tuple.of(candidate.get(0));
            }
        }
        return Tuple.of();
    }
    private Tuple getFirst(String name, Integer... binds){
        Cursor<Tuple, TruthValue> cursor = generator.getPartialInterpretation(
                generator.getProblemTrace().getPartialRelation(name)).getAll();
        while(cursor.move()){
            Tuple candidate = cursor.getKey();
            boolean set = true;
            for(int i=0; i<binds.length; i++){
                if(binds[i] != null) {
                    set &= (binds[i]==candidate.get(i));
                }
            }
            if(set){
                return cursor.getKey();
            }
        }
        return null;
    }
    private String name(int id){
        String name = generator.getNodesMetadata().getSimpleName(id);
        int idx = name.lastIndexOf('_');
        if(idx < 0)
            return name;
        return name.substring(0,idx);
    }
    private String name(Tuple tuple){
        if(tuple.getSize()!=1){
            throw new RuntimeException("Tuple must be size of 1. Got: "+tuple.getSize());
        }
        String name = generator.getNodesMetadata().getSimpleName(tuple.get(0));
        int idx = name.lastIndexOf('_');
        if(idx < 0)
            return name;
        return name.substring(0,idx);
    }
    private Boolean asBoolean(Tuple tuple){
        if(tuple.getSize()!=1){
            throw new RuntimeException("Tuple must be size of 1. Got: "+tuple.getSize());
        }
        return asBoolean(tuple.get(0));
    }
    private Boolean asBoolean(int id){
        if(TRUE.get(0) == id)
            return true;
        if(FALSE.get(0) == id)
            return false;
        return null;
    }
}
