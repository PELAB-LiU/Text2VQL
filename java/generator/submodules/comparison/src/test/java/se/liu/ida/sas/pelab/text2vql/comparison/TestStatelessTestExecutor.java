package se.liu.ida.sas.pelab.text2vql.comparison;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;
import se.liu.ida.sas.pelab.text2vql.comparison.input.TestCase;
import se.liu.ida.sas.pelab.text2vql.utilities.Text2VQLProjectStructure;
import se.liu.ida.sas.pelab.text2vql.vql.StatelessVQLSyntaxCheck;

public class TestStatelessTestExecutor {

    @Test
    public void testServeTest() throws InterruptedException, ExecutionException{
        StatelessVQLSyntaxCheck.init();

        List<EObject> instances = new ArrayList<EObject>();
        
        instances.add(PatternLanguagePackage.eINSTANCE);
        instances.add(EcorePackage.eINSTANCE);

        Query truth = new Query(0, "normalClasses", TestVQLJob.normalClasses);

        Query vql1 = new Query(1, "normalClasses", TestVQLJob.normalClasses);
        Query vql2 = new Query(2, "abstractClasses", TestVQLJob.abstractClasses);

        Query ocl1 = new Query(3, "", TestOCLJob.abstractClasses);
        Query ocl2 = new Query(4, "", TestOCLJob.normalClassesTuple);

        Query java1 = new Query(5, "query", TestJavaJob.normalClasses);
        Query java2 = new Query(6, "query", TestJavaJob.abstractClasses); 
        TestCase test = new TestCase(truth, new Query[]{vql1, vql2}, new Query[]{ocl1, ocl2}, new Query[]{java1, java2});

        
        //TestCase test = new TestCase(null, truth, new Query[]{truth, faulty}, new Query[]{}, new Query[]{});


        StatelessTestExecutor executor = new StatelessTestExecutor(){};
        try {
            System.out.println(executor.serveTest(new ResourceImpl(), instances, test, null));
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | InterruptedException | ExecutionException e) {// Happy Friday: There are a few failure points...
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testServeTestExternal() throws InterruptedException, ExecutionException{
        StatelessVQLSyntaxCheck.init();
        
        Query truth = new Query(0, "goRouteMisalignedSwitch", """
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
            """);

        Query vql1 = new Query(1, "goRouteMisalignedSwitch", """
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
            """);
        Query vql2 = new Query(2, "stopOrGo", """
            pattern stopOrGo(semaphore: Semaphore){
                Semaphore.signal(semaphore, Signal::FAILURE);
            }
            """);

        Query ocl1 = new Query(3, "", "Semaphore.allInstances()->select(s | s.signal = Signal::GO or s.signal = Signal::STOP)");
        Query ocl2 = new Query(4, "", "Semaphore.allInstances()->select(s | s.signal = Signal::FAILURE)");

        //Query java1 = new Query(5, "query", TestJavaJob.normalClasses);
        //Query java2 = new Query(6, "query", TestJavaJob.abstractClasses); 
        TestCase test = new TestCase(truth, new Query[]{vql1, vql2}, new Query[]{ocl1, ocl2}, new Query[]{});

        
        //TestCase test = new TestCase(null, truth, new Query[]{truth, faulty}, new Query[]{}, new Query[]{});


        StatelessTestExecutor executor = new StatelessTestExecutor(){};
        Resource metamodel = loadMetamodel().eResource();
        List<EObject> instances = new ArrayList<>();
        instances.add(loadModel((EPackage) metamodel.getContents().getFirst(), "results/testmodels/Manual0.xmi"));
        instances.add(loadModel((EPackage) metamodel.getContents().getFirst(), "results/testmodels/Manual10.xmi"));
        assertEquals(1, metamodel.getContents().size());

        try {
            System.out.println(executor.serveTest(metamodel, instances, test, null));
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | InterruptedException | ExecutionException e) {// Happy Friday: There are a few failure points...
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    private EObject loadModel(EPackage metamodel, String modelpath){
        final ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("xmi", new XMIResourceFactoryImpl());
        rs.getPackageRegistry().put(metamodel.getNsURI(), metamodel);
        Resource res = rs.getResource(URI.createFileURI(
            Text2VQLProjectStructure.find(modelpath).getAbsolutePath()), true);
        return res.getContents().getFirst();
    }
    
}
