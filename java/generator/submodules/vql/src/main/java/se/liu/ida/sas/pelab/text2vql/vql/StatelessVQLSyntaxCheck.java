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
import org.eclipse.viatra.query.patternlanguage.emf.util.AdvancedPatternParser;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParser;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;

public interface StatelessVQLSyntaxCheck {
    public static final ConcurrentHashMap<File, Resource> loadedResources = new ConcurrentHashMap<>();

    static ResourceSet init(){
        EMFPatternLanguageStandaloneSetup.doSetup();
        StandaloneParserWithSeparateModules runtimeModule = new StandaloneParserWithSeparateModules();
        var injector = Guice.createInjector(runtimeModule);
        EPackage.Registry.INSTANCE.putIfAbsent(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);

        ResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().putIfAbsent(
                "ecore", new EcoreResourceFactoryImpl());

        return resourceSet;
    }

    /*static Resource loadMetamodelToGlobalPackageRegistry(File metamodel, ResourceSet resourceSet){
        if(loadedResources.containsKey(metamodel)){
            System.out.println("Metamodel is already loaded.");
            return loadedResources.get(metamodel);
        }
        Resource meta = resourceSet.getResource(URI.createFileURI(metamodel.getAbsolutePath()), true);
        loadedResources.put(metamodel, meta);

        getMetamodelsOfResource(meta).forEach(it -> EPackage.Registry.INSTANCE.putIfAbsent(it.getNsURI(), it));
        return meta;
    }*/
    
    static List<EPackage> getMetamodelsOfResource(Resource resource){
        return resource.getContents().stream()
            .filter(it -> it instanceof EPackage)
            .map(it -> (EPackage) it)
            .toList();
    }

    default ParseResult check(Resource metamodels, String baseQuery){
        /*
         * Setup resource set
         */
        PatternParserBuilder parserBuilder = PatternParserBuilder.instance();
        PatternParser parser = parserBuilder.build();

        String query = buildQueryString(baseQuery, metamodels);
        System.out.println(query);
        try{
            PatternParsingResults result = parser.parse(query);

            StringBuilder diagnostics = new StringBuilder();
            result.getAllDiagnostics().forEach(issue -> {
                diagnostics.append(issue.getMessage()).append(System.lineSeparator());
                System.out.println(issue);
            });
            return new ParseResult(!result.hasError(), diagnostics.toString());
        } catch (Exception e){
            return new ParseResult(false, e.getMessage());
        }
    }

    /*default Injector makeInjector(){
        StandaloneParserWithSeparateModules runtimeModule = new StandaloneParserWithSeparateModules();
        return Guice.createInjector(runtimeModule);
    }*/

    /*default EPackage.Registry makeRegistry(){
        return new EPackageRegistryImpl(EPackage.Registry.INSTANCE);
    }*/

    /*default ResourceSet makeLocalResourceSet(Injector injector){
        XtextResourceSet resources = injector.getInstance(XtextResourceSet.class);
        EPackage.Registry registry = makeRegistry();
        resources.setPackageRegistry(registry);
        resources.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());
        return resources;
    }*/

    default String buildQueryString(String basequery, Resource loadedResources){
        StringBuilder builder = new StringBuilder();
        //Add metamodels from domain
        getMetamodelsOfResource(loadedResources).forEach(epackage -> {
            builder.append("import \"%s\"".formatted(epackage.getNsURI())).append(System.lineSeparator());
        });
        //Add Ecore 2002 just in case
        builder.append("import \"http://www.eclipse.org/emf/2002/Ecore\"").append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append(basequery).append(System.lineSeparator());

        return builder.toString();
    }

    public static record ParseResult(Boolean isCorrect, String diagnostics){}
}
