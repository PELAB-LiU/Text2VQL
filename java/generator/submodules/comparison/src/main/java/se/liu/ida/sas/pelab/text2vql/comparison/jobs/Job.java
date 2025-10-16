package se.liu.ida.sas.pelab.text2vql.comparison.jobs;

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.emf.ecore.EObject;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;

public interface Job extends Callable<List<String>>{
    boolean hasSyntaxError();
    void configureInstanceModel(EObject model);
    Query getQuery();
    default void dispose(){};
}
