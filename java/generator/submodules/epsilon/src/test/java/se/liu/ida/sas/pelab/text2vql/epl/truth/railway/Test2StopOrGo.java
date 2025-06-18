package se.liu.ida.sas.pelab.text2vql.epl.truth.railway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.epl.execute.model.PatternMatchModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.epl.truth.railway.util.Util;

public class Test2StopOrGo extends Util{
private String query = 
"""
pattern stopOrGo 
    semaphore: Semaphore from: Semaphore.all.select(sema | sema.signal = Signal#STOP or sema.signal = Signal#GO) {
}
""";
    @Test
    public void testGo() throws Exception{
        var module = new EplModule();
        module.parse(query);
        System.out.println( module.getDebugInfo());
        var instance = new InMemoryEmfModel(makeModel("Go"));
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));
        System.out.println("Raw");
        System.out.println(results);

        assertEquals(1, results.getMatches().size());
    }

    @Test
    public void testStop() throws Exception{
        var module = new EplModule();
        module.parse(query);
        System.out.println( module.getDebugInfo());
        var instance = new InMemoryEmfModel(makeModel("Stop"));
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));
        System.out.println("Raw");
        System.out.println(results);

        assertEquals(1, results.getMatches().size());
    }

    @Test
    public void testFail() throws Exception{
        var module = new EplModule();
        module.parse(query);
        System.out.println( module.getDebugInfo());
        var instance = new InMemoryEmfModel(makeModel("Failure"));
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));
        System.out.println("Raw");
        System.out.println(results);

        assertEquals(0, results.getMatches().size());
    }

    private Resource makeModel(String signal){
        var resource = new ResourceImpl();
        var container = make("RailwayContainer");
        resource.getContents().add(container);

        var region = packageHelper.make(container, "regions", "Region");
        var segment = packageHelper.make(region, "elements","Segment");
        var semaGO = packageHelper.make(segment, "semaphores", "Semaphore");
        packageHelper.makeEnum(semaGO, "signal", "Signal::"+signal.toUpperCase());
        
        return resource;
    }
}
