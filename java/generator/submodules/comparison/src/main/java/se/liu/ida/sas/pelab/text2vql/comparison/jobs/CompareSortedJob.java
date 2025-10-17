package se.liu.ida.sas.pelab.text2vql.comparison.jobs;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.List;

public record CompareSortedJob(Future<List<String>> expected, Future<List<String>> actual) implements Callable<CompareSortedJob.ComparisonResult>{
    
    @Override
    public ComparisonResult call() throws Exception {
        List<String> exp = expected.get();
        List<String> act = actual.get();
        if(exp.size()!=act.size()){
            return new ComparisonResult(false, this);
        }
        for(int i=0; i<exp.size(); i++){
            if(!exp.get(i).equals(act.get(i))){
                return new ComparisonResult(false, this);
            }
        }
        return new ComparisonResult(true, this);
    }
    public List<String> results() throws InterruptedException, ExecutionException{
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
