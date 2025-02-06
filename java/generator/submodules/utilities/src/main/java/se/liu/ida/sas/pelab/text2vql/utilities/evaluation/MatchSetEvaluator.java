package se.liu.ida.sas.pelab.text2vql.utilities.evaluation;

import org.apache.commons.csv.CSVRecord;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import se.liu.ida.sas.pelab.text2vql.utilities.csv.CSVHandler;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Evaluates queries specified in a CSV file
 * Columns: versions of the query
 * Rows: different queries
 *
 * Configuration arguments
 * - column name for ground truth query
 * - column names for queries to be evaluated
 * @param <Query>
 */
public abstract class MatchSetEvaluator<Query, Result> {
    protected final String truth;
    protected final String regex;
    private final List<File> models;
    private final Iterable<CSVRecord> csv;
    public MatchSetEvaluator(String truth, String regex, File csv, File modelsDir) throws IOException {
        this.truth = truth;
        this.regex = regex;
        if(modelsDir.isDirectory()){
            models = Arrays.stream(modelsDir.listFiles(f -> f.getName().endsWith(".xmi"))).toList();
        } else {
            models = Arrays.asList(modelsDir);
        }

        this.csv = CSVHandler.loadCSV(csv);
    }
    public void run(){
        csv.forEach(line -> {
            int id = Integer.parseInt(line.get("id"));
            Query truth = parse(line.get(this.truth));
            List<AbstractMap.SimpleEntry<String,Query>> queries = line.toMap().entrySet().stream()
                    .filter(entry -> entry.getKey().matches(regex))
                    .map(entry -> new AbstractMap.SimpleEntry<String, Query>(
                            entry.getKey(),
                            parse(entry.getValue())))
                    .toList();
            ComparisonResult result = new ComparisonResult(line.get("id"), queries.size());
            AbortFlag[] flags = AbortFlag.of(queries.size());
            ResourceSet resourceSet = new ResourceSetImpl();

            //XMIResourceFactory generates identical IDs when loading the same model.
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                    "xmi", new XMIResourceFactoryImpl());

            models.forEach(model -> {
                System.out.println("[CSV ROW ID="+id+" MODEL="+model.getName()+"]");
                EObject root = resourceSet.getResource(URI.createFileURI(model.getPath()),true).getContents().getFirst();

                List<Result> baseline = evaluate(truth, root);

                for (int idx=0; idx< queries.size(); idx++){
                    if(flags[idx].shouldContinue()){
                        Query query = queries.get(idx).getValue();

                        List<Result> matches = evaluate(query, root);

                        //TODO compare
                    }
                }
            });
        });
    }
    abstract protected List<Result> evaluate(Query query, EObject model);


    abstract protected boolean compare(Result truth, Result got);

    abstract protected Query parse(String query);
}
