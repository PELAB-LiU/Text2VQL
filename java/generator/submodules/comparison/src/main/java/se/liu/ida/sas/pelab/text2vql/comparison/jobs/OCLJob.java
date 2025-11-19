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
import org.eclipse.ocl.pivot.utilities.ParserException;
import org.eclipse.ocl.pivot.values.InvalidValueException;
import org.eclipse.ocl.types.TupleType;
import org.eclipse.ocl.util.Tuple;
import org.eclipse.ocl.xtext.essentialocl.EssentialOCLStandaloneSetup;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;
import se.liu.ida.sas.pelab.text2vql.ocl.StatelessSyntaxCheckPivotOCL.OCLParsed;
import se.liu.ida.sas.pelab.text2vql.ocl.StatelessSyntaxCheckPivotOCL;

public class OCLJob implements Job {
    private Query qut;
    private OCLParsed query;
    private Syntax syntax;
    private Map<TupleType<?, ?>, List<String>> typekeys = new HashMap<>();

    static {
        EssentialOCLStandaloneSetup.doSetup();
    }

    public OCLJob(Resource metamodel, Query query) {
        this.qut = query;
        StatelessSyntaxCheckPivotOCL checker = new StatelessSyntaxCheckPivotOCL() {
        };
        try {
            String fixedQuery = query.query();
            if (fixedQuery.startsWith("The query should return a ")) {
                fixedQuery = "-- " + fixedQuery;
            }
            this.query = checker.parse(fixedQuery, metamodel);
            this.syntax = new Syntax(true, new String[] {});
        } catch (ParserException e) {
            //System.out.println("Error?");
            this.syntax = new Syntax(false, new String[] { e.getMessage(), e.getStackTrace().toString() });
            return;
        }
    }

    private Copier trace = new Copier();
    private Map<EObject, EObject> reverse = new HashMap<>();
    private EObject instanceModel;

    public void configureInstanceModel(EObject model) {
        if (query == null) {
            return;
        }

        trace.clear();
        reverse.clear();
        instanceModel = trace.copy(model);
        trace.copyReferences();
        trace.entrySet().forEach(it -> reverse.put(it.getValue(), it.getKey()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Job.MatchSet call() throws Exception {
        try {
            if (query == null) {
                return null;
            }
            query.env().dispose();
            // query() creates a query from the OCLExpression to avoid a caching bug.
            Object result = query.query().evaluateUnboxed(instanceModel);

            List<String> matches = new LinkedList<>();
            if (result instanceof Collection collection) {
                collection.forEach(element -> {
                    matches.add(processSignleMatch(element));
                });
            } else {
                matches.add(processSignleMatch(result));
            }
            Collections.sort(matches);
            return new Job.MatchSet(this.qut.id(), matches, null);
        } catch(InvalidValueException e){
            return new Job.MatchSet(this.qut.id(), List.of("E_"+e.toString()), null);
        } catch (Exception e) {
            return new Job.MatchSet(this.qut.id(), null, e);
        }
    }

    public List<String> getSortedTupleKeys(TupleType<?, ?> type) {
        List<String> keys = new ArrayList<>(type.oclProperties().size());
        type.oclProperties().forEach(property -> {
            EStructuralFeature attribute = (EStructuralFeature) property;
            keys.add(attribute.getName());
        });
        Collections.sort(keys);
        return keys;
    }

    public String processSignleMatch(Object match) {
        if (match instanceof Tuple tuple) {
            StringBuilder builder = new StringBuilder();
            builder.append("M");

            List<String> keys = typekeys.computeIfAbsent(tuple.getTupleType(), this::getSortedTupleKeys);
            for (String key : keys) {
                builder.append('_').append(processValue(tuple.getValue(key)));
            }
            return builder.toString();
        } else {
            // There can be a problem if the condition is a global true/false.
            // Viatra might use a query like pattern foo(), wich would be mepped to M
            // OCL (invariant-like queries) maps the same to either M_ or M_true/M_false
            // Possibly fix at java
            return "M_" + processValue(match);
        }
    }

    public String processValue(Object value) {
        if (value instanceof EObject eobj) {
            EObject original = reverse.get(eobj);
            if(original!=null){
                return "@" + original.hashCode();
            } else {
                return "!No original:"+eobj;
            }
            
        } else {
            return Objects.toString(value);
        }
    }

    public void dispose() {
        if (this.query != null) {
            this.query.env().dispose();
        }
    }

    @Override
    public boolean hasSyntaxError() {
        return query == null;
    }

    @Override
    public Query getQuery() {
        return this.qut;
    }

    @Override
    public Syntax getSyntaxResult() {
        return this.syntax;
    }
}
