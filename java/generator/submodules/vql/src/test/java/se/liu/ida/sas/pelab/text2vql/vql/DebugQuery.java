package se.liu.ida.sas.pelab.text2vql.vql;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.RailwayRuntimePackageHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.ResourcesHelper;

public class DebugQuery {
    private static RailwayRuntimePackageHelper railway;
    private AdvancedViatraQueryEngine engine;
    private ResourceSet resources;

    //@Disabled
    @Test
    public void debug(){
        String query = """
                import "railway"
                import "http://www.eclipse.org/emf/2002/Ecore"
                
                pattern positive(s1: Segment){
                    Segment.length(s1,l);
                    check(l>=0);
                }
                """;
        PatternParsingResults result = PatternParserBuilder.instance().parse(query);

        if(result.hasError()){
            result.getErrors().forEach(System.out::println);
            return;
        }

        var specification = result.getQuerySpecification("positive").get();

        Resource resource = new ResourceImpl();
        resource.getContents().add(makeModel());
        resources.getResources().add(resource);

        var matches = engine.getMatcher(specification).getAllMatches();

        matches.forEach(match-> System.out.println(match.prettyPrint()));
    }

    private EObject makeModel(){
        var factory = railway.factory;

        var container = railway.make("RailwayContainer");
        var region = railway.make("Region");
        var segment1 = railway.make("Segment");
        var segment2 = railway.make("Segment");

        railway.add(container, "regions", region);
        railway.add(region, "elements", segment1);
        railway.add(region, "elements", segment2);

        railway.<Integer>set(segment1, "length", -1);
        railway.<Integer>set(segment2, "length", 1);

        return container;
    }

    @BeforeEach
    public void setupEnv(){
        resources = new ResourceSetImpl();
        engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(resources));
    }
    @BeforeAll
    public static void setupGlobal(){
        EMFPatternLanguageStandaloneSetup.doSetup();
        var runtimeModule = new EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules();
        Injector injector = Guice.createInjector(runtimeModule);
        //XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);

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
        railway = new RailwayRuntimePackageHelper((EPackage) meta.getContents().getFirst());
        EPackage.Registry.INSTANCE.put(railway.railway.getNsURI(), railway.railway);
    }
}
