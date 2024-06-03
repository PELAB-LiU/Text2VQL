package se.liu.ida.sas.pelab.querycomparison

import com.google.inject.Guice
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet
import java.util.List
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory
import org.eclipse.xtext.resource.XtextResourceSet
import se.liu.ida.sas.pelab.vqlsyntaxcheck.Config
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl

class Comparison {
	val PatternParsingResults queries1;
	val PatternParsingResults queries2;
	val AdvancedViatraQueryEngine engine;
	val ResourceSet resourceSet
	
	def static void main(String[] args){
		val cfg = new Config(args)
		
		val rs = setup()
		
		loadMetamodel(cfg.asString("meta"),rs)
		
		val instance = new Comparison(cfg, rs);
		instance.compare(cfg.asString("query1"), cfg.asString("query2"))
	}
	
	def compare(String query1, String query2){
		if(!isCompatible(query1,query2)){
			println("Incompatible query header.")
			return
		}
		val matches1 = matchesOf(queries1, query1).map([asArray(it)])
		val matches2 = matchesOf(queries2, query2).map([asArray(it)])
		
		val common = common(matches1, matches2);
		val onlyQuery1 = minus(matches1, common)
		val onlyQuery2 = minus(matches2, common)
		
//		println("Matches")
//		matches1.forEach[m|println(Arrays.toString(m))]
//		println("Matches")
//		matches2.forEach[m|println(Arrays.toString(m))]
		
		println("|A ∩ B|="+common.size)
		println("|A \\ B|="+onlyQuery1.size)
		println("|B \\ A|="+onlyQuery2.size)
	}
	
	def minus(List<Object[]> ls1, List<Object[]> ls2){
		val common = newArrayList
		for(item1 : ls1){
			var contained = false
			for(item2 : ls2){
				contained = contained || Arrays.equals(item1,item2)
			}
			if(!contained){
				common.add(item1);
			}
		}
		return common
	}
	def common(List<Object[]> ls1, List<Object[]> ls2){
		val common = newArrayList
		for(item1 : ls1){
			for(item2 : ls2){
				if(Arrays.equals(item1,item2)){
					common.add(item1);
				}
			}
		}
		return common
	}
	
	def boolean isCompatible(String query1, String query2){
		val q1 = queries1.getQuerySpecification(query1).get
		val q2 = queries2.getQuerySpecification(query2).get
		
		if(q1.parameters.length !== q2.parameters.length){
			return false
		}
		return true
	}
	
	def asArray(IPatternMatch match){
		val cnt = match.parameterNames.length
		val array = newArrayOfSize(cnt);
		for(i : 0..cnt-1){
			//array.set(i,System.identityHashCode(match.get(i)))
			array.set(i,match.get(i))
		}
		return array
	}
	
	new(Config cfg, ResourceSet resourceSet){
		this.resourceSet = resourceSet
		queries1 = parseFile(cfg.asString("queries1"))
		queries2 = parseFile(cfg.asString("queries2"))
		
		loadModel(cfg.asString("instance"))
		
		engine = ViatraQueryEngine.on(new EMFScope(resourceSet)) as AdvancedViatraQueryEngine
	}
	private static def parseFile(String path){
		val parsed = PatternParserBuilder.instance.parse(
			Files.readString(
				Path.of(path)))
				
		var correct = ! parsed.hasError
		var  errors = parsed.errors
		
		if(!correct) {
			println("Input "+path+" contains errors.")
			errors.forEach[println(it)]
			return null
		}
		return parsed
	}
	
	private static def loadMetamodel(String path, ResourceSet resourceSet){
		val meta = resourceSet.getResource(
			URI.createFileURI(new File(path).absolutePath), true
		);
		val pkg = meta.contents.get(0) as EPackage
		EPackage.Registry.INSTANCE.put(pkg.nsURI, pkg)
	}
	
	private static def ResourceSet setup(){
		EMFPatternLanguageStandaloneSetup.doSetup()
		val runtimeModule = new StandaloneParserWithSeparateModules 
		val injector = Guice.createInjector(runtimeModule)
		val XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet)
		
		ViatraQueryEngineOptions.setSystemDefaultBackends(ReteBackendFactory.INSTANCE, ReteBackendFactory.INSTANCE,
			LocalSearchEMFBackendFactory.INSTANCE)
		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE)
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
			"ecore", new EcoreResourceFactoryImpl());
			
		return resourceSet
	}
	
	private def matchesOf(PatternParsingResults queries, String name){
		val matches = new ArrayList;
		queries.getQuerySpecification(name).ifPresent([specification | 
			val matcher = engine.getMatcher(specification)
			matches.addAll(matcher.allMatches)
		])
		return matches
	}
	
	private def loadModel(String path){
		val modelResource = resourceSet.getResource(URI.createFileURI(path), true)
		resourceSet.resources.forEach[res|println(res)]
	}
	
	
}