package se.liu.ida.sas.pelab.text2vql.ocl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.liu.ida.sas.pelab.text2vql.ocl.helper.MatchProcessor;
import se.liu.ida.sas.pelab.text2vql.utilities.evaluation.MatchSetEvaluator;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.PackageHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.RailwayRuntimePackageHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.YakinduRuntimePackageHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.syntax.SyntaxChecker;

import java.io.IOException;
import java.sql.SQLException;

public class DebugQuery {
    //private static PackageHelper packageHelper = new YakinduRuntimePackageHelper();
    private static PackageHelper packageHelper = new RailwayRuntimePackageHelper();
    private static MatchProcessor matchProcessor = new MatchProcessor(MatchSetEvaluator.regex_object);
    @Test
    public void debug() throws ParserException {
        String query = """
                context Statechart
                body : foo() : Bag(Tuple(s : State))
                Vertex.allInstances()->select(state | state.oclIsTypeOf(Entry) or state.oclIsTypeOf(FinalState))->collect(state | Tuple{s=state})
                """;

        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        OCLHelper helper = ocl.createOCLHelper();

        helper.setContext(packageHelper.epackage.getEClassifier("Statechart"));
        //OCLExpression expression = helper.createQuery(query);
        Object expression = helper.defineOperation(query);
        System.out.println(expression.getClass());
        var OCLquery = ocl.createQuery(expression);
        Object result = OCLquery.evaluate(makeModel());
        matchProcessor.processContainer(result);
    }
    private EObject makeModel(){
        var container = packageHelper.make("RailwayContainer");
        var route = packageHelper.make(container, "routes", "Route");

        var region = packageHelper.make(container, "regions", "Region");
        var segment = packageHelper.make(region, "elements","Segment");

        var semaGO = packageHelper.make(segment, "semaphores", "Semaphore");
        packageHelper.make(semaGO, "signal", "Signal.GO");
        var finalState = packageHelper.make("FinalState");
        var state = packageHelper.make("State");

        packageHelper.add(container, "regions", region);
        //packageHelper.add(region, "vertices", entryState);
        packageHelper.add(region, "vertices", finalState);
        packageHelper.add(region, "vertices", state);

        return container;
    }//*/

    /*private EObject makeModel(){
        var factory = packageHelper.factory;

        var container = packageHelper.make("Statechart");
        var region = packageHelper.make("Region");
        var entryState = packageHelper.make("Entry");
        var finalState = packageHelper.make("FinalState");
        var state = packageHelper.make("State");

        packageHelper.add(container, "regions", region);
        packageHelper.add(region, "vertices", entryState);
        packageHelper.add(region, "vertices", finalState);
        packageHelper.add(region, "vertices", state);

        return container;
    }//*/
}
