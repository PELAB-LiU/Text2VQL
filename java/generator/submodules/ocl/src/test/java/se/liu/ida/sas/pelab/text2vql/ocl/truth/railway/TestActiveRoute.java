package se.liu.ida.sas.pelab.text2vql.ocl.truth.railway;

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
import se.liu.ida.sas.pelab.text2vql.utilities.evaluation.MatchSetEvaluator;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.PackageHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.RailwayRuntimePackageHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

@Deprecated
@SuppressWarnings({"rawtypes","unchecked"})
public class TestActiveRoute {
    private static PackageHelper packageHelper = new RailwayRuntimePackageHelper();
    private static MatchProcessor matchProcessor = new MatchProcessor(MatchSetEvaluator.regex_object);

    String query = """
Route.allInstances()->select(route | route.active and route.entry.signal = Signal::GO)
            """;

    @Test
    public void test1() throws ParserException {
        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        OCLHelper helper = ocl.createOCLHelper();
        helper.setContext(packageHelper.epackage.getEClassifier("RailwayContainer"));
        OCLExpression expression = helper.createQuery(query);
        //Object expression = helper.defineOperation(query);
        var OCLquery = ocl.createQuery(expression);
        
        EObject model = makeModel1("Go", true);
        Object result = OCLquery.evaluate(model);
        List<String> matches = matchProcessor.processContainer(result);
        EObject route = (EObject) getEObject(model, "routes");
        assertEquals(1, matches.size());
        assertEquals(matches.get(0), "@"+Integer.toHexString(route.hashCode()));
    }

    @Test
    public void test2() throws ParserException {
        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        OCLHelper helper = ocl.createOCLHelper();
        helper.setContext(packageHelper.epackage.getEClassifier("RailwayContainer"));
        OCLExpression expression = helper.createQuery(query);
        //Object expression = helper.defineOperation(query);
        var OCLquery = ocl.createQuery(expression);
        
        EObject model = makeModel1("Go", false);
        Object result = OCLquery.evaluate(model);
        List<String> matches = matchProcessor.processContainer(result);
        assertEquals(0, matches.size());
    }

    private EObject makeModel1(String signal, boolean active){
        var container = packageHelper.make("RailwayContainer");
        var region = packageHelper.make(container, "regions", "Region");
        var segment = packageHelper.make(region, "elements","Segment");
        var sema = packageHelper.make(segment, "semaphores", "Semaphore");
        var route = packageHelper.make(container, "routes", "Route");
        packageHelper.makeEnum(sema, "signal", "Signal::"+signal.toUpperCase());
        packageHelper.link(route, "entry", sema);
        packageHelper.set(route, "active", active);

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
