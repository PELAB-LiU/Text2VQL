package se.liu.ida.sas.pelab.text2vql.ocl;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.Query;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;

@Deprecated
@SuppressWarnings({"rawtypes","unchecked"})
public interface StatelessSyntaxCheckOCL {
    public static record OCLParsed(OCL env, OCLExpression expression){
        public Query query(){
            return env.createQuery(expression);
        }
    };

    default OCLParsed parse(String baseQuery, Resource metamodel) throws ParserException{
        EPackageRegistryImpl registry = new EPackageRegistryImpl();
        registry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        getMetamodelsOfResource(metamodel).forEach(epackage -> registry.put(epackage.getNsURI(), epackage));
        
        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(registry);
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        OCLHelper helper = ocl.createOCLHelper();
        
        helper.setContext(EcorePackage.Literals.ECLASS);//Must be set
        OCLExpression expression = helper.createQuery(baseQuery);
        
        return new OCLParsed(ocl, expression);

        // Queries cache an extent map which does not reset after ven context.
        //return new OCLParsed(ocl, ocl.createQuery(expression));
    }
    default ParseResult check(String baseQuery, Resource metamodel){
        try {
            this.parse(baseQuery, metamodel); //OCL dies with exception if parsing fails.
            return new ParseResult(true, "");
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
