package se.liu.ida.sas.pelab.text2vql.acceleo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.acceleo.query.AQLUtils;
import org.eclipse.acceleo.query.runtime.IQueryEnvironment;
import org.eclipse.acceleo.query.runtime.impl.namespace.ClassLoaderQualifiedNameResolver;
import org.eclipse.acceleo.query.runtime.impl.namespace.JavaLoader;
import org.eclipse.acceleo.query.runtime.namespace.IQualifiedNameQueryEnvironment;
import org.eclipse.acceleo.query.runtime.namespace.IQualifiedNameResolver;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//https://eclipse.dev/acceleo/documentation/
//https://github.com/eclipse-acceleo/acceleo/blob/master/plugins/org.eclipse.acceleo.aql.doc/pages/index.adoc#7-using-acceleo-4-programmatically
public class DebugQuery2 {
	@Test
	public void maven() {
		EcorePackage.eINSTANCE.getName();
		//var resourceSetForModels = new ResourceSetImpl();
		//resourceSetForModels.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
		
/*
		final IQualifiedNameResolver resolver = new ClassLoaderQualifiedNameResolver(getClass()
				.getClassLoader(), AcceleoParser.QUALIFIER_SEPARATOR);
		final Map<String, String> options = new HashMap<>();
		final ArrayList<Exception> exceptions = new ArrayList<>();
		final ResourceSet resourceSetForModels = AQLUtils.createResourceSetForModels(exceptions, resolver, new ResourceSetImpl(), options);
		
		resourceSetForModels.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

		final IQualifiedNameQueryEnvironment queryEnvironment = AcceleoUtil.newAcceleoQueryEnvironment(
				options, resolver, resourceSetForModels, false);
		AcceleoEvaluator evaluator = new AcceleoEvaluator(queryEnvironment.getLookupEngine(), "\n");
		
		resolver.addLoader(new ModuleLoader(new AcceleoParser(), evaluator));
		resolver.addLoader(new JavaLoader(AcceleoParser.QUALIFIER_SEPARATOR, false));
		
		final Object resolved = resolver.resolve(moduleQualifiedName);
		final Module mainModule;
		if (resolved instanceof Module) {
			mainModule = (Module)resolved;
		} else {
			mainModule = null;
		}*/
	}
}
