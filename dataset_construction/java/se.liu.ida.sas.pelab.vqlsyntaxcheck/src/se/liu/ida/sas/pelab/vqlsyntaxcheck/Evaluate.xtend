package se.liu.ida.sas.pelab.vqlsyntaxcheck

import java.nio.file.Files
import java.nio.file.Path
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl
import java.io.File
import org.eclipse.xtext.validation.Issue

class Evaluate {
	def static evaluate(String path, String vql) {
		val ResourceSet resourceSet = new ResourceSetImpl();
		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE)
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
			"ecore", new EcoreResourceFactoryImpl());
		
		val resource = resourceSet.getResource(
			URI.createFileURI(new File(path).absolutePath), true
		);
		
		val pkg = resource.contents.get(0) as EPackage
		if(pkg.nsURI===null){
			EPackage.Registry.INSTANCE.put("http://example.org/", pkg)
		} else {
			EPackage.Registry.INSTANCE.put(pkg.nsURI, pkg)
		}
		
			
		EMFPatternLanguageStandaloneSetup.doSetup()

		val parsed = PatternParserBuilder.instance.parse(vql)
		
		var correct = true
		var Iterable<Issue> errors = null
		if(parsed.hasError){
			errors = parsed.errors.filter[!it.message.startsWith("Cannot find type for reference ")]
			correct = errors.isEmpty
		}
		if(correct){
			//println("Input is OK.")
			return true
		} else {
			// errors?.forEach[println(it)]
			return false
			//println("Input has errors.")
			//errors.forEach[println(it)]
		}
	}
	
	def static void main(String[] args) {
		val cfg = new Config(args)
		evaluate(cfg.asString("meta"), Files.readString(
				Path.of(cfg.asString("vql"))))
		
	}
}