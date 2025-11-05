package se.liu.ida.sas.pelab.text2vql.ocl;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ocl.pivot.ExpressionInOCL;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.ocl.pivot.utilities.OCL;
import org.eclipse.ocl.pivot.utilities.OCLHelper;
import org.eclipse.ocl.pivot.utilities.ParserException;
import org.eclipse.ocl.pivot.utilities.Query;

public interface StatelessSyntaxCheckPivotOCL {
    public static record OCLParsed(EPackageRegistryImpl registry, EObject context, String basequery){
        public OCL env(){
            return OCL.newInstance(registry);
        }
        public Query query() throws ParserException{
            OCL ocl = env();
            OCLHelper helper = ocl.createOCLHelper(context);
            ExpressionInOCL expression = helper.createQuery(basequery);
            return ocl.createQuery(expression);
        }
    };

    default OCLParsed parse(String baseQuery, Resource metamodel) throws ParserException{
        
        
        EPackageRegistryImpl registry = new EPackageRegistryImpl();
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.setPackageRegistry(registry);
        
        //EnvironmentFactory environmentFactory = PivotStandaloneSetup.createEnvironmentFactory(resourceSet);
        //OCL ocl = OCL.newInstance(resourceSet);
        OCL ocl = OCL.newInstance(registry);
        
        registry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        getMetamodelsOfResource(metamodel).forEach(epackage -> {
            registry.put(epackage.getNsURI(), epackage);
        });

        EObject context = getMetamodelsOfResource(metamodel).getFirst().getEClassifiers().getFirst();
        OCLHelper helper = ocl.createOCLHelper(context);
        
        ExpressionInOCL expression = helper.createQuery(baseQuery);
        
        
        return new OCLParsed(registry, context, baseQuery);

        // Queries cache an extent map which does not reset after ven context.
        //return new OCLParsed(ocl, ocl.createQuery(expression));
    }
    default ParseResult check(String baseQuery, Resource metamodel){
        try {
            if(baseQuery.length()<5){
                return new ParseResult(false, "Query is too short, probably empty. (length: "+baseQuery.length()+")");
            }
            this.parse(baseQuery, metamodel); //OCL dies with exception if parsing fails.
            return new ParseResult(true, "");
        } catch (ParserException | IndexOutOfBoundsException | IllegalStateException | WrappedException e) {
            return new ParseResult(false, e.getClass().getSimpleName()+": "+e.getMessage());
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
