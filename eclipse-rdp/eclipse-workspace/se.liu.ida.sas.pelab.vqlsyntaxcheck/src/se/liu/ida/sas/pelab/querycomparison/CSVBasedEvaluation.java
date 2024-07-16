package se.liu.ida.sas.pelab.querycomparison;

import static java.lang.System.out;

import java.io.File;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.Tuple;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.Arrays;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;

import com.google.inject.Guice;

public class CSVBasedEvaluation {
	private final ResourceSet resources;
	private final Iterable<CSVRecord> input;
	private final CSVHandler output;
	private final ModelLoader models;
	private final String aitemplate;
	
	static final String META_PATH = System.getenv("META");
	static final String INPUT_CSV = System.getenv("INPUT");
	static final String OUTPUT_CSV = System.getenv("OUTPUT");
	static final String INSTANCERID = System.getenv("INSTANCEDIR");
	static final String AI = System.getenv("AI");
	
	/**
	 * Configure: meta=pathToMetamodel input=pathToInputCSV output=pathToOutputCSV
	 */
	public static void main(String[] args) {
		/*if (args.length == 0) {
			out.println("Required arguments:");
			out.println("\tmeta=path to Metamodel");
			out.println("\tinput=path to input CSV");
			out.println("\toutput=path to output CSV");
			out.println("\tinstancedir=path to Instance Model directory");
			return;
		}
		var cfg = new Config(args);*/
		var rs = setup();
		loadMetamodel(META_PATH,rs);

		var eval = new CSVBasedEvaluation(rs);
		eval.run();

	}

	public void run() {
		AdvancedViatraQueryEngine engine = (AdvancedViatraQueryEngine) ViatraQueryEngine.on(new EMFScope(resources));

		input.forEach(record -> {
			System.out.println("[" + record.get("id")
					+ "]=================================================================================");

			var human = QueryUtility.parse(record.get("truth"));

			var parsed = new PatternParsingResults[] { 
					QueryUtility.parse(record.get(aitemplate.replace("<idx>", "0"))),
					QueryUtility.parse(record.get(aitemplate.replace("<idx>", "1"))),
					QueryUtility.parse(record.get(aitemplate.replace("<idx>", "2"))),
					QueryUtility.parse(record.get(aitemplate.replace("<idx>", "3"))),
					QueryUtility.parse(record.get(aitemplate.replace("<idx>", "4"))) };
			var name = record.get("header").split(" ")[1].split("\\(")[0];

			var syntax = new Boolean[parsed.length];
			var evaluation = new Boolean[parsed.length];
			for (int i = 0; i < parsed.length; i++) {
				syntax[i] = !parsed[i].hasError();
				evaluation[i] = syntax[i];
			}
			
			Pair<Integer,Boolean[]> result = models.<Pair<Integer,Boolean[]>>fold(Pair.of(0, evaluation), (seed, xmi) -> {

				List<String> base = QueryUtility.matchesSortedString(human, name, engine);
				var count = seed.getKey() + base.size();
				var state = seed.getValue();
				
				for (int i = 0; i < state.length; i++) {
					if (state[i]) {
						// Error-free parsing AND no difference from earlier models
						boolean equlaity = QueryUtility.matchesEqual(parsed[i], name, engine, base);
						state[i] = equlaity;
					}
				}
				return Pair.of(count, state);
			});
			
			System.out.println("syntax=" + java.util.Arrays.toString(syntax));
			System.out.println("result=" + java.util.Arrays.toString(result.getValue()));

			output.put(CSVHandler.OUT_ID, record.get("id"));
			output.put(CSVHandler.OUT_CORRECT, Arrays.<Boolean>contains(result.getValue(), true));
			output.put(CSVHandler.OUT_BASEMATCHCOUNT, result.getKey());
			output.put(CSVHandler.OUT_SYNT, java.util.Arrays.toString(syntax));
			output.put(CSVHandler.OUT_MATCH, java.util.Arrays.toString(result.getValue()));
			output.commit();
		});
		output.close();
		/*
		 * for( int i = 0; i < result.length; i++) { if(parsed[i].hasError()) {
		 * syntax[i] = false; result[i] = false; } else {// Error-free parsing syntax[i]
		 * = true; //var matches = QueryUtility.matches(parsed[i], name, engine); //var
		 * equlaity = QueryUtility.isEqual(base, matches); List<String> matches =
		 * QueryUtility.matchesSortedString(parsed[i], name, engine); boolean equlaity =
		 * QueryUtility.isEqualString(base, matches); result[i] = equlaity; }
		 * System.out.println("syntax["+i+"]="+syntax[i]);
		 * System.out.println("match["+i+"]="+result[i]); }
		 * 
		 * output.put(CSVHandler.OUT_ID, record.get("id"));
		 * output.put(CSVHandler.OUT_CORRECT, Arrays.<Boolean>contains(result, true));
		 * output.put(CSVHandler.OUT_BASEMATCHCOUNT, base.size());
		 * output.put(CSVHandler.OUT_SYNT, java.util.Arrays.toString(syntax));
		 * output.put(CSVHandler.OUT_MATCH, java.util.Arrays.toString(result));
		 * output.commit();
		 * 
		 * //engine.wipe(); //System.gc(); //});
		 */
	}

	public CSVBasedEvaluation(ResourceSet resources) {
		this.resources = resources;
		// this.models = new ModelLoader(resources /*new ResourceSetImpl()*/,
		// cfg.asString("instancedir"));
		this.models = new SeparateModelLoader(resources, INSTANCERID);
		this.input = (Iterable<CSVRecord>) CSVHandler.parse(INPUT_CSV);
		CSVHandler csv;
		try {
			csv = new CSVHandler(OUTPUT_CSV);
		} catch (Exception e) {
			csv = new CSVHandler(null);
		}
		this.output = csv;
		this.aitemplate = AI;
	}

	private static ResourceSet setup() {
		EMFPatternLanguageStandaloneSetup.doSetup();
		var runtimeModule = new StandaloneParserWithSeparateModules();
		var injector = Guice.createInjector(runtimeModule);
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);

		ViatraQueryEngineOptions.setSystemDefaultBackends(ReteBackendFactory.INSTANCE, ReteBackendFactory.INSTANCE,
				LocalSearchEMFBackendFactory.INSTANCE);
		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());

		return resourceSet;
	}

	private static void loadMetamodel(String path, ResourceSet resourceSet) {
		var meta = resourceSet.getResource(URI.createFileURI(new File(path).getAbsolutePath()), true);
		var pkg = (EPackage) meta.getContents().get(0);
		EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
	}
}
