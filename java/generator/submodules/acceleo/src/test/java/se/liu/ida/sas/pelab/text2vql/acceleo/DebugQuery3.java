package se.liu.ida.sas.pelab.text2vql.acceleo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.acceleo.Module;
import org.eclipse.acceleo.Query;
import org.eclipse.acceleo.aql.AcceleoUtil;
import org.eclipse.acceleo.aql.evaluation.AcceleoEvaluator;
import org.eclipse.acceleo.aql.evaluation.AcceleoProfilerEvaluator;
import org.eclipse.acceleo.aql.evaluation.strategy.DefaultGenerationStrategy;
import org.eclipse.acceleo.aql.evaluation.strategy.DefaultWriterFactory;
import org.eclipse.acceleo.aql.evaluation.strategy.IAcceleoGenerationStrategy;
import org.eclipse.acceleo.aql.parser.AcceleoParser;
import org.eclipse.acceleo.aql.parser.ModuleLoader;
import org.eclipse.acceleo.aql.profiler.IProfiler;
import org.eclipse.acceleo.aql.profiler.ProfilerPackage;
import org.eclipse.acceleo.aql.profiler.ProfilerUtils;
import org.eclipse.acceleo.aql.profiler.ProfilerUtils.Representation;
import org.eclipse.acceleo.aql.validation.AcceleoValidator;
import org.eclipse.acceleo.aql.validation.IAcceleoValidationResult;
import org.eclipse.acceleo.impl.ErrorQueryImpl;
import org.eclipse.acceleo.query.AQLUtils;
import org.eclipse.acceleo.query.parser.AstValidator;
import org.eclipse.acceleo.query.runtime.IQueryEnvironment;
import org.eclipse.acceleo.query.runtime.IValidationResult;
import org.eclipse.acceleo.query.runtime.impl.QueryBuilderEngine;
import org.eclipse.acceleo.query.runtime.impl.QueryEvaluationEngine;
import org.eclipse.acceleo.query.runtime.impl.namespace.ClassLoaderQualifiedNameResolver;
import org.eclipse.acceleo.query.runtime.impl.namespace.JavaLoader;
import org.eclipse.acceleo.query.runtime.namespace.IQualifiedNameQueryEnvironment;
import org.eclipse.acceleo.query.runtime.namespace.IQualifiedNameResolver;
import org.eclipse.acceleo.query.validation.type.EClassifierType;
import org.eclipse.acceleo.query.validation.type.IType;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//https://github.com/eclipse-acceleo/acceleo/blob/master/plugins/org.eclipse.acceleo.aql.doc/pages/index.adoc#5-the-acceleo-language
//https://eclipse.dev/acceleo/documentation/
//https://github.com/eclipse-acceleo/acceleo/blob/master/plugins/org.eclipse.acceleo.aql.doc/pages/index.adoc#7-using-acceleo-4-programmatically
public class DebugQuery3 extends Util{
	private String query = 
"""
[module generate('http://www.eclipse.org/emf/2002/Ecore')/]

[query public generateElement(parameter : Integer) : Integer = parameter.query()/]
""";

	private String query1 = 
"""
[module main('http://www.eclipse.org/emf/2002/Ecore','railway')/]

[query public goActiveRoute(model : railway::RailwayContainer) : railway::Route = model.routess
/]
""";

	private String query2 = 
"""
[module main('http://www.eclipse.org/emf/2002/Ecore','railway')/]

[query public goActiveRoute(model : railway::RailwayContainer) : Set(railway::Route) =
	model.routes->select(route : route::Route | route.active and route.entry.signal = Signal#GO)
/]
""";

	@Test
	public void maven1() {
		EcorePackage.eINSTANCE.getName();
		//AcceleoUtil.registerEPackage(null, null, null);

		var parser = new AcceleoParser();
		var results = parser.parse(query, "UTF-8", "text2vql::debug");


		System.out.println(results.getClass().getSimpleName());
		results.getErrors().forEach(System.out::println);

		var module = results.getModule();
		module.getModuleElements().forEach(System.out::println);
		

		final IQualifiedNameResolver resolver = new ClassLoaderQualifiedNameResolver(getClass()
			.getClassLoader(), AcceleoParser.QUALIFIER_SEPARATOR);
		final Map<String, String> options = new HashMap<>();
		var env = AcceleoUtil.newAcceleoQueryEnvironment(options, resolver, new ResourceSetImpl(), false);

		env.registerEPackage(EcorePackage.eINSTANCE);
		env.registerEPackage(packageHelper.epackage);

		var builder = new QueryBuilderEngine();
		var ast = builder.build(query);
		System.out.println("Errors:");
		ast.getErrors().forEach(System.out::println);

		System.out.println("Validation:");
		//Map<String, Set<IType>> variableTypes = new LinkedHashMap<String, Set<IType>>();
		//Set<IType> selfTypes = new LinkedHashSet<IType>();
		//selfTypes.add(new EClassifierType(env, EcorePackage.eINSTANCE.getEPackage()));
		//selfTypes.add(new EClassifierType(env, getEClass(packageHelper.epackage)));
		//variableTypes.put("self", selfTypes);
		AcceleoValidator validator = new AcceleoValidator(env);
		validator.validate(module.getAst(), "text2vql::debug");
		
		var engine = new QueryEvaluationEngine(env);
		var matches = engine.eval(ast, Maps.newHashMap());

		System.out.println("Matches:");
		System.out.println(matches);
		System.out.println(matches.getResult());
	}

	@Test
	public void maven() {
		EcorePackage.eINSTANCE.getName();
		//AcceleoUtil.registerEPackage(null, null, null);

		var parser = new AcceleoParser();
		var results = parser.parse(query, "UTF-8", "text2vql::debug");


		System.out.println(results.getClass().getSimpleName());
		results.getErrors().forEach(System.out::println);

		var module = results.getModule();
		module.getModuleElements().forEach(System.out::println);
		
		System.out.println(results.getModule().getClass().getSimpleName());
		
		final IQualifiedNameResolver resolver = new ClassLoaderQualifiedNameResolver(getClass()
			.getClassLoader(), AcceleoParser.QUALIFIER_SEPARATOR);
		final Map<String, String> options = new HashMap<>();
		final ArrayList<Exception> exceptions = new ArrayList<>();
		final ResourceSet resourceSetForModels = AQLUtils.createResourceSetForModels(exceptions, resolver, new ResourceSetImpl(), options);

		resourceSetForModels.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

		final IQualifiedNameQueryEnvironment queryEnvironment = AcceleoUtil.newAcceleoQueryEnvironment(
			options, resolver, resourceSetForModels, false);

		AcceleoEvaluator evaluator = new AcceleoEvaluator(queryEnvironment.getLookupEngine(), "\n");

		final AcceleoValidator acceleoValidator = new AcceleoValidator(queryEnvironment);
		final IAcceleoValidationResult acceleoValidationResult = acceleoValidator.validate(module.getAst(), "main");
		System.out.println(acceleoValidationResult);
	}
}
