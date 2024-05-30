package se.liu.ida.sas.pelab.vqlsyntaxcheck;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class Evaluate {
  public static boolean evaluate(final String path, final String vql) {
    final ResourceSet resourceSet = new ResourceSetImpl();
    EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
    Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    EcoreResourceFactoryImpl _ecoreResourceFactoryImpl = new EcoreResourceFactoryImpl();
    _extensionToFactoryMap.put(
      "ecore", _ecoreResourceFactoryImpl);
    final Resource resource = resourceSet.getResource(
      URI.createFileURI(new File(path).getAbsolutePath()), true);
    EObject _get = resource.getContents().get(0);
    final EPackage pkg = ((EPackage) _get);
    EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
    EMFPatternLanguageStandaloneSetup.doSetup();
    final PatternParsingResults parsed = PatternParserBuilder.instance().parse(vql);
    boolean correct = true;
    Iterable<Issue> errors = null;
    boolean _hasError = parsed.hasError();
    if (_hasError) {
      final Function1<Issue, Boolean> _function = (Issue it) -> {
        boolean _startsWith = it.getMessage().startsWith("Cannot find type for reference ");
        return Boolean.valueOf((!_startsWith));
      };
      errors = IterableExtensions.<Issue>filter(parsed.getErrors(), _function);
      correct = IterableExtensions.isEmpty(errors);
    }
    if (correct) {
      return true;
    } else {
      return false;
    }
  }
}