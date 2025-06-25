package se.liu.ida.sas.pelab.text2vql.ocl;

import org.eclipse.emf.common.util.TreeIterator; 
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.ecore.delegate.OCLInvocationDelegateFactory;
import org.eclipse.ocl.ecore.delegate.OCLSettingDelegateFactory;
import org.eclipse.ocl.ecore.delegate.OCLValidationDelegateFactory;
import org.eclipse.ocl.ecore.internal.OCLFactoryImpl;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.common.OCLConstants;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.pivot.internal.ModelImpl;
import org.eclipse.ocl.pivot.internal.ecore.es2as.Ecore2AS;
import org.eclipse.ocl.pivot.internal.utilities.PivotEnvironmentFactory;
import org.eclipse.ocl.xtext.completeocl.CompleteOCLStandaloneSetup;
import org.eclipse.ocl.xtext.completeocl.utilities.CompleteOCLASResourceImpl;
import org.eclipse.ocl.xtext.essentialocl.EssentialOCLStandaloneSetup;
import org.eclipse.ocl.xtext.oclinecore.OCLinEcoreStandaloneSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import se.liu.ida.sas.pelab.text2vql.ocl.helper.MatchProcessor;
import se.liu.ida.sas.pelab.text2vql.utilities.evaluation.MatchSetEvaluator;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.PackageHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.RailwayRuntimePackageHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.modeling.YakinduRuntimePackageHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.syntax.SyntaxChecker;

//import org.eclipse.ocl.pivot.utilities.OCL;
//import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.examples.standalone.StandaloneApplication;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

public class DebugQuery extends StandaloneApplication {
    private static PackageHelper packageHelper = new YakinduRuntimePackageHelper();
    //private static PackageHelper packageHelper = new RailwayRuntimePackageHelper();
    private static MatchProcessor matchProcessor = new MatchProcessor(MatchSetEvaluator.regex_object);
    
    //@Disabled
    @Test
    public void debug() throws ParserException, IOException, InterruptedException {
        CompleteOCLStandaloneSetup.doSetup();

        packageHelper.epackage.setNsURI("http://www.example.org/yakindu");
        EPackage.Registry.INSTANCE.put(packageHelper.epackage.getNsURI(), packageHelper.epackage);
        String query = """
                -- import 'http://www.example.org/yakindu'
                
                context Statechart
                foo() : Bag(Tuple(s : State)) =

                Vertex.allInstances()->select(state | state.oclIsTypeOf(Entry) or state.oclIsTypeOf(FinalState))->collect(state | Tuple{s=state})

                
                """;
// Guide from: https://help.eclipse.org/latest/index.jsp?topic=%2Forg.eclipse.ocl.doc%2Fhelp%2FPivotParsingDocuments.html
// and:
// https://download.eclipse.org/justj/?file=modeling/mdt/ocl/builds/release/6.22.0/plugins
//*/
  //      org.eclipse.ocl.xtext.completeocl.
          // *.ocl
        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        Environment environment = environmentFactory.createEnvironment();
        //environment.getUMLReflection().getClass();
        //OCL ocl = OCL.newInstance();
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        //ocl.getResourceSet().getPackageRegistry().put(packageHelper.epackage.getNsURI(), packageHelper.epackage);
        
        OCLHelper helper = ocl.createOCLHelper();

        Object foo = helper.defineOperation(query);
        //ocl.parse
        //var uri = tmpFile(query);
        /*var uri = tmpFIS(query);
        System.out.println("Foo "+uri);
        var foo = ocl.parse(new OCLInput(uri));
        System.out.println(foo);*/
        if(foo instanceof CompleteOCLASResourceImpl valami){
            System.out.println("Errors="+valami.getErrors().size());
            valami.getErrors().forEach(System.out::println);
            
        }
        
        Thread.sleep(1*60*1000);
    //org.eclipse.ocl.xtext.essentialocl.
    //EssentialOCLStandaloneSetup.doSetup(); // *.essentialocl

    //org.eclipse.ocl.xtext.oclinecore.
    //OCLinEcoreStandaloneSetup.doSetup();   // *.ecore, *.oclinecore

    //org.ecli/pse.ocl.xtext.oclstdlib.
  //  OCLstdlibStandaloneSetup.doSetup();    // *.oclstdlib
//*/    
        
        
        
        
        
        
        
//Guide with Pivot        
/*
        var registry = new EPackageRegistryImpl();
        registry.put(packageHelper.epackage.getNsURI(), packageHelper.epackage);
        
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
        //TODO may need to be adapted
        Resource resource = resourceSet.getResource(URI.createURI("Example.test"), true);
        EObject root = resource.getContents().get(0);
        
        String oclDelegateURI = OCLConstants.OCL_DELEGATE_URI_PIVOT;
        registry.put(oclDelegateURI, new OCLInvocationDelegateFactory.Global());
        registry.put(oclDelegateURI, new OCLSettingDelegateFactory.Global());
        registry.put(oclDelegateURI, new OCLValidationDelegateFactory.Global());
        
        //OCL.initialize(resourceSet);
        //Ecore2AS.initialize(resourceSet);
        //org.eclipse.ocl.examples.pivot.model.OCLstdlib.install();
        //org.eclipse.ocl.examples.pivot.delegate.OCLDelegateDomain.initialize(resourceSet);
        //org.eclipse.ocl.examples.xtext.completeocl.CompleteOCLStandaloneSetup.doSetup();
        //org.eclipse.ocl.examples.xtext.oclinecore.OCLinEcoreStandaloneSetup.doSetup();
        //org.eclipse.ocl.examples.xtext.oclstdlib.OCLstdlibStandaloneSetup.doSetup();
        //org.eclipse.ocl.examples.domain.utilities.StandaloneProjectMap.getAdapter(resourceSet);

        OCL ocl = OCL.newInstanceAbstract(new PivotEnvironmentFactory(registry, new MetamodelManager(resourceSet)));
 //*/        

//Old version
/*

        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        //OCL ocl = environmentFactory.createOCL();
        System.out.println(environmentFactory.getEPackageRegistry());
        OCL ocl = OCL.newInstance(environmentFactory);
        OCLHelper helper = ocl.createOCLHelper();

        //helper.setContext(packageHelper.epackage.getEClassifier("Statechart"));
        OCLExpression expression = helper.createQuery(query);
        //Object expression = helper.defineOperation(query);
        //System.out.println(ocl.getEnvironment());
        //var foo = ocl.parse(new OCLInput(query));
        //System.out.println("Foo:\n"+foo);
        //System.out.println(expression.getClass());
        //var OCLquery = ocl.createQuery(expression);
        //Object result = OCLquery.evaluate(makeModel());
        //matchProcessor.processContainer(result);
//*/
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
    private URI tmpFile(String value) throws IOException{
        File file = File.createTempFile("ocl-query", ".ocl");
        //file.deleteOnExit();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.append(value);
        writer.flush();
        writer.close();

        return URI.createFileURI(file.getAbsolutePath());

    }
    private FileInputStream tmpFIS(String value) throws IOException{
        File file = File.createTempFile("ocl-query", ".ocl");
        //file.deleteOnExit();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.append(value);
        writer.flush();
        writer.close();

        return new FileInputStream(file);

    }
}
