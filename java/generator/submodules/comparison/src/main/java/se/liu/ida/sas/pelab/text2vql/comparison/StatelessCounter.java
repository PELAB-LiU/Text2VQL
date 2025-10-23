package se.liu.ida.sas.pelab.text2vql.comparison;

import java.lang.module.Configuration;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;
import se.liu.ida.sas.pelab.text2vql.comparison.input.Request;
import se.liu.ida.sas.pelab.text2vql.comparison.input.TestCase;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.CountVQLJob;

public class StatelessCounter {
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public void serveRequest(Request request){

        //for()

    }
    public void serveDomain(Configuration domain, Stream<TestCase> tests){

        //tests.forEach(test -> serveTest(List<File> instances, test));
    }
    public Map<String, Integer> serveTest(Resource metamodels, List<EObject> instances, Query test) throws InterruptedException, ExecutionException, MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{

        CountVQLJob truth = new CountVQLJob(metamodels, test);
        
        Map<String, Integer> evresult = new HashMap<>();

        for(EObject instance : instances){

            truth.configureInstanceModel(instance);
            String model = instance.eResource().getURI().toString();
            int matches = executor.submit(truth).get();
            evresult.put(model, matches);

            System.out.println(model+": #"+matches);
        }
        return evresult;
    }
}
