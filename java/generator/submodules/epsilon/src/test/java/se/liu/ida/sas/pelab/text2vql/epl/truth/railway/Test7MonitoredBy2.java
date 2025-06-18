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



public class Test7MonitoredBy2 extends Util{
private String query = 
"""
pattern MonitoredBy2Sensors
    track: TrackElement from: TrackElement.all.select(te | te.monitoredBy.size >=2) {
}
""";

    @Test
    public void test0() throws Exception{
        var module = new EplModule();
        module.parse(query);
        var instance = new InMemoryEmfModel(makeModel0(3));
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
        
        var instance = new InMemoryEmfModel(makeModel0(0));
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));
        System.out.println("Raw");
        System.out.println(results);

        assertEquals(0, results.getMatches().size());
    }

    private Resource makeModel0(int... sensors){
        var resource = new ResourceImpl();

        var container = make("RailwayContainer");
        resource.getContents().add(container);
        var region = make(container, "regions", "Region");
        for(int sc : sensors){
            var te = make(region, "elements", "Segment");
            for(int i =0; i<sc; i++){
                var s = make(region, "sensors", "Sensor");
                link(te, "monitoredBy", s);
            }
        }
        return resource;
    }
}
