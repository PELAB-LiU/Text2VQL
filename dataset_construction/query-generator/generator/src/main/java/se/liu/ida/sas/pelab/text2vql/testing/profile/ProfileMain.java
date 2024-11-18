package se.liu.ida.sas.pelab.text2vql.testing.profile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.csv.CSVRecord;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Guice;
import se.liu.ida.sas.pelab.text2vql.testing.matchset.CSVHandler;

public class ProfileMain {
	static final String MODE = System.getenv("MODE");
	static final String CSV = System.getenv("CSV");
	static final String COL = System.getenv("COL");
	static final String OUT = System.getenv("OUT");
	
	public static void main(String[] args) throws IOException {
		switch (MODE) {
		case "IND": {
			csv_individual_proofile();
			break;
		}
		case "AGG": {
			csv_aggregate_proofile();
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + MODE);
		}
	}
	
	private static void csv_aggregate_proofile() {
		var input = (Iterable<CSVRecord>) CSVHandler.parse(CSV);
		
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
			var pattern = entry.get(COL);
			
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
	private static void csv_individual_proofile() throws IOException {
		var input = (Iterable<CSVRecord>) CSVHandler.parse(CSV);
		
		EMFPatternLanguageStandaloneSetup.doSetup();
		StandaloneParserWithSeparateModules runtimeModule = new StandaloneParserWithSeparateModules(); 
		var injector = Guice.createInjector(runtimeModule);
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		
		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
			"ecore", new EcoreResourceFactoryImpl());
		
	    PrintWriter printWriter = new PrintWriter(new FileWriter(OUT));
		
		
		//var profiler = new QueryProfile();
	    String line = "id,"+QueryProfile.header();
		System.out.println(line);
		printWriter.println(line);
		input.forEach(entry ->{
			var pattern = entry.get(COL);
			
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
