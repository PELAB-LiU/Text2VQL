package se.liu.ida.sas.pelab.text2vql.acceleo;

//Start of user code copyright
/*******************************************************************************
 * Copyright (c) 2025 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
//End of user code

//Start of user code imports

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.acceleo.Module;
import org.eclipse.acceleo.Template;
import org.eclipse.acceleo.aql.AcceleoUtil;
import org.eclipse.acceleo.aql.evaluation.AcceleoEvaluator;
import org.eclipse.acceleo.aql.evaluation.AcceleoProfilerEvaluator;
import org.eclipse.acceleo.aql.evaluation.strategy.DefaultGenerationStrategy;
import org.eclipse.acceleo.aql.evaluation.strategy.DefaultWriterFactory;
import org.eclipse.acceleo.aql.evaluation.strategy.IAcceleoGenerationStrategy;
import org.eclipse.acceleo.aql.parser.AcceleoParser;
import org.eclipse.acceleo.aql.parser.ModuleLoader;
import org.eclipse.acceleo.aql.profiler.IProfiler;
import org.eclipse.acceleo.aql.profiler.ProfileResource;
import org.eclipse.acceleo.query.AQLUtils;
import org.eclipse.acceleo.query.ast.EClassifierTypeLiteral;
import org.eclipse.acceleo.query.ast.TypeLiteral;
import org.eclipse.acceleo.query.runtime.impl.namespace.ClassLoaderQualifiedNameResolver;
import org.eclipse.acceleo.query.runtime.impl.namespace.JavaLoader;
import org.eclipse.acceleo.query.runtime.namespace.IQualifiedNameQueryEnvironment;
import org.eclipse.acceleo.query.runtime.namespace.IQualifiedNameResolver;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.BasicMonitor.Printing;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.emf.ecore.EcorePackage;

//End of user code
//https://github.com/eclipse-acceleo/acceleo/blob/master/examples/MavenLauncher/org.eclipse.acceleo.aql.launcher.sample/src/org/eclipse/acceleo/aql/launcher/sample/MainGenerator.java
/**
 * Standalone launcher for org::eclipse::acceleo::aql::launcher::sample::main.
 * 
 * @author <a href="mailto:yvan.lussaud@obeo.fr">Yvan Lussaud</a>
 * @generated
 */
public class Rangom {

	/**
	 * The {@link List} of resources to load.
	 * 
	 * @generated
	 */
	protected final List<String> resources;

	/**
	 * The target folder for the generation.
	 * 
	 * @generated
	 */
	protected final String target;

	/**
	 * Constructor.
	 * 
	 * @param resources
	 *                  the {@link List} of model resources to load
	 * @param target
	 *                  the target folder for the generation
	 * @generated
	 */
	public Rangom(List<String> resources, String target) {
		this.resources = resources;
		this.target = target;
	}

	/**
	 * Main entry point.
	 * 
	 * @param args
	 *             resources separated by a comma, target folder
	 * @generated
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			final List<String> resources = new ArrayList<>();
			for (String resource : args[0].split(",")) {
				resources.add(resource.trim());
			}
			final String target = args[1];
			final Rangom generator = new Rangom(resources, target);
			generator.generate(getMonitor());
		} else {
			printUsage();
		}

	}

	/**
	 * Print the usage.
	 * 
	 * @generated
	 */
	private static void printUsage() {
		System.out.println("Usage: <resources> <target>");
		System.out.println("Example: model1.xmi,model2.xmi src-gen/");
	}

	/**
	 * Gets the progress {@link Monitor}.
	 * 
	 * @return the progress {@link Monitor}
	 * @generated
	 */
	private static Monitor getMonitor() {
		return new Printing(new PrintStream(System.out));
	}

	/**
	 * Generates.
	 * 
	 * @param monitor
	 *                the progress {@link Monitor}
	 * @generated
	 */
	public void generate(Monitor monitor) {
		// inputs
		final String moduleQualifiedName = getModuleQualifiedName();
		final URI targetURI = getTargetURI(target);
		final Map<String, String> options = getOptions();






		// create the resource set used to load models
		final Object generationKey = new Object();
		final List<Exception> exceptions = new ArrayList<>();
		final ResourceSet resourceSet = new ResourceSetImpl();
		final ResourceSet resourceSetForModels = AQLUtils.createResourceSetForModels(exceptions,
				generationKey, resourceSet, options);
				// initialize EPackages
		EcorePackage.eINSTANCE.getName();
		// register default XMI resource factory
		resourceSetForModels.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());






		// prepare Acceleo environment
		final IQualifiedNameResolver resolver = new ClassLoaderQualifiedNameResolver(this.getClass()
				.getClassLoader(), AcceleoParser.QUALIFIER_SEPARATOR);
		final IQualifiedNameQueryEnvironment queryEnvironment = AcceleoUtil.newAcceleoQueryEnvironment(
				options, resolver, resourceSetForModels, false);
		final AcceleoEvaluator evaluator = new AcceleoEvaluator(queryEnvironment.getLookupEngine(), System
				.lineSeparator());
		resolver.addLoader(new ModuleLoader(new AcceleoParser(), evaluator));
		resolver.addLoader(new JavaLoader(AcceleoParser.QUALIFIER_SEPARATOR, false));
		final IAcceleoGenerationStrategy strategy = new DefaultGenerationStrategy(resourceSetForModels
				.getURIConverter(), new DefaultWriterFactory());//To be removed?????????




		final Module module = (Module) resolver.resolve(moduleQualifiedName);
		AcceleoUtil.registerEPackage(queryEnvironment, resolver, module);
		final URI logURI = AcceleoUtil.getlogURI(targetURI, options.get(AcceleoUtil.LOG_URI_OPTION));
		final List<Template> mainTemplates = AcceleoUtil.getMainTemplates(module);






		monitor.beginTask("Generating", resources.size() + 1 + mainTemplates.size() * resources.size() + 1);
		// load models
		final List<Resource> modelResources = loadResources(resourceSetForModels, resources, monitor);

		monitor.subTask("Before generation");
		monitor.worked(1);
		try {
			final Map<EClass, List<EObject>> valuesCache = new LinkedHashMap<>();
			for (Template template : mainTemplates) {
				final EClassifierTypeLiteral eClassifierTypeLiteral = (EClassifierTypeLiteral) template
						.getParameters().get(0).getType().getAst();
				final List<EObject> values = getValues(queryEnvironment, valuesCache, eClassifierTypeLiteral,
						resourceSetForModels, modelResources, monitor);

				final String parameterName = template.getParameters().get(0).getName();
				Map<String, Object> variables = new LinkedHashMap<>();
				for (EObject value : values) {
					variables.put(parameterName, value);
					AcceleoUtil.generate(template, variables, evaluator, queryEnvironment, strategy,
							targetURI, logURI, monitor);
					if (monitor.isCanceled()) {
						return;
					}
				}
				monitor.worked(1);
				if (monitor.isCanceled()) {
					return;
				}
			}
		} finally {
			if (evaluator instanceof AcceleoProfilerEvaluator) {
				IProfiler profiler = ((AcceleoProfilerEvaluator) evaluator).getProfiler();
				ProfileResource profileResource = profiler.getResource();
				profileResource.setStartResource(resolver.getSourceURI(moduleQualifiedName).toString());
				try {
					profiler.save(URI.createURI(targetURI.toString() + "/" + module.getName() + ".mtlp"));
				} catch (IOException e) {
					final Diagnostic diagnostic = new BasicDiagnostic(Diagnostic.ERROR, getClass()
							.getCanonicalName(), 0, e.getMessage(), new Object[] { e });
					evaluator.getGenerationResult().addDiagnostic(diagnostic);
				}
			}
			AQLUtils.cleanResourceSetForModels(generationKey, resourceSetForModels);
			AcceleoUtil.cleanServices(queryEnvironment, resourceSetForModels);
			monitor.subTask("After generation");
			monitor.worked(1);
		}
	}


	/**
	 * Gets the {@link List} of {@link EObject} values to use.
	 * 
	 * @param queryEnvironment
	 *                             the {@link IQualifiedNameQueryEnvironment}
	 * @param valuesCache
	 *                             the cache for any previous values
	 * @param type
	 *                             the {@link TypeLiteral}
	 * @param resourceSetForModels
	 *                             the {@link ResourceSet} for models
	 * @param modelResources
	 *                             the {@link List} of loaded {@link Resource}
	 * @param monitor
	 *                             the progress {@link Monitor}, it must consumes
	 *                             the resources.size()
	 * @return the {@link List} of {@link EObject} values to use
	 * @generated
	 */
	protected List<EObject> getValues(IQualifiedNameQueryEnvironment queryEnvironment,
			Map<EClass, List<EObject>> valuesCache, TypeLiteral type,
			ResourceSet resourceSetForModels, List<Resource> modelResources, Monitor monitor) {
		final List<EObject> values = AcceleoUtil.getValues(type, queryEnvironment, modelResources,
				valuesCache, monitor);
		return values;
	}

	/**
	 * Gets the module qualified name.
	 * 
	 * @return the module qualified name
	 * @generated
	 */
	protected String getModuleQualifiedName() {
		return "org::eclipse::acceleo::aql::launcher::sample::main";
	}

	/**
	 * Gets the target folder {@link URI}.
	 * 
	 * @param target
	 *               the target folder {@link String}.
	 * @return the target folder {@link URI}
	 * @generated
	 */
	protected URI getTargetURI(String target) {
		return URI.createFileURI(new File(target).getAbsolutePath() + "/");
	}

	/**
	 * Gets the {@link Map} of options for the generation.
	 * 
	 * @return the {@link Map} of options for the generation
	 * @generated
	 */
	protected Map<String, String> getOptions() {
		Map<String, String> res = new LinkedHashMap<>();
		res.put(AcceleoUtil.LOG_URI_OPTION, "acceleo.log");
		res.put(AcceleoUtil.NEW_LINE_OPTION, System.lineSeparator());
		return res;
	}



	/**
	 * Loads {@link Resource} in the given {@link ResourceSet} for models.
	 * 
	 * @param resourceSetForModels
	 *                             the {@link ResourceSet} for models
	 * @param resources
	 *                             the {@link List} of resource names to load
	 * @param monitor
	 *                             the progress {@link Monitor}, it must consumes
	 *                             the number of resources
	 * @return the {@link List} of loaded {@link Resource}
	 * @generated
	 */
	protected List<Resource> loadResources(ResourceSet resourceSetForModels, List<String> resources, Monitor monitor) {
		final List<Resource> res = new ArrayList<>();

		for (String resource : resources) {
			monitor.subTask("Loading " + resource);
			final Resource loaded = resourceSetForModels.getResource(URI.createURI(resource, true), true);
			if (loaded != null) {
				res.add(loaded);
			}
			monitor.worked(1);
			if (monitor.isCanceled()) {
				break;
			}
		}

		return res;
	}
}