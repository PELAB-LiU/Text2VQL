package se.liu.ida.sas.pelab.text2vql.comparison.jobs;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public record CompareSortedJob(Future<Job.MatchSet> expected, Future<Job.MatchSet> actual) implements Callable<CompareSortedJob.ComparisonResult>{
    
    @Override
    public ComparisonResult call() throws Exception {
        Job.MatchSet exp = expected.get();
        try{
            Job.MatchSet act = actual.get(30, TimeUnit.MINUTES);
            if(act.hasError()){
                throw new ExecutionException(act.e());
            }
            if(exp.matches().size()!=act.matches().size()){
                return new ComparisonResult(false, this);
            }
            for(int i=0; i<exp.matches().size(); i++){
                if(!exp.matches().get(i).equals(act.matches().get(i))){
                    return new ComparisonResult(false, this);
                }
            }

            return new ComparisonResult(true, this);

        } catch(TimeoutException e) {
            actual.cancel(true);
            return new ComparisonResult(false, this);
        } catch(ExecutionException e){
            System.out.println(actual.get().id());
            e.getCause().printStackTrace(System.out);
            return null;
        }
    }
    public Job.MatchSet results() throws InterruptedException, ExecutionException{
        return actual.get();
    }
    public static record ComparisonResult(boolean isEqual, CompareSortedJob job) {
        @Override
        public final String toString() {
            try {
                return isEqual+ " "+job.results();
            } catch (InterruptedException | ExecutionException e) {
                return isEqual +" "+null;
            }
        }
    }
}
