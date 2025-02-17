package se.liu.ida.sas.pelab.text2vql.vql;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import se.liu.ida.sas.pelab.text2vql.utilities.ResourcesHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.evaluation.MatchSetEvaluator;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;


public class ValidateWithVQL extends MatchSetEvaluator<IQuerySpecification<?>, String> {
    private AdvancedViatraQueryEngine engine;

    private ResourceSet resources;
    public ValidateWithVQL(File csv, File modelsDir, File output) throws IOException {
        super("truth_vql", "^vql_query_([0-9]+)$", csv, modelsDir, output);
        resources = new ResourceSetImpl();
        engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(resources));
    }

    @Override
    protected List<String> evaluate(IQuerySpecification<?> query, Resource resource) {
        engine.wipe();
        resources.getResources().add(resource);
        try{
            Collection<? extends IPatternMatch> matches = engine.getMatcher(query).getAllMatches();
            List<String> results = new ArrayList<>(matches.size());

            matches.forEach(match->{
                StringBuilder builder = new StringBuilder();

                Matcher m = regex_object.matcher(match.prettyPrint());
                while (m.find()) {
                    builder.append(m.group().replaceAll("DynamicEObjectImpl",""));
                }
                System.out.println("\t"+builder);
                results.add(builder.toString());
            });
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            resources.getResources().remove(resource);
        }

    }

    @Override
    protected boolean compare(List<String> truth, List<String> got) {
        return false;
    }

    @Override
    protected IQuerySpecification<?> parse(String query, String top) {
        StringBuilder builder = new StringBuilder();
        builder.append("import \"railway\"").append(System.lineSeparator());
        builder.append("import \"http://www.eclipse.org/emf/2002/Ecore\"").append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append(query).append(System.lineSeparator());

        PatternParsingResults result = PatternParserBuilder.instance().parse(builder.toString());

        if(result.hasError()){
            result.getErrors().forEach(System.out::println);
            return null;
        };
        System.out.println(top);
        System.out.println(result.getQuerySpecification(top));
        return result.getQuerySpecification(top).orElseGet(null);
    }

    public static void main(String[] args) throws IOException {
        /*
         * Process command line arguments
         */
        String models = Arrays.stream(args).filter(arg -> arg.startsWith("models=")).map(arg -> arg.replaceFirst("models=","")).findFirst().orElseGet(()->"/config/text2vql/results/testmodels/");
        String csv = Arrays.stream(args).filter(arg -> arg.startsWith("csv=")).map(arg -> arg.replaceFirst("csv=","")).findFirst().orElseGet(()->"/config/text2vql/results/ai/sample.csv");
        File out = Arrays.stream(args).filter(arg -> arg.startsWith("out=")).map(arg -> arg.replaceFirst("out=","")).map(File::new).findFirst().orElseGet(()->null);
        /*
          Load Ecore
         */

        Logger.getRootLogger();
        EMFPatternLanguageStandaloneSetup.doSetup();
//        var runtimeModule = new EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules();
//        Injector injector = Guice.createInjector(runtimeModule);
//        //XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);


        ViatraQueryEngineOptions.setSystemDefaultBackends(ReteBackendFactory.INSTANCE, ReteBackendFactory.INSTANCE,
                LocalSearchEMFBackendFactory.INSTANCE);


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
        ValidateWithVQL validator = new ValidateWithVQL(new File(csv), new File(models), out);
        validator.run();
    }
}
