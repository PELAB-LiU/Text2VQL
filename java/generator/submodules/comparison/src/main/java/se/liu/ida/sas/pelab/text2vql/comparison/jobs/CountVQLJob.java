package se.liu.ida.sas.pelab.text2vql.comparison.jobs;

import java.util.concurrent.Callable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.EMFScope;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;
import se.liu.ida.sas.pelab.text2vql.vql.StatelessVQLSyntaxCheck;

public class CountVQLJob implements Callable<Integer> {
    public PatternParsingResults patterns;
    private IQuerySpecification<?> main;
    private AdvancedViatraQueryEngine engine;
    private Resource resource;

    public CountVQLJob(Resource metamodel, Query query){
        StatelessVQLSyntaxCheck checker = new StatelessVQLSyntaxCheck(){};
        this.patterns = checker.parse(metamodel, query.query());

        if(!this.patterns.hasError()){
            this.main = this.patterns.getQuerySpecification(query.entry()).orElseGet(()-> null);
            if(this.main==null){
                return;
            }

            resource = new ResourceImpl();
            engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(resource));
        } else {
            this.patterns.getAllDiagnostics().forEach(System.out::println);
        }
    }

    public void configureInstanceModel(EObject model){
        if(main==null || engine==null){
            return;
        }
        var trace = new Copier();
        EObject instanceModel = trace.copy(model);
        trace.copyReferences();

        resource.getContents().clear();
        resource.getContents().add(instanceModel);
    }
    @Override
    public Integer call() throws Exception { 
        if(main==null || engine==null){
            return null;
        }
        var matcher = engine.getMatcher(this.main);
        return matcher.countMatches();
    }

    public void dispose(){
        if(engine!=null && !engine.isDisposed()){
            engine.dispose();
            engine = null;
        }
    }
}
