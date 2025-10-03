package se.liu.ida.sas.pelab.text2vql.comparison;

import java.io.File;
import java.lang.module.Configuration;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.resource.Resource;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;
import se.liu.ida.sas.pelab.text2vql.comparison.input.Request;
import se.liu.ida.sas.pelab.text2vql.comparison.input.TestCase;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.JavaJob;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.Job;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.OCLJob;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.VQLJob;

public class StatelessTestExecutor {
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public void serveRequest(Request request){

        //for()

    }
    public void serveDomain(Configuration domain, Stream<TestCase> tests){

        //tests.forEach(test -> serveTest(List<File> instances, test));
    }
    public void serveTest(List<EObject> instances, TestCase test, File domainjar) throws InterruptedException, ExecutionException, MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Resource metamodels = new ResourceImpl();
        Resource metamodelsWithEcore = new ResourceImpl();
        metamodelsWithEcore.getContents().add(EcoreUtil.copy(EcorePackage.eINSTANCE));

        VQLJob truth = new VQLJob(metamodels, test.truth().entry(), test.truth().query());
        
        List<Job> jobs = new ArrayList<Job>(test.vql().length+test.ocl().length);

        for(Query query : test.vql()){
            jobs.add(new VQLJob(metamodels, query.entry(), query.query()));
        }
        for(Query query : test.ocl()){
            jobs.add(new OCLJob(metamodels, query.query()));
        }
        for(Query query : test.java()){
            jobs.add(new JavaJob(metamodelsWithEcore, query.entry(), query.query(), domainjar));
        }

        for(EObject instance : instances){
            truth.configureInstanceModel(instance);
            Future<List<String>> result = executor.submit(truth);

            Map<Job, Future<List<String>>> results = new HashMap<>();
            jobs.forEach(job -> {
                job.configureInstanceModel(instance);
                results.put(job, executor.submit(job));
            });


            System.err.println("Truth: "+result.get().toString());
            results.entrySet().forEach(it -> {
                try {
                    System.err.println("\tResult ("+it.getKey().getClass().getSimpleName()+"): "+it.getValue().get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            
        }

        jobs.forEach(Job::dispose);
        
    }
}
