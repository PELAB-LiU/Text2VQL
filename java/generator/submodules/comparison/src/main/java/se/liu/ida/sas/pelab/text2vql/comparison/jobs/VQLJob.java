package se.liu.ida.sas.pelab.text2vql.comparison.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.EMFScope;

import se.liu.ida.sas.pelab.text2vql.vql.StatelessVQLEvaluation;
import se.liu.ida.sas.pelab.text2vql.vql.StatelessVQLSyntaxCheck;

public class VQLJob implements Job {
    private PatternParsingResults patterns;
    private IQuerySpecification<?> main;
    private AdvancedViatraQueryEngine engine;
    private Resource resource;
    private List<String> parameterNames;
    public VQLJob(Resource metamodel, String main, String query){
        StatelessVQLSyntaxCheck checker = new StatelessVQLSyntaxCheck(){};
        this.patterns = checker.parse(metamodel, query);

        if(!this.patterns.hasError()){
            this.main = this.patterns.getQuerySpecification(main).orElseGet(()-> null);
            if(this.main==null){
                return;
            }
            this.parameterNames = new ArrayList<>(this.main.getParameterNames());
            Collections.sort(this.parameterNames);

            resource = new ResourceImpl();
            engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(resource));
        }
    }
    
    private Copier trace = new Copier();
    private Map<EObject, EObject> reverse = new HashMap<>();
    public void configureInstanceModel(EObject model){
        if(main==null || engine==null){
            return;
        }

        trace.clear();
        reverse.clear();
        EObject instanceModel = trace.copy(model);
        trace.copyReferences();
        trace.entrySet().forEach(it -> reverse.put(it.getValue(), it.getKey()));
        resource.getContents().clear();
        resource.getContents().add(instanceModel);
    }
    @Override
    public List<String> call() throws Exception { 
        if(main==null || engine==null){
            return null;
        }

        List<String> matches = new LinkedList<>();
        var matcher = engine.getMatcher(this.main); 
        matcher.forEachMatch(match -> {
            StringBuilder builder = new StringBuilder();
            builder.append("M");
            for(String param : this.parameterNames){
                builder.append('_');
                Object value = match.get(param);
                if(value instanceof EObject eobj){
                    builder.append('@').append(reverse.get(eobj).hashCode());
                } else {
                    builder.append(value);
                }
            }
            matches.add(builder.toString());
        });
        Collections.sort(matches);
        return matches;
    }

    public void dispose(){
        if(engine!=null && !engine.isDisposed()){
            engine.dispose();
            engine = null;
        }
    }
}
