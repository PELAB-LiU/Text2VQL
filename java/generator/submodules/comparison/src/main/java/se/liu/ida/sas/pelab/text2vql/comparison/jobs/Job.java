package se.liu.ida.sas.pelab.text2vql.comparison.jobs;

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.emf.ecore.EObject;

public interface Job extends Callable<List<String>>{
    void configureInstanceModel(EObject model);
    default void dispose(){};
}
