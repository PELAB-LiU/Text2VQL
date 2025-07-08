package se.liu.ida.sas.pelab.text2vql.yamtl;

import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.junit.jupiter.api.Test;

import yamtl.core.YAMTLModuleGroovy;
import yamtl.core.YAMTLModule.ExecutionPhase;

import se.liu.ida.sas.pelab.text2vql.utilities.modeling.RailwayRuntimePackageHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.YakinduRuntimePackageHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.PackageHelper;

public class DebugYAMTLQuery extends Util  {
    private String query = 
"""
[
    context: 'Class',
    where: { it.attr.size() > 0 },
    query: {
        def idCounts = it.attr.countBy { it.name }
        def repeatedIds = idCounts.findAll { k, v -> v > 1 }.keySet()
        result = repeatedIds.size()
    }
]
""";
    @Test
    public void test(){
        System.out.println("Hello YAMTL!");
        createAndConfigure();
    }

    void createAndConfigure() {
		//def activityRes = preloadMetamodel(ecorePath);
		//def activityPk = activityRes.getContents().get(0) as EPackage
		
		//def xform = new QueryActivityDsl(activityPk)
        header().in("railway", packageHelper.epackage);
        selectedExecutionPhases = ExecutionPhase.MATCH_ONLY;

		//xform.loadMetamodelResource(activityRes)
		//xform.loadInputModels(['activity': xmiPath])

		loadMetamodelResource(packageHelper.resource);
        loadInputResources(Map.of("railway", makeModel0()));
        this.
		//xform.context(contextArgs)
		
		//return xform
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
