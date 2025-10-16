package se.liu.ida.sas.pelab.text2vql.vql;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParser;
import com.google.inject.Guice;

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
    
    static List<EPackage> getMetamodelsOfResource(Resource resource){
        return resource.getContents().stream()
            .filter(it -> it instanceof EPackage)
            .map(it -> (EPackage) it)
            .toList();
    }
    default PatternParsingResults parse(Resource metamodels, String baseQuery){
        /*
         * Setup resource set
         */
        PatternParserBuilder parserBuilder = PatternParserBuilder.instance();
        PatternParser parser = parserBuilder.build();

        String query = buildQueryString(baseQuery, metamodels);
        //System.out.println(query);
        return parser.parse(query);
    }
    default PatternParsingResults safeParse(Resource metamodels, String baseQuery){
        try{
            PatternParsingResults result = parse(metamodels, baseQuery);
            if(result.hasError()){
                return null;
            }
            return result;
        } catch (Exception e){
            return null;
        }
    }
    default ParseResult check(Resource metamodels, String baseQuery){
        try{
            PatternParsingResults result = this.parse(metamodels, baseQuery);
            
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
