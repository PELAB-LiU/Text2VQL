package se.liu.ida.sas.pelab.text2vql.comparison;

import java.io.File;
import java.lang.module.Configuration;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import se.liu.ida.sas.pelab.text2vql.comparison.input.EvaluationResult;
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
    public Map<Integer, EvaluationResult> serveTest(Resource metamodels, List<EObject> instances, TestCase test, File domainjar) throws InterruptedException, ExecutionException, MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Resource metamodelsWithEcore = new ResourceImpl();
        metamodels.getContents().forEach(entry -> metamodelsWithEcore.getContents().add(EcoreUtil.copy(entry)));
        metamodelsWithEcore.getContents().add(EcoreUtil.copy(EcorePackage.eINSTANCE));

        VQLJob truth = new VQLJob(metamodels, test.truth());
        
        Map<Integer, EvaluationResult> evresult = new HashMap<>();

        List<Job> jobs = new ArrayList<Job>(test.vql().length+test.ocl().length);

        for(Query query : test.vql()){
            jobs.add(new VQLJob(metamodels, query));
        }
        for(Query query : test.ocl()){
            jobs.add(new OCLJob(metamodels, query));
        }
        for(Query query : test.java()){
            jobs.add(new JavaJob(metamodelsWithEcore, query, domainjar));
        }

        jobs.forEach(job ->{
            if(job.hasSyntaxError()){
                evresult.put(job.getQuery().id(), EvaluationResult.SYNTAX_ERROR);
            } else {
                evresult.put(job.getQuery().id(), EvaluationResult.PARSED);
            }
        });

        for(EObject instance : instances){
            if(!checkContinuation(evresult)){
                truth.dispose();
                jobs.forEach(Job::dispose);
                return makeAssessment(evresult);
            }

            truth.configureInstanceModel(instance);
            Future<List<String>> result = executor.submit(truth);

            Map<Job, Future<List<String>>> results = new HashMap<>();
            jobs.forEach(job -> {
                if(evresult.get(job.getQuery().id()).canProceedWithEvaluation()){
                    job.configureInstanceModel(instance);
                    results.put(job, executor.submit(job));
                }
                
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

        truth.dispose();
        jobs.forEach(Job::dispose);
        return makeAssessment(evresult);
    }

    public boolean checkContinuation(Map<Integer, EvaluationResult> status){
        for(Entry<Integer, EvaluationResult> entry : status.entrySet()){
            if(entry.getValue().canProceedWithEvaluation()){
                return true;
            }
        }
        return false;
    }

    public Map<Integer, EvaluationResult> makeAssessment(Map<Integer, EvaluationResult> status){
        Map<Integer, EvaluationResult> evresult = new HashMap<>();
        for(Entry<Integer, EvaluationResult> entry : status.entrySet()){
            evresult.put(entry.getKey(), entry.getValue().assessment());
        }
        return evresult;
    }
}
