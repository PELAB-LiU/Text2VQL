package se.liu.ida.sas.pelab.text2vql.ocl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.Query;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.util.Bag;
import org.eclipse.ocl.util.Tuple;
import se.liu.ida.sas.pelab.text2vql.utilities.RailwayLoader;
import se.liu.ida.sas.pelab.text2vql.utilities.ResourcesHelper;
import org.eclipse.ocl.expressions.OCLExpression;
import se.liu.ida.sas.pelab.text2vql.utilities.evaluation.MatchSetEvaluator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ValidateWithOCL extends MatchSetEvaluator<String,String> {
    private static EPackage railway;
    public ValidateWithOCL(File csv, File models) throws IOException {
        super("truth_ocl", "^ocl_query_([0-9]+)$", csv, models);
    }
    @Override
    protected List<String> evaluate(String query, EObject model) {
        try {
            EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
            OCL ocl = OCL.newInstanceAbstract(environmentFactory);
            OCLHelper helper = ocl.createOCLHelper();
            helper.setContext(railway.getEClassifier("RailwayContainer"));//TODO check?
            final OCLExpression expression = helper.createQuery(query);
            Query queryEvaluator = ocl.createQuery(expression);
            final Bag<Tuple<?, ?>> bag = (Bag<Tuple<?, ?>>) queryEvaluator.evaluate(model);
            bag.forEach(t -> {
                System.out.println("\t"+t.toString());
            });
            return null;
        } catch (ParserException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean compare(String truth, String got) {
        return false;
    }

    @Override
    protected String parse(String query) {
        return query;
    }

    public static String query = """
            Sensor.allInstances()->collect(
            	sensor | sensor.monitors->select(oclIsKindOf(Segment))->
            		collect(segment1 | segment1.connectsTo->select(oclIsKindOf(Segment))->
            			select(segment2 | segment2.monitoredBy->includes(sensor))->
            			collect(segment2 | segment2.connectsTo->select(oclIsKindOf(Segment))->
            				select(segment3 | segment3.monitoredBy->includes(sensor))->
            				collect(segment3 | segment3.connectsTo->select(oclIsKindOf(Segment))->
            					select(segment4 | segment4.monitoredBy->includes(sensor))->
            					collect(segment4 | segment4.connectsTo->select(oclIsKindOf(Segment))->
            						select(segment5 | segment5.monitoredBy->includes(sensor))->
            						collect(segment5 | segment5.connectsTo->select(oclIsKindOf(Segment))->
            						select(segment6 | segment6.monitoredBy->includes(sensor))->collect(
            							segment6 | Tuple{sensor = sensor, segment1 = segment1, segment2 = segment2, segment3 = segment3, segment4 = segment4, segment5 = segment5, segment6 = segment6}
            						)
            					)
            				)
            			)
            		)
            	)
            )
            """;
    public static void main(String[] args) throws ParserException, IOException {
        /*
         * Process command line arguments
         */
        String models = Arrays.stream(args).filter(arg -> arg.startsWith("models=")).map(arg -> arg.replaceFirst("models=","")).findFirst().orElseGet(()->"/config/text2vql/results/testmodels/");
        String csv = Arrays.stream(args).filter(arg -> arg.startsWith("csv=")).map(arg -> arg.replaceFirst("csv=","")).findFirst().orElseGet(()->"/config/text2vql/results/ai/sample_ocl.csv");
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
        railway = (EPackage) meta.getContents().getFirst();
        EPackage.Registry.INSTANCE.put(railway.getNsURI(), railway);
        /*
         * Load railway model
         */
        EObject railway_container = RailwayLoader.loadRailway().getContents().getFirst();
        ValidateWithOCL validator = new ValidateWithOCL(new File(csv), new File(models));
        validator.run();
    }
}
