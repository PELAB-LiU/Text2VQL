package se.liu.ida.sas.pelab.text2vql.epsilon;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.epl.execute.PatternMatch;
import org.eclipse.epsilon.epl.execute.model.PatternMatchModel;
import se.liu.ida.sas.pelab.text2vql.utilities.RailwayLoader;
import se.liu.ida.sas.pelab.text2vql.utilities.ResourcesHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.evaluation.MatchSetEvaluator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public class ValidateWithEOL extends MatchSetEvaluator<EplModule,String>{
    public ValidateWithEOL(File csv, File models, File output) throws IOException {
        super("truth_epl", "^epl_query_([0-9]+)$", csv, models, output);
    }

    public static void main(String[] args) throws Exception {
        /*
         * Process command line arguments
         */
        String models = Arrays.stream(args).filter(arg -> arg.startsWith("models=")).map(arg -> arg.replaceFirst("models=","")).findFirst().orElseGet(()->"/config/text2vql/results/testmodels/");
        String csv = Arrays.stream(args).filter(arg -> arg.startsWith("csv=")).map(arg -> arg.replaceFirst("csv=","")).findFirst().orElseGet(()->"/config/text2vql/results/ai/sample.csv");
        File out = Arrays.stream(args).filter(arg -> arg.startsWith("out=")).map(arg -> arg.replaceFirst("out=","")).map(File::new).findFirst().orElseGet(()->null);
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
        EPackage railway = (EPackage) meta.getContents().getFirst();
        EPackage.Registry.INSTANCE.put(railway.getNsURI(), railway);
        /*
         * Load railway model
         */
        ValidateWithEOL validator = new ValidateWithEOL(new File(csv), new File(models), out);
        validator.run();
    }

    @Override
    protected List<String> evaluate(EplModule module, Resource resource) {
        try {
            /*
             * Reset module (just in case)
             */
            module.getContext().getModelRepository().getModels().clear();//Clear models
            module.getContext().getModelRepository().removeModel(null);//Clear cache
            /*
             * Setup evaluation
             */
            InMemoryEmfModel instance = new InMemoryEmfModel(resource);
            module.getContext().getModelRepository().addModel(instance);
            PatternMatchModel resultModel = (PatternMatchModel) module.execute();

            List<String> data = new ArrayList<>(resultModel.getMatches().size());
            for (PatternMatch match : resultModel.getMatches()) {
                StringBuilder builder = new StringBuilder();
                Matcher m = regex_object.matcher(match.toString());
                while (m.find()) {
                    builder.append(m.group().replaceAll("DynamicEObjectImpl",""));
                }
                data.add(builder.toString());
            }
            /*
             * Clean up module
             */
            module.getContext().getModelRepository().removeModel(instance);
            return data;
        } catch (EolRuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected boolean compare(List<String> truth, List<String> got) {
        return isEqual(truth, got);
    }

    @Override
    protected EplModule parse(String query, String top) {
        try {
            EplModule module = new EplModule();
            module.parse(query);
            return module;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
