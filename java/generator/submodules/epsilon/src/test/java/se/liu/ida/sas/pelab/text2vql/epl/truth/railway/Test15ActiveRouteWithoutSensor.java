package se.liu.ida.sas.pelab.text2vql.epl.truth.railway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.epl.execute.model.PatternMatchModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.epl.truth.railway.util.Util;



public class Test15ActiveRouteWithoutSensor extends Util{
private String query = 
"""
pattern ActiveRouteWithoutSensor
    route: Route from : Route.all.select(rt |  rt.entry.signal == Signal#GO and rt.requires.isEmpty) {

    match: route.active == true
}
""";

    @Test
    public void test0() throws Exception{
        var module = new EplModule();
        module.parse(query);
        
        var instance = new InMemoryEmfModel(makeModel0(true, "Go", false));
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));
        System.out.println("Raw");
        System.out.println(results);

        assertEquals(1, results.getMatches().size());
    }

    @Test
    public void test1() throws Exception{
        var module = new EplModule();
        module.parse(query);
        
        var instance = new InMemoryEmfModel(makeModel0(true, "Go", true));
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));
        System.out.println("Raw");
        System.out.println(results);

        assertEquals(0, results.getMatches().size());
    }

    private Resource makeModel0(boolean active, String signal, boolean sensor){
        var resource = new ResourceImpl();

        var container = make("RailwayContainer");
        resource.getContents().add(container);
        var region = packageHelper.make(container, "regions", "Region");
        var segment = packageHelper.make(region, "elements", "Segment");
        var sema = packageHelper.make(segment, "semaphores", "Semaphore");
        var route = packageHelper.make(container, "routes", "Route");
        packageHelper.link(route, "entry", sema);

        packageHelper.link(route, "active", active);
        packageHelper.makeEnum(sema, "signal", "Signal::"+signal.toUpperCase());
        if(sensor){
            var s = packageHelper.make(region, "sensors", "Sensor");
            packageHelper.link(route, "requires", s);
        }

        return resource;
    }
}
