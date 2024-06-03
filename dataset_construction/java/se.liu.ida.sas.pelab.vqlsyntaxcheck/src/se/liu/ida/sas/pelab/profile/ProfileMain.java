package se.liu.ida.sas.pelab.profile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Guice;

import se.liu.ida.sas.pelab.querycomparison.CSVHandler;
import se.liu.ida.sas.pelab.vqlsyntaxcheck.Config;

public class ProfileMain {

	public static void main(String[] args) throws IOException {
		var cfg = new Config(args);
		if(cfg.isDefined("aggregate")) {
			csv_aggregate_proofile(cfg);
		} else {
			csv_individual_proofile(cfg);
		}
	}
	
	private static void csv_aggregate_proofile(Config cfg) {
		var input = CSVHandler.parse(cfg.asString("csv"));
		
		EMFPatternLanguageStandaloneSetup.doSetup();
		StandaloneParserWithSeparateModules runtimeModule = new StandaloneParserWithSeparateModules(); 
		var injector = Guice.createInjector(runtimeModule);
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		
		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
			"ecore", new EcoreResourceFactoryImpl());
		
		
		var profiler = new QueryProfile();
	    String line = "id,"+QueryProfile.header();

		input.forEach(entry ->{
			var pattern = entry.get(cfg.asString("col"));
			
			var parsed = PatternParserBuilder.instance().parse(""
					+ "import \"http://www.eclipse.org/emf/2002/Ecore\"\r\n"
					+ ""
					+ pattern
					);
			System.out.println(pattern);
			profiler.processParsed(parsed);
		});

		System.out.println(profiler);
	}
	private static void csv_individual_proofile(Config cfg) throws IOException {
		var input = CSVHandler.parse(cfg.asString("csv"));
		
		EMFPatternLanguageStandaloneSetup.doSetup();
		StandaloneParserWithSeparateModules runtimeModule = new StandaloneParserWithSeparateModules(); 
		var injector = Guice.createInjector(runtimeModule);
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		
		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
			"ecore", new EcoreResourceFactoryImpl());
		
	    PrintWriter printWriter = new PrintWriter(new FileWriter(cfg.asString("out")));
		
		
		//var profiler = new QueryProfile();
	    String line = "id,"+QueryProfile.header();
		System.out.println(line);
		printWriter.println(line);
		input.forEach(entry ->{
			var pattern = entry.get(cfg.asString("col"));
			
			var parsed = PatternParserBuilder.instance().parse(""
					+ "import \"http://www.eclipse.org/emf/2002/Ecore\"\r\n"
					+ ""
					+ pattern
					);
			var profiler = new QueryProfile();
			profiler.processParsed(parsed);
			String line1 = entry.get("id")+","+profiler.data();
			System.out.println(line1);
			printWriter.println(line1);
			
		});
		printWriter.flush();
		printWriter.close();
		//System.out.println(profiler);
	}
}
