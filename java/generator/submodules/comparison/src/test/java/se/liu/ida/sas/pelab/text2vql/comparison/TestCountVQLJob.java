package se.liu.ida.sas.pelab.text2vql.comparison;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.CountVQLJob;
import se.liu.ida.sas.pelab.text2vql.utilities.Text2VQLProjectStructure;
import se.liu.ida.sas.pelab.text2vql.vql.StatelessVQLSyntaxCheck;



public class TestCountVQLJob {
        public static String stopOrGo = """
            pattern goRouteMisalignedSwitch(route: Route){
 find goRoute(route);
 Route.follows(route, swP);
 find misalignedSwitchPosition(swP);
}

pattern goRoute(route: Route){
	Route.active(route,true);
	Route.entry(route, semaphore);
	Semaphore.signal(semaphore, Signal::GO);	
}

pattern misalignedSwitchPosition(swP : SwitchPosition){
	SwitchPosition.target(swP, sw);
	SwitchPosition.position(swP, swpPosition);
	Switch.currentPosition(sw, swCurrentPosition);
	swpPosition != swCurrentPosition;
}
            """;
    @Test
    public void testJob() throws Exception{
        StatelessVQLSyntaxCheck.init();

        var ecore = EcorePackage.eINSTANCE;
        Resource metamodel = loadMetamodel().eResource();
        var job = new CountVQLJob(metamodel, new Query(0, "goRouteMisalignedSwitch", stopOrGo));
        /*job.patterns.getAllDiagnostics().forEach(it -> {
            System.err.println(it);
        });*/
        
        assertEquals(false, job.patterns.hasError());

        job.configureInstanceModel(loadModel((EPackage) metamodel.getContents().getFirst()));
        assertEquals(1, job.call());
    }

    private EObject loadMetamodel(){
        final ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("xmi", new XMIResourceFactoryImpl());
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("ecore", new XMIResourceFactoryImpl());
        Resource res = rs.getResource(URI.createFileURI(
            Text2VQLProjectStructure.find("dataset_construction/test_metamodel/railway.ecore").getAbsolutePath()), true);
        
        EPackage ePackage = (EPackage) res.getContents().getFirst();
        EPackage.Registry.INSTANCE.putIfAbsent(ePackage.getNsURI(), ePackage);

        return ePackage;
    }

    private EObject loadModel(EPackage metamodel){
        final ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("xmi", new XMIResourceFactoryImpl());
        rs.getPackageRegistry().put(metamodel.getNsURI(), metamodel);
        Resource res = rs.getResource(URI.createFileURI(
            Text2VQLProjectStructure.find("results/testmodels/Manual0.xmi").getAbsolutePath()), true);
        return res.getContents().getFirst();
    }
}
