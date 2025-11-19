package se.liu.ida.sas.pelab.text2vql.comparison.jobs;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.emf.ecore.EObject;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;

public interface Job extends Callable<Job.MatchSet>{
    boolean hasSyntaxError();
    void configureInstanceModel(EObject model);
    Query getQuery();
    default void dispose(){};

    Syntax getSyntaxResult();
    public static record Syntax(boolean syntax, String[] diagnostics){
        @Override
        public final String toString() {
            return "Syntax[syntax="+syntax+", diagnostics="+Arrays.toString(diagnostics);
        }
    };

    /**
     * 
     */
    public static record MatchSet(int id, List<String> matches, Exception e){
        public boolean hasError(){return e!=null;}
        public boolean execuded(){return e==null;}
    };
    public static record ErrorResult(Exception e) {
    }
    public static record Semantics(boolean semantics, boolean nullpointer, String indicator){};
    public static record Result(int id, Syntax syntax, Semantics semantics){};
    default String identify(){
        return this.getClass().getSimpleName()+"#"+this.getQuery().id();
    }
}
