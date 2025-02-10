package se.liu.ida.sas.pelab.text2vql.utilities.evaluation;

import org.apache.commons.csv.CSVRecord;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import se.liu.ida.sas.pelab.text2vql.utilities.csv.CSVHandler;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

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
    protected static Pattern regex_object = Pattern.compile("DynamicEObjectImpl@[0-9a-f]+");
    private static final String id = "id";
    private static final String parse = "parse";
    private static final String compare = "compare";
    private static final String pass = "pass";
    private static final String count = "match_count";
    private static final String matches = "matches";
    protected final String truth;
    protected final String regex;
    private final List<File> models;
    private final Iterable<CSVRecord> csv;

    protected final CSVHandler output;
    public MatchSetEvaluator(String truth, String regex, File csv, File modelsDir, File output) throws IOException {
        this.truth = truth;
        this.regex = regex;
        if(modelsDir.isDirectory()){
            models = Arrays.stream(modelsDir.listFiles(f -> f.getName().endsWith(".xmi"))).toList();
        } else {
            models = Arrays.asList(modelsDir);
        }
        this.csv = CSVHandler.loadCSV(csv);

        this.output = new CSVHandler(output, id, parse, compare, pass, count, matches);
    }
    public void run() {
        csv.forEach(line -> {
            try{
                output.put(id, line.get("id"));
                int id = Integer.parseInt(line.get("id"));
                String name = line.get("name");
                /*
                 * Parse queries
                 */
                Query truth = parse(line.get(this.truth), name);
                List<AbstractMap.SimpleEntry<String,Query>> queries = line.toMap().entrySet().stream()
                        .filter(entry -> entry.getKey().matches(regex))
                        .map(entry -> new AbstractMap.SimpleEntry<String, Query>(
                                entry.getKey(),
                                parse(entry.getValue(), name)))
                        .toList();
                /*
                 * Check parsing results
                 */
                ComparisonResult result = new ComparisonResult(line.get("id"), queries.size());
                for(int i=0; i<queries.size(); i++){
                    if(queries.get(i).getValue()!=null){
                        result.parsed(i);
                    }
                }
                /*
                 * Setup query evaluation
                 */
                AbortFlag[] flags = AbortFlag.of(queries.size());
                ResourceSet resourceSet = new ResourceSetImpl();
                //XMIResourceFactory generates identical IDs when loading the same model.
                resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                        "xmi", new XMIResourceFactoryImpl());
                StringBuilder builder = new StringBuilder();
                builder.append("{");
                /*
                 * Evaluate queries over models
                 */
                models.forEach(model -> {
                    System.out.println("[CSV ROW ID="+id+" MODEL="+model.getName()+"]");
                    Resource resource = resourceSet.getResource(URI.createFileURI(model.getPath()),true);//.getContents().getFirst();

                    List<Result> baseline = evaluate(truth, resource);
                    builder.append(model.getName());
                    builder.append(":");
                    builder.append(baseline);
                    builder.append(",");
                    builder.append(System.lineSeparator());
                    result.increment(baseline.size());
                    //FIXME what if query is null?
                    for (int idx=0; idx< queries.size(); idx++){
                        if(flags[idx].shouldContinue()){
                            Query query = queries.get(idx).getValue();

                            List<Result> matches = evaluate(query, resource);

                            boolean equal = matches!=null && compare(baseline, matches);

                            if(!equal){
                                result.different(idx);
                                flags[idx].abort();
                            }
                        }
                    }
                });
                builder.append("}");
                /*
                 * Write data
                 */
                output.put(parse, result.getParse());
                output.put(compare, result.getCompare());
                output.put(pass, result.getPass());
                output.put(count, result.getCount());
                output.put(matches, builder.toString());
            } catch (NumberFormatException e){
                System.out.println("Unable to parse ID as integer. Skipping row. ("+line.get("id")+")");
            } finally {
                output.commit();
            }
        });
    }
    public static boolean isEqual(List<String> l1, List<String> l2){
        Collections.sort(l1);
        Collections.sort(l2);
        if(l1.size()!=l2.size()){
            return false;
        }
        for (int i=0; i<l1.size(); i++){
            if(!l1.get(i).equals(l2.get(i))){
                return false;
            }
        }
        return true;
    }
    abstract protected List<Result> evaluate(Query query, Resource resource);


    abstract protected boolean compare(List<Result> truth, List<Result> got);

    abstract protected Query parse(String query, String top);
}
