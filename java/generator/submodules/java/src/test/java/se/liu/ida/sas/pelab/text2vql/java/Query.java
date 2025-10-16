package se.liu.ida.sas.pelab.text2vql.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

public class Query{ 
    /*public List<TrackElement> monitoredBy2Sensors(Resource resource){
        Set<TrackElement> result=new HashSet<>();
        for(EObject root: resource.getContents()){
            collect(root, result);
        }
        return new ArrayList<>(result);
    }
    private void collect(EObject e, Set<TrackElement> result){
        if(e instanceof TrackElement te && te.getMonitoredBy().size()>=2){
            result.add(te);
        }
        for(EObject child: e.eContents()){
            collect(child, result);
        }
    }*/

    /*public List<Semaphore> stopOrGo(Resource resource){
        List<Semaphore> result = new ArrayList<>();
        for (EObject root : resource.getContents()){
            collectStopOrGo(root, result);
        }
        return result;
    }
    private void collectStopOrGo(EObject eObject, List<Semaphore> result) {
        if (eObject instanceof Semaphore){
            Semaphore s = (Semaphore) eObject;
            Signal st = s.getSignal();
            if ((st == Signal.STOP || st == Signal.GO)) {
                result.add(s);
            }
        }
        for (EObject child : eObject.eContents()) collectStopOrGo(child, result);
    }*/
}