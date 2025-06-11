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

public class TestMonitoredBy2 {
    private static PackageHelper packageHelper = new RailwayRuntimePackageHelper();
    private static MatchProcessor matchProcessor = new MatchProcessor(MatchSetEvaluator.regex_object);

    String query = """
TrackElement.allInstances()->select(track |  track.monitoredBy->size()>=2)
            """;

    @Test
    public void testLength() throws ParserException {
        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        OCLHelper helper = ocl.createOCLHelper();
        helper.setContext(packageHelper.epackage.getEClassifier("RailwayContainer"));
        OCLExpression expression = helper.createQuery(query);
        //Object expression = helper.defineOperation(query);
        var OCLquery = ocl.createQuery(expression);
        
        EObject model = makeModel1();
        Object result = OCLquery.evaluate(model);
        List<String> matches = matchProcessor.processContainer(result);
        //EObject sema = (EObject) getEObject(model, "regions", "elements", "semaphores");
        assertEquals(1, matches.size());
        //assertEquals(matches.get(0), "@"+Integer.toHexString(sema.hashCode()));
    }
    private EObject makeModel1(){
        var container = packageHelper.make("RailwayContainer");
        var region = packageHelper.make(container, "regions", "Region");
        var segment1 = packageHelper.make(region, "elements","Segment");
        var segment2 = packageHelper.make(region, "elements","Segment");
        var sensor1 = packageHelper.make(region, "sensors","Sensor");
        var sensor2 = packageHelper.make(region, "sensors","Sensor");
        packageHelper.link(segment1, "monitoredBy", sensor1);
        packageHelper.link(segment1, "monitoredBy", sensor2);

        packageHelper.link(segment2, "monitoredBy", sensor1);
        
        return container;
    }

    /**
     * Check if EMF allos duplicates in reference lists.
     * (If yes, it would allow a mismatch between VQL and OCL truth.)
     * Answer: Seemingly no.
     */
    @Test
    public void testSameSensor() throws ParserException {
        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        OCLHelper helper = ocl.createOCLHelper();
        helper.setContext(packageHelper.epackage.getEClassifier("RailwayContainer"));
        OCLExpression expression = helper.createQuery(query);
        //Object expression = helper.defineOperation(query);
        var OCLquery = ocl.createQuery(expression);
        
        EObject model = makeModel2();
        Object result = OCLquery.evaluate(model);
        List<String> matches = matchProcessor.processContainer(result);
        assertEquals(0, matches.size());
    }
    private EObject makeModel2(){
        var container = packageHelper.make("RailwayContainer");
        var region = packageHelper.make(container, "regions", "Region");
        var segment1 = packageHelper.make(region, "elements","Segment");
        var sensor1 = packageHelper.make(region, "sensors","Sensor");
        packageHelper.link(segment1, "monitoredBy", sensor1);
        packageHelper.link(segment1, "monitoredBy", sensor1);
        
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
