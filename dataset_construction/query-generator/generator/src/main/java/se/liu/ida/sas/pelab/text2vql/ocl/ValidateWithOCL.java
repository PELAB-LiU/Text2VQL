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
import se.liu.ida.sas.pelab.text2vql.ResourcesHelper;

import java.util.HashSet;
import java.util.List;

public class ValidateWithOCL {
    public static void main(String[] args) throws ParserException {
        System.out.println("Hello OCL!");
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
        /*
          Load railway domain
         */
        Resource meta = resourceSet.getResource(ResourcesHelper.emfURI("railway/railway.ecore"),true);
        EPackage railway = (EPackage) meta.getContents().getFirst();
        EPackage.Registry.INSTANCE.put(railway.getNsURI(), railway);
        /*
         * Load railway model
         */
        EObject railway_container = resourceSet.getResource(
                ResourcesHelper.emfURI("railway/railway.xmi"), true).getContents().getFirst();
        /*
          Load OCL
         */
        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        List<Constraint> constraints = ocl.parse(new OCLInput(ResourcesHelper.inputStream("ocl/sample.ocl")));
        for(Constraint constraint : constraints){
            System.out.println(constraint.getName() +" is "+constraint.getSpecification().getBodyExpression());
            Object result = ocl.evaluate(railway_container, constraint.getSpecification().getBodyExpression());
            System.out.println(result);
            if (result instanceof HashSet<?>){
                ((HashSet<?>) result).forEach(System.out::println);
            }
        }


    }
}
