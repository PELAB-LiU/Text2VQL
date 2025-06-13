package se.liu.ida.sas.pelab.text2vql.epl.truth.railway;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.epl.execute.model.PatternMatchModel;
import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.epl.truth.railway.util.Util;
import se.liu.ida.sas.pelab.text2vql.utilities.evaluation.MatchSetEvaluator;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.PackageHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.RailwayRuntimePackageHelper;

public class Test0ActiveRouteWithMisaligned extends Util{
    
    //private static MatchProcessor matchProcessor = new MatchProcessor(MatchSetEvaluator.regex_object);
private String query = """
        
        """;
    @Test
    public void test0() throws Exception{
        var module = new EplModule();
        module.parse(query);
        
        var instance = new InMemoryEmfModel(makeModel0());
        module.getContext().getModelRepository().addModel(instance);

        var results = (PatternMatchModel) module.execute();
    }

    private Resource makeModel0(){
        var resource = new ResourceImpl();

        var container = make("RailwayContainer");
        resource.getContents().add(container);

        var route = make(container, "routes", "Route");
        var region = make(container, "regions", "Region");
        var segment1 = make(region, "elements","Segment");
        var sw = make(region, "elements","Switch");
        var swp = make(route, "follows", "SwitchPosition");
        
        return resource;
    }
}
