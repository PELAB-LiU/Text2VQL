package se.liu.ida.sas.pelab.text2vql.comparison.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.types.TupleType;
import org.eclipse.ocl.util.Tuple;

import se.liu.ida.sas.pelab.text2vql.ocl.StatelessSyntaxCheckOCL;
import se.liu.ida.sas.pelab.text2vql.ocl.StatelessSyntaxCheckOCL.OCLParsed;


public class OCLJob implements Job {
    private OCLParsed query;
    private Map<TupleType,List<String>> typekeys = new HashMap<>();

    public OCLJob(Resource metamodel, String query){
        StatelessSyntaxCheckOCL checker = new StatelessSyntaxCheckOCL(){};
        try{
            this.query = checker.parse(query, metamodel);
        } catch (ParserException e){
            System.out.println("Error?");
            e.printStackTrace();
            return;
        }
    }
    
    private Copier trace = new Copier();
    private Map<EObject, EObject> reverse = new HashMap<>();
    private EObject instanceModel;
    public void configureInstanceModel(EObject model){
        if(query==null){
            return;
        }

        trace.clear();
        reverse.clear();
        instanceModel = trace.copy(model);
        trace.copyReferences();
        trace.entrySet().forEach(it -> reverse.put(it.getValue(), it.getKey()));
    }

    @Override
    public List<String> call() throws Exception { 
        if(query==null){
            return null;
        }
        query.env().dispose();
        // query() creates a query from the OCLExpression to avoid a caching bug.
        Object result = query.query().evaluate(instanceModel);
        
        List<String> matches = new LinkedList<>();
        if(result instanceof Collection collection){
            collection.forEach(element ->{
                matches.add(processSignleMatch(element));
            });
        } else {
            matches.add(processSignleMatch(result));
        }
        Collections.sort(matches);
        return matches;
    }
    
    public List<String> getSortedTupleKeys(TupleType type){
        List<String> keys = new ArrayList<>(type.oclProperties().size());
        type.oclProperties().forEach(property -> {
            EStructuralFeature attribute = (EStructuralFeature) property;
            keys.add(attribute.getName());
        });
        Collections.sort(keys);
        return keys;
    }
    public String processSignleMatch(Object match){
        if(match instanceof Tuple tuple){
            StringBuilder builder = new StringBuilder();
            builder.append("M");

            List<String> keys = typekeys.computeIfAbsent(tuple.getTupleType(), this::getSortedTupleKeys);
            for(String key: keys){
                builder.append('_').append(processValue(tuple.getValue(key)));
            }
            return builder.toString();
        } else {
            // There can be a problem if the condition is a global true/false.
            // Viatra might use a query like pattern foo(), wich would be mepped to M
            // OCL (invariant-like queries) maps the same to either M_ or M_true/M_false
            // Possibly fix at java
            return "M_"+processValue(match);
        }
    }
    public String processValue(Object value){
        if(value instanceof EObject eobj){
            return "@"+reverse.get(eobj).hashCode();
        } else {
            return Objects.toString(value);
        }
    }

    public void dispose(){
        this.query.env().dispose();
    }
}
