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

public class TestSemaStopGo {
    private static PackageHelper packageHelper = new RailwayRuntimePackageHelper();
    private static MatchProcessor matchProcessor = new MatchProcessor(MatchSetEvaluator.regex_object);

    String query = """
            Semaphore.allInstances() -> select(semaphore | semaphore.signal = Signal::GO or semaphore.signal = Signal::STOP)
            """;

    @Test
    public void testGo() throws ParserException {
        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        OCLHelper helper = ocl.createOCLHelper();
        helper.setContext(packageHelper.epackage.getEClassifier("RailwayContainer"));
        OCLExpression expression = helper.createQuery(query);
        //Object expression = helper.defineOperation(query);
        var OCLquery = ocl.createQuery(expression);
        
        EObject model = makeModelWithSemaphore("Go");
        Object result = OCLquery.evaluate(model);
        List<String> matches = matchProcessor.processContainer(result);
        EObject sema = (EObject) getEObject(model, "regions", "elements", "semaphores");
        assertEquals(1, matches.size());
        assertEquals(matches.get(0), "@"+Integer.toHexString(sema.hashCode()));
    }

    @Test
    public void testStop() throws ParserException {
        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        OCLHelper helper = ocl.createOCLHelper();
        helper.setContext(packageHelper.epackage.getEClassifier("RailwayContainer"));
        OCLExpression expression = helper.createQuery(query);
        //Object expression = helper.defineOperation(query);
        var OCLquery = ocl.createQuery(expression);

        EObject model = makeModelWithSemaphore("STOP");
        Object result = OCLquery.evaluate(model);
        List<String> matches = matchProcessor.processContainer(result);
        EObject sema = (EObject) getEObject(model, "regions", "elements", "semaphores");
        assertEquals(1, matches.size());
        assertEquals(matches.get(0), "@"+Integer.toHexString(sema.hashCode()));
    }

    @Test
    public void testFail() throws ParserException {
        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        OCLHelper helper = ocl.createOCLHelper();
        helper.setContext(packageHelper.epackage.getEClassifier("RailwayContainer"));
        OCLExpression expression = helper.createQuery(query);
        //Object expression = helper.defineOperation(query);
        var OCLquery = ocl.createQuery(expression);

        EObject model = makeModelWithSemaphore("FAILURE");
        Object result = OCLquery.evaluate(model);
        List<String> matches = matchProcessor.processContainer(result);
        EObject sema = (EObject) getEObject(model, "regions", "elements", "semaphores");
        assertEquals(0, matches.size());
    }
    /**
     * 
     * @return
     */
    private EObject makeModelWithSemaphore(String signal){
        var container = packageHelper.make("RailwayContainer");
        var region = packageHelper.make(container, "regions", "Region");
        var segment = packageHelper.make(region, "elements","Segment");
        var semaGO = packageHelper.make(segment, "semaphores", "Semaphore");
        packageHelper.makeEnum(semaGO, "signal", "Signal::"+signal.toUpperCase());
        
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
