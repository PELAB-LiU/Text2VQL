package se.liu.ida.sas.pelab.text2vql.ocl;

import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;

public interface StatelessSyntaxCheckOCL {
    default ParseResult check(String baseQuery, Resource metamodel){
        EPackageRegistryImpl registry = new EPackageRegistryImpl();
        registry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        getMetamodelsOfResource(metamodel).forEach(epackage -> registry.put(epackage.getNsURI(), epackage));
        
        try {
            EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
            OCL ocl = OCL.newInstanceAbstract(environmentFactory);
            OCLHelper helper = ocl.createOCLHelper();
            //Select an arbitrary class from the largest package as context
            EClassifier context = getMainPackage(getMetamodelsOfResource(metamodel)).getEClassifiers().getFirst();
            helper.setContext(context);
            OCLExpression expression = helper.createQuery(baseQuery);
            ocl.createQuery(expression);
            return new ParseResult(true, "Chosen context: "+context.getName());
        } catch (ParserException e) {
            return new ParseResult(false, e.getMessage());
        }
    }
    static EPackage getMainPackage(List<EPackage> packages){
        int index = 0;
        int maxSize = 0;
        for(int i=0; i<packages.size(); i++){
            int packagesize = packages.get(i).getEClassifiers().size();
            if(packagesize > maxSize){
                index = i;
                maxSize = packagesize;
            }
        }
        return packages.get(index);
    }
    static List<EPackage> getMetamodelsOfResource(Resource resource){
        return resource.getContents().stream()
            .filter(it -> it instanceof EPackage)
            .map(it -> (EPackage) it)
            .toList();
    }

    public static record ParseResult(Boolean isCorrect, String diagnostics){}
}
