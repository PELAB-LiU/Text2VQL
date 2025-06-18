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



public class Test9Switch extends Util{
private String query = 
"""
pattern SwitchOrSwitchPositionRailwayElement
    sw: RailwayElement from: Switch.all {
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
        var sw = make(region, "elements", "Switch");
        
        return resource;
    }
}
