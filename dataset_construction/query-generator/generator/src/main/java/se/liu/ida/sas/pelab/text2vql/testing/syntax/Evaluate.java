package se.liu.ida.sas.pelab.text2vql.testing.syntax;

import java.io.File;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class Evaluate {
    public static boolean evaluate(String path, String vql) {
        ResourceSet resourceSet = new ResourceSetImpl();
        EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);

        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());

        Resource resource = resourceSet.getResource(
                URI.createFileURI(new File(path).getAbsolutePath()), true);

        EPackage pkg = ((EPackage) resource.getContents().get(0));
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        EMFPatternLanguageStandaloneSetup.doSetup();
        PatternParsingResults parsed = PatternParserBuilder.instance().parse(vql);

        if (parsed.hasError()) {
            return true;
        } else {
            parsed.getErrors().forEach(System.out::println);
            return false;
        }
    }
}
