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

public class Test3ConnectedOrSameSensor extends Util{
private String query = 
"""
pattern RelatedTrackElements 
    track1: TrackElement, 
    track2: TrackElement {

    match : (
        track1.monitoredBy->exists(s: Sensor | track2.monitoredBy->includes(s))
    ) or (
        track1.connectsTo->includes(track2)
    )
}
""";
    @Test
    public void test0() throws Exception{
        var module = new EplModule();
        module.parse(query);
        System.out.println( module.getDebugInfo());
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
        var segment1 = make(region, "elements","Segment");
        var segment2 = make(region, "elements","Segment");
        link(segment1, "connectsTo", segment2);
        
        return resource;
    }

    @Test
    public void test1() throws Exception{
        var module = new EplModule();
        module.parse(query);
        System.out.println( module.getDebugInfo());
        var instance = new InMemoryEmfModel(makeModel1());
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        System.out.println("Processed");
        System.out.println(process(results));

        assertEquals(4, results.getMatches().size());
    }

    private Resource makeModel1(){
        var resource = new ResourceImpl();
        var container = make("RailwayContainer");
        resource.getContents().add(container);

        var region = make(container, "regions", "Region");
        var segment1 = make(region, "elements","Segment");
        var segment2 = make(region, "elements","Segment");
        var sensor = make(region, "sensors", "Sensor");
        link(segment2, "monitoredBy", sensor);
        link(segment1, "monitoredBy", sensor);
        
        
        return resource;
    }
}
