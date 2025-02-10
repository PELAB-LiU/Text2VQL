package se.liu.ida.sas.pelab.text2vql.ocl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.util.Bag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import se.liu.ida.sas.pelab.text2vql.utilities.RailwayLoader;
import se.liu.ida.sas.pelab.text2vql.utilities.ResourcesHelper;

import java.util.HashSet;
import java.util.List;

public class MatchTest {
    @Disabled
    @Test
    public void printMatch() throws ParserException {

        try{
            /*
             * Load railway model
             */
            EObject railway_container = RailwayLoader.loadRailway().getContents().getFirst();
        /*
          Load OCL
         */
            EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
            OCL ocl = OCL.newInstanceAbstract(environmentFactory);
            List<Constraint> constraints = ocl.parse(new OCLInput(RailwayOCLQueries.SIX_SEGMENT));
            for(Constraint constraint : constraints){
                System.out.print("Matches of "+constraint.getName());
                Object result = ocl.evaluate(railway_container, constraint.getSpecification().getBodyExpression());
                System.out.println(" is " + result.getClass());
                if (result instanceof HashSet<?>){
                    ((HashSet<?>) result).forEach(System.out::println);
                }
                if (result instanceof Bag<?> bag){
                    bag.forEach(System.out::println);
                }
            }
        } catch (ParserException e){
            var diagnostics = e.getDiagnostic();
            System.err.println(e.getDiagnostic());
        }

    }

    @BeforeAll
    public static void setup(){
        /*
          Load Ecore
         */
        EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        var resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "xmi", new EcoreResourceFactoryImpl());

        /*
          Load railway domain
         */
        Resource meta = resourceSet.getResource(ResourcesHelper.emfURI("railway/railway.ecore"),true);
        EPackage railway = (EPackage) meta.getContents().getFirst();
        EPackage.Registry.INSTANCE.put(railway.getNsURI(), railway);
    }
}
