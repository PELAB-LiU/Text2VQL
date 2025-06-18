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



public class Test13BigRegion extends Util{
private String query = 
"""
pattern MisalignedSwitchPosition
    region: Region from: Region.all.select(reg | 
        reg.elements.select(track | track.isTypeOf(Segment)).collect(segment | segment.length).sum() >= 50 or
        reg.sensors.size >= 10) {
}
""";

    @Test
    public void test0() throws Exception{
        var module = new EplModule();
        module.parse(query);
        
        var instance = new InMemoryEmfModel(makeModel0(2, 1, 3, 25, 30, -1));
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
        
        var instance = new InMemoryEmfModel(makeModel0(10, 1, 3));
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));
        System.out.println("Raw");
        System.out.println(results);

        assertEquals(1, results.getMatches().size());
    }

    @Test
    public void test2() throws Exception{
        var module = new EplModule();
        module.parse(query);
        
        var instance = new InMemoryEmfModel(makeModel0(2, 1, 3));
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));
        System.out.println("Raw");
        System.out.println(results);

        assertEquals(0, results.getMatches().size());
    }

    @Test
    public void test3() throws Exception{
        var module = new EplModule();
        module.parse(query);
        
        var instance = new InMemoryEmfModel(makeModel0(2));
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));
        System.out.println("Raw");
        System.out.println(results);

        assertEquals(0, results.getMatches().size());
    }

    private Resource makeModel0(int sensors, int...  lengths){
        var resource = new ResourceImpl();

        var container = make("RailwayContainer");
        resource.getContents().add(container);
        var region = make(container, "regions", "Region");
        
        for(int i=0; i<sensors; i++){
            packageHelper.make(region, "sensors", "Sensor");
        }
        for(int length : lengths){
            var segment = packageHelper.make(region, "elements", "Segment");
            packageHelper.set(segment, "length", length);
        }

        return resource;
    }
}
