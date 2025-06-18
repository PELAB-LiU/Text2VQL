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



public class Test5RouteSensor extends Util{
private String query = 
"""
pattern RouteSensor
  route: Route,
  swP: SwitchPosition from: route.follows,
  sw: Switch from: swP.target,
  sensor: Sensor from: sw.monitoredBy.select(s|route.requires.excludes(s))
}

""";

private String query2 = 
"""
pattern NotRequiredRouteSensor
    route: Route, 
    swP: SwitchPosition from: route.follows, 
    sw: Switch from: swP.target,
    sensor: Sensor from: sw.monitoredBy {

    match: route.requires->excludes(sensor))
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

        assertEquals(0, results.getMatches().size());
    }

    private Resource makeModel0(Boolean link){
        var resource = new ResourceImpl();

        var container = make("RailwayContainer");
        resource.getContents().add(container);
        var region = make(container, "regions", "Region");
        var sw = make(region, "elements","Switch");
        var sensor = make(region, "sensors", "Sensor");

        var route = make(container, "routes", "Route");
        var swp = make(route, "follows", "SwitchPosition");
        
        link(swp, "target", sw);
        link(sw, "monitoredBy", sensor);
        
        if(link){
            link(route, "requires", sensor);
        }
        return resource;
    }
}
