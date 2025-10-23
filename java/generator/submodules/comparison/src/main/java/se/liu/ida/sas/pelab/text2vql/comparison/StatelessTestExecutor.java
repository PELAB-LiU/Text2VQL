package se.liu.ida.sas.pelab.text2vql.comparison;

import java.io.File;
import java.lang.module.Configuration;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
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
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.CompareSortedJob;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.CompareSortedJob.ComparisonResult;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.JavaJob;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.Job;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.OCLJob;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.VQLJob;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.Job.Result;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.Job.Semantics;

public class StatelessTestExecutor {
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public void serveRequest(Request request){

        //for()

    }
    public void serveDomain(Configuration domain, Stream<TestCase> tests){

        //tests.forEach(test -> serveTest(List<File> instances, test));
    }
    public Map<Integer, Result> serveTest(Resource metamodels, List<EObject> instances, TestCase test, File domainjar) throws InterruptedException, ExecutionException, MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Resource metamodelsWithEcore = new ResourceImpl();
        metamodels.getContents().forEach(entry -> metamodelsWithEcore.getContents().add(EcoreUtil.copy(entry)));
        metamodelsWithEcore.getContents().add(EcoreUtil.copy(EcorePackage.eINSTANCE));

        if(1 != metamodels.getContents().size()){
            throw new RuntimeException("Resource was taken!");
        }
        VQLJob truth = new VQLJob(metamodels, test.truth());
        
        Map<Integer, EvaluationResult> evresult = new HashMap<>();
        
        Map<Integer, Job> jobs = new HashMap<Integer, Job>(test.vql().length+test.ocl().length+test.java().length);
        Map<Integer, String> indicator = new HashMap<>();
        Map<Integer, String> nullpointer = new HashMap<>();

        for(Query query : test.vql()){
            jobs.put(query.id(), new VQLJob(metamodels, query));
        }
        for(Query query : test.ocl()){
            jobs.put(query.id(), new OCLJob(metamodels, query));
        }
        for(Query query : test.java()){
            jobs.put(query.id(), new JavaJob(metamodelsWithEcore, query, domainjar));
        }

        jobs.forEach((id, job) ->{
            if(job.hasSyntaxError()){
                evresult.put(id, EvaluationResult.SYNTAX_ERROR);
            } else {
                evresult.put(id, EvaluationResult.PARSED);
            }
        });

        for(EObject instance : instances){
            if(!checkContinuation(evresult)){
                truth.dispose();
                jobs.forEach((id, job) -> job.dispose());
                return makeAssessment(jobs, indicator, nullpointer);
            }

            truth.configureInstanceModel(instance);
            Future<List<String>> result = executor.submit(truth);

            Map<Job, Future<ComparisonResult>> results = new HashMap<>();
            jobs.forEach((id, job) -> {
                if(evresult.get(id).canProceedWithEvaluation()){
                    job.configureInstanceModel(instance);
                    Future<List<String>> future = executor.submit(job);
                    CompareSortedJob comparison = new CompareSortedJob(result, future);
                    results.put(job, executor.submit(comparison));
                }
            });


            System.out.println("Truth (#"+result.get().size()+"): "+result.get().toString());
            results.forEach((job, comparison) ->{
                try{
                    ComparisonResult cr = comparison.get();
                    System.err.println("\tResult ("+job.identify()+"): "+cr);
                    int id = job.getQuery().id();
                    evresult.put(id, evresult.get(id).update(!cr.isEqual()));
                    if(!cr.isEqual()){
                        indicator.put(id, instance.eResource().getURI().toString());
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            
        }

        truth.dispose();
        jobs.forEach((id, job) -> job.dispose());
        return makeAssessment(jobs, indicator, nullpointer);
    }

    public boolean checkContinuation(Map<Integer, EvaluationResult> status){
        for(Entry<Integer, EvaluationResult> entry : status.entrySet()){
            if(entry.getValue().canProceedWithEvaluation()){
                return true;
            }
        }
        return false;
    }

    public Map<Integer, Result> makeAssessment(Map<Integer, Job> jobs, Map<Integer,String> indicators, Map<Integer,String> npe){
        Map<Integer, Result> evresult = new HashMap<>();
        for(Entry<Integer, Job> entry : jobs.entrySet()){
            String error = indicators.getOrDefault(entry.getKey(), null);
            String nulle = npe.getOrDefault(entry.getKey(), null);
            evresult.put(entry.getKey(), 
                new Result(
                    entry.getKey(), entry.getValue().getSyntaxResult(),
                    new Semantics(error==null, nulle!=null, 
                        error!=null ? error : nulle)
                )
            );
        }
        return evresult;
    }
}
