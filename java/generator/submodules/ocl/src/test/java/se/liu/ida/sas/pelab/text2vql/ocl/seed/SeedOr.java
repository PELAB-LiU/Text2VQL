package se.liu.ida.sas.pelab.text2vql.ocl.seed;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;
import org.junit.jupiter.api.Test;
import se.liu.ida.sas.pelab.text2vql.ocl.helper.MatchProcessor;
import se.liu.ida.sas.pelab.text2vql.ocl.helper.SignatureProcessor;
import se.liu.ida.sas.pelab.text2vql.utilities.evaluation.MatchSetEvaluator;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.PackageHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.YakinduRuntimePackageHelper;

@Deprecated
@SuppressWarnings({"rawtypes","unchecked","unused"})
public class SeedOr {
    private static PackageHelper packageHelper = new YakinduRuntimePackageHelper();
    private static MatchProcessor matchProcessor = new MatchProcessor(MatchSetEvaluator.regex_object);

    String query = """
Region.allInstances()->select(r |
                            r.name.indexOf('normal')>0
                        )
""";

    @Test
    public void test1() throws ParserException {
        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        OCLHelper helper = ocl.createOCLHelper();
        helper.setContext(packageHelper.epackage.getEClassifiers().getFirst());
        OCLExpression expression = helper.createQuery(query);
        //Object expression = helper.defineOperation(query);
        var OCLquery = ocl.createQuery(expression);

        System.out.println(new SignatureProcessor().process(OCLquery.resultType()));

        /*EObject model = makeModel1(2, 1, 3);
        Object result = OCLquery.evaluate(model);
        List<String> matches = matchProcessor.processContainer(result);
        assertEquals(2, matches.size());
        //EObject swp = (EObject) getEObject(model, "regions");
        //assertEquals(matches.get(0), "@"+Integer.toHexString(swp.hashCode()));
        */
    }

    private EObject makeModel1(int swc, int... lengths){
        var container = packageHelper.make("RailwayContainer");
        var region = packageHelper.make(container, "regions", "Region");

        for(int i=0; i<swc; i++){
            packageHelper.make(region, "elements", "Switch");
        }
        for(int length : lengths){
            var segment = packageHelper.make(region, "elements", "Segment");
            packageHelper.set(segment, "length", length);
        }

        return container;
    }


    

    private Object getEObject(EObject source, String... relation){
        return getEObject(source, relation, 0);
    }

    private Object getEObject(Object source, String[] relation, int depth){
        if(depth==relation.length){
            return source;
        }
        System.out.println(source+"."+relation[depth]);

        if(source instanceof EObject eobject){
            EStructuralFeature feature = eobject.eClass().getEStructuralFeature(relation[depth]);
            if(feature==null){
                return null;
            }

            Object data = eobject.eGet(feature);
            if(data instanceof EList list){
                for (Object object : list) {
                    Object result = getEObject(object, relation, depth+1);
                    if(result!=null){
                        return result;
                    }
                }
            } else {
                return getEObject(data, relation, depth+1);
            }
        }
        
        return null;
    }
}
