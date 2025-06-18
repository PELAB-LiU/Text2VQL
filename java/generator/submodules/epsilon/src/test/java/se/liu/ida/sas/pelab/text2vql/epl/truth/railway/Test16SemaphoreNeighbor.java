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



public class Test16SemaphoreNeighbor extends Util{
private String query = 
"""
pattern SemaphoreNeighbor
    semaphore: Semaphore, 
    route1: Route from: Route.all.select(rt | rt.exit = semaphore), 
    route2: Route from: Route.all.select(rt | rt.entry != semaphore), 
    sensor1: Sensor from: route1.requires, 
    sensor2: Sensor from: route2.requires,
    te1: TrackElement from: TrackElement.all.select(te | te.monitoredBy.contains(sensor1)), 
    te2: TrackElement from: TrackElement.all.select(te | te.monitoredBy.contains(sensor2))  {

    match: te1.connectsTo.contains(te2) and route1 != route2
}
""";

    @Test
    public void test0() throws Exception{
        var module = new EplModule();
        module.parse(query);
        
        var instance = new InMemoryEmfModel(makeModel0(false));
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
        
        var instance = new InMemoryEmfModel(makeModel0(true));
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));
        System.out.println("Raw");
        System.out.println(results);

        assertEquals(1, results.getMatches().size());
    }

    private Resource makeModel0(boolean self){
        var resource = new ResourceImpl();

        var container = make("RailwayContainer");
        resource.getContents().add(container);
        
        var region = make(container, "regions", "Region");
        var sensor = make(region, "sensors", "Sensor");
        var segment = make(region, "elements", "Segment");
        var sema = make(segment, "semaphores", "Semaphore");

        link(segment, "monitoredBy", sensor);

        if(self){
            link(segment, "connectsTo", segment);
        } else {
            var other = make(region, "elements", "Segment");
            link(other, "monitoredBy", sensor);
            link(segment, "connectsTo", other);
        }
        var route1 = make(container, "routes", "Route");
        var route2 = make(container, "routes", "Route");
        link(route1, "requires", sensor);
        link(route2, "requires", sensor);
        link(route1, "exit", sema);

        
        return resource;
    }
}
