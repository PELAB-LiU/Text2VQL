package se.liu.ida.sas.pelab.text2vql.epl.truth.railway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.epl.execute.model.PatternMatchModel;
import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.epl.truth.railway.util.Util;

public class Test0ActiveRouteWithMisaligned extends Util{
private String query = 
"""
pattern GoRoute
    route: Route from : Route.all.select(rt |  rt.entry.signal == Signal#GO) {
    
    match: (route.`active`) and 
        route.follows->exists(swP | swP.position != swP.target.currentPosition)
}
""";
    @Test
    public void testEmpty() throws Exception{
        var module = new EplModule();
        System.out.println(module.parse(query));
        module.getParseProblems().forEach(System.out::println);

        var instance = new InMemoryEmfModel(makeEmpty());
        instance.setMetamodelFile("C:\\Apps\\git\\Text2VQL\\java\\generator\\submodules\\utilities\\src\\main\\resources\\railway\\railway.ecore");
        instance.load();

        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
        assertEquals(0, results.getMatches().size());
    }

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

        var route = make(container, "routes", "Route");
        var swp = make(route, "follows", "SwitchPosition");
        packageHelper.makeEnum(swp, "position", "Position::STRAIGHT");

        var region = make(container, "regions", "Region");
        var sw = make(region, "elements","Switch");
        packageHelper.makeEnum(sw, "currentPosition", "Position::DIVERGING");
        
        link(route, "active", true);
        link(swp, "target", sw);
        
        var segment = make(region, "elements", "Segment");
        var sema = make(segment, "semaphores", "Semaphore");
        packageHelper.makeEnum(sema, "signal", "Signal::GO");
        link(route, "entry", sema);

        return resource;
    }
}
