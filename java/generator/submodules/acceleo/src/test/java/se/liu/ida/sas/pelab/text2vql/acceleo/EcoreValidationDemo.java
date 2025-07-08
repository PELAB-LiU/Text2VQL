package se.liu.ida.sas.pelab.text2vql.acceleo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
import org.eclipse.acceleo.query.parser.AstEvaluator;
import org.eclipse.acceleo.query.parser.AstValidator;
import org.eclipse.acceleo.query.runtime.IQueryEnvironment;
import org.eclipse.acceleo.query.runtime.IValidationResult;
import org.eclipse.acceleo.query.runtime.impl.EvaluationServices;
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
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

import se.liu.ida.sas.pelab.text2vql.utilities.modeling.RailwayRuntimePackageHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//https://github.com/eclipse-acceleo/acceleo/blob/master/plugins/org.eclipse.acceleo.aql.doc/pages/index.adoc#5-the-acceleo-language
//https://eclipse.dev/acceleo/documentation/
//https://github.com/eclipse-acceleo/acceleo/blob/master/plugins/org.eclipse.acceleo.aql.doc/pages/index.adoc#7-using-acceleo-4-programmatically
public class EcoreValidationDemo {
	private String query = 
"""
[module generate('http://www.eclipse.org/emf/2002/Ecore')/]

[query public generateElement(parameter : ecore::EClassifier) : ecore::EClassifier = parameter/]
""";

	@Test
	public void maven1() {
		EcorePackage.eINSTANCE.getName();
		//AcceleoUtil.registerEPackage(null, null, null);

		var parser = new AcceleoParser();
		var results = parser.parse(query, "UTF-8", "generate");
		System.out.println(results.getClass().getSimpleName());
		results.getErrors().forEach(System.out::println);
		var module = results.getModule();
		module.getModuleElements().forEach(System.out::println);
		//System.out.println("Name: "+module.getName());

		
		final Map<String, String> options = new HashMap<>();
		
		/**
		 * Configure resource set
		 */
		var resourceSet = new ResourceSetImpl();
		List<Exception> exceptions = new ArrayList<>();
		Object key = new Object();
		var resourceSetForModels = AQLUtils.createResourceSetForModels(exceptions, key, resourceSet, options);
		
		/**
		 * Configure environment
		 */
		final IQualifiedNameResolver resolver = new ClassLoaderQualifiedNameResolver(getClass()
			.getClassLoader(), AcceleoParser.QUALIFIER_SEPARATOR);


		var env = AcceleoUtil.newAcceleoQueryEnvironment(options, resolver, resourceSet, false);
		env.registerEPackage(EcorePackage.eINSTANCE);

		var resource = resourceSetForModels.createResource(URI.createFileURI("tmp.xmi"));
		//var resource = new ResourceImpl();
		//resourceSet.getResources().add(resource);
		System.out.println(resourceSetForModels.getClass());
		var pkg = new RailwayRuntimePackageHelper();
		resource.getContents().add(pkg.railway);
		
		
		
		/*var builder = new QueryBuilderEngine();
		var ast = builder.build(query);
		System.out.println("Errors:");
		ast.getErrors().forEach(System.out::println);*/

		System.out.println("Validation:");
		AcceleoValidator validator = new AcceleoValidator(env);
		var validationResult = validator.validate(module.getAst(), "generate");
		validationResult.getValidationMessages().forEach(System.out::println);
		

		AstEvaluator evaluator = new AstEvaluator(new EvaluationServices(env));
		var query = (Query) module.getModuleElements().get(0);
		
		Map<String,Object> vars = new HashMap<>();
		vars.put("self", pkg.epackage);
		var matches = evaluator.eval(vars, query.getTypeAql());

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
