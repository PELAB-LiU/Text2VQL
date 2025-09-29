package se.liu.ida.sas.pelab.text2vql.vql;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.File;

public interface StatelessVQLSyntaxCheck {
    static void init(){
        EMFPatternLanguageStandaloneSetup.doSetup();
        EPackage.Registry.INSTANCE.putIfAbsent(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
    }

    default ParseResult check(File metamodel, String baseQuery){
        Injector injector = makeInjector();
        ResourceSet resources = makeLocalResourceSet(injector);


        /*
         * Setup resource set
         */
        Resource loadedResources = resources.getResource(URI.createFileURI(metamodel.getAbsolutePath()), true);
        loadedResources.getContents().forEach(resource -> {
            if(resource instanceof EPackage epackage){
                resources.getPackageRegistry().put(epackage.getNsURI(), epackage);
            }
        });

        PatternParserBuilder parser = PatternParserBuilder.instance();
        parser.withInjector(injector);

        String query = buildQueryString(baseQuery, loadedResources);

        try{
            PatternParsingResults result = parser.parse(query);

            StringBuilder diagnostics = new StringBuilder();
            result.getAllDiagnostics().forEach(issue -> {
                diagnostics.append(issue.getMessage()).append(System.lineSeparator());
            });
            return new ParseResult(!result.hasError(), diagnostics.toString());
        } catch (Exception e){
            return new ParseResult(false, e.getStackTrace().toString());
        }
    }

    default Injector makeInjector(){
        StandaloneParserWithSeparateModules runtimeModule = new StandaloneParserWithSeparateModules();
        return Guice.createInjector(runtimeModule);
    }

    default EPackage.Registry makeRegistry(){
        return new EPackageRegistryImpl(EPackage.Registry.INSTANCE);
    }

    default ResourceSet makeLocalResourceSet(Injector injector){
        XtextResourceSet resources = injector.getInstance(XtextResourceSet.class);
        EPackage.Registry registry = makeRegistry();
        resources.setPackageRegistry(registry);
        resources.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());
        return resources;
    }

    default String buildQueryString(String basequery, Resource loadedResources){
        StringBuilder builder = new StringBuilder();
        //Add metamodels from domain
        loadedResources.getContents().forEach(resource -> {
            if(resource instanceof EPackage epackage){
                builder.append("import \"%s\"".formatted(epackage.getNsURI())).append(System.lineSeparator());
            }
        });
        //Add Ecore 2002 just in case
        builder.append("import \"http://www.eclipse.org/emf/2002/Ecore\"").append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append(basequery).append(System.lineSeparator());

        return builder.toString();
    }
    public static record ParseResult(Boolean isCorrect, String diagnostics){
        @Override
        public final String toString() {
            return String.format(
            "{\"isCorrect\": %s, \"diagnostics\": \"%s\"}",
            isCorrect,
            //diagnostics == null ? "" : diagnostics.replace("\"", "\\\"")
            "Placeholder"
        );
        }
    }
}
