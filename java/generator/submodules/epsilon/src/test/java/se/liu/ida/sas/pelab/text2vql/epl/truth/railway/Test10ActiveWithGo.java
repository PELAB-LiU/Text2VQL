package se.liu.ida.sas.pelab.text2vql.epl.truth.railway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.epl.execute.model.PatternMatchModel;
import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.epl.truth.railway.util.Util;



public class Test10ActiveWithGo extends Util{
private String query = 
"""
pattern GoActiveRoute
    route: Route, 
    semaphore: Semaphore from: route.entry {
    
    match: semaphore.signal == Signal#GO and (route.active == true);
}
""";

    @Test
    public void test0() throws Exception{
        var module = new EplModule();
        module.parse(query);
        
        var instance = new InMemoryEmfModel(makeModel0());
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));
        System.out.println("Raw");
        System.out.println(results);

        assertEquals(1, results.getMatches().size());
    }
    private Resource makeModel0(){
        var resource = new ResourceImpl();

        var container = make("RailwayContainer");
        resource.getContents().add(container);
        var region = make(container, "regions", "Region");
        var track =  make(region, "elements", "Segment");
        var sema = make(track, "semaphores", "Semaphore");
        packageHelper.makeEnum(sema, "signal", "Signal::GO");

        var route = make(container, "routes", "Route");
        link(route, "entry", sema);
        link(route, "active", true);
        
        return resource;
    }

    @Test
    public void test1() throws Exception{
        var module = new EplModule();
        module.parse(query);
        
        var instance = new InMemoryEmfModel(makeModel1());
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));
        System.out.println("Raw");
        System.out.println(results);

        assertEquals(0, results.getMatches().size());
    }
    private Resource makeModel1(){
        var resource = new ResourceImpl();

        var container = make("RailwayContainer");
        resource.getContents().add(container);
        var region = make(container, "regions", "Region");
        var track =  make(region, "elements", "Segment");
        var sema = make(track, "semaphores", "Semaphore");
        packageHelper.makeEnum(sema, "signal", "Signal::STOP");

        var route = make(container, "routes", "Route");
        link(route, "entry", sema);
        link(route, "active", true);
        
        return resource;
    }
}
