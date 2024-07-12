package se.liu.ida.sas.pelab.querycomparison;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import se.liu.ida.sas.pelab.vqlsyntaxcheck.Config;

@SuppressWarnings("all")
public class Comparison {
  private final PatternParsingResults queries1;

  private final PatternParsingResults queries2;

  private final AdvancedViatraQueryEngine engine;

  private final ResourceSet resourceSet;

  public static void main(final String[] args) {
    final Config cfg = new Config(args);
    final ResourceSet rs = Comparison.setup();
    Comparison.loadMetamodel(cfg.asString("meta"), rs);
    final Comparison instance = new Comparison(cfg, rs);
    instance.compare(cfg.asString("query1"), cfg.asString("query2"));
  }

  public void compare(final String query1, final String query2) {
    boolean _isCompatible = this.isCompatible(query1, query2);
    boolean _not = (!_isCompatible);
    if (_not) {
      InputOutput.<String>println("Incompatible query header.");
      return;
    }
    final Function1<IPatternMatch, Object[]> _function = (IPatternMatch it) -> {
      return this.asArray(it);
    };
    final List<Object[]> matches1 = ListExtensions.<IPatternMatch, Object[]>map(this.matchesOf(this.queries1, query1), _function);
    final Function1<IPatternMatch, Object[]> _function_1 = (IPatternMatch it) -> {
      return this.asArray(it);
    };
    final List<Object[]> matches2 = ListExtensions.<IPatternMatch, Object[]>map(this.matchesOf(this.queries2, query2), _function_1);
    final ArrayList<Object[]> common = this.common(matches1, matches2);
    final ArrayList<Object[]> onlyQuery1 = this.minus(matches1, common);
    final ArrayList<Object[]> onlyQuery2 = this.minus(matches2, common);
    int _size = common.size();
    String _plus = ("|A ∩ B|=" + Integer.valueOf(_size));
    InputOutput.<String>println(_plus);
    int _size_1 = onlyQuery1.size();
    String _plus_1 = ("|A \\ B|=" + Integer.valueOf(_size_1));
    InputOutput.<String>println(_plus_1);
    int _size_2 = onlyQuery2.size();
    String _plus_2 = ("|B \\ A|=" + Integer.valueOf(_size_2));
    InputOutput.<String>println(_plus_2);
  }

  public ArrayList<Object[]> minus(final List<Object[]> ls1, final List<Object[]> ls2) {
    final ArrayList<Object[]> common = CollectionLiterals.<Object[]>newArrayList();
    for (final Object[] item1 : ls1) {
      {
        boolean contained = false;
        for (final Object[] item2 : ls2) {
          contained = (contained || Arrays.equals(item1, item2));
        }
        if ((!contained)) {
          common.add(item1);
        }
      }
    }
    return common;
  }

  public ArrayList<Object[]> common(final List<Object[]> ls1, final List<Object[]> ls2) {
    final ArrayList<Object[]> common = CollectionLiterals.<Object[]>newArrayList();
    for (final Object[] item1 : ls1) {
      for (final Object[] item2 : ls2) {
        boolean _equals = Arrays.equals(item1, item2);
        if (_equals) {
          common.add(item1);
        }
      }
    }
    return common;
  }

  public boolean isCompatible(final String query1, final String query2) {
    final IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> q1 = this.queries1.getQuerySpecification(query1).get();
    final IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> q2 = this.queries2.getQuerySpecification(query2).get();
    int _length = ((Object[])Conversions.unwrapArray(q1.getParameters(), Object.class)).length;
    int _length_1 = ((Object[])Conversions.unwrapArray(q2.getParameters(), Object.class)).length;
    boolean _tripleNotEquals = (_length != _length_1);
    if (_tripleNotEquals) {
      return false;
    }
    return true;
  }

  public Object[] asArray(final IPatternMatch match) {
    final int cnt = ((Object[])Conversions.unwrapArray(match.parameterNames(), Object.class)).length;
    final Object[] array = new Object[cnt];
    IntegerRange _upTo = new IntegerRange(0, (cnt - 1));
    for (final Integer i : _upTo) {
      array[(i).intValue()] = match.get((i).intValue());
    }
    return array;
  }

  public Comparison(final Config cfg, final ResourceSet resourceSet) {
    this.resourceSet = resourceSet;
    this.queries1 = Comparison.parseFile(cfg.asString("queries1"));
    this.queries2 = Comparison.parseFile(cfg.asString("queries2"));
    this.loadModel(cfg.asString("instance"));
    EMFScope _eMFScope = new EMFScope(resourceSet);
    ViatraQueryEngine _on = ViatraQueryEngine.on(_eMFScope);
    this.engine = ((AdvancedViatraQueryEngine) _on);
  }

  private static PatternParsingResults parseFile(final String path) {
    try {
      final PatternParsingResults parsed = PatternParserBuilder.instance().parse(
        Files.readString(
          Path.of(path)));
      boolean _hasError = parsed.hasError();
      boolean correct = (!_hasError);
      Iterable<Issue> errors = parsed.getErrors();
      if ((!correct)) {
        InputOutput.<String>println((("Input " + path) + " contains errors."));
        final Consumer<Issue> _function = (Issue it) -> {
          InputOutput.<Issue>println(it);
        };
        errors.forEach(_function);
        return null;
      }
      return parsed;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  private static Object loadMetamodel(final String path, final ResourceSet resourceSet) {
    Object _xblockexpression = null;
    {
      final Resource meta = resourceSet.getResource(
        URI.createFileURI(new File(path).getAbsolutePath()), true);
      EObject _get = meta.getContents().get(0);
      final EPackage pkg = ((EPackage) _get);
      _xblockexpression = EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
    }
    return _xblockexpression;
  }

  private static ResourceSet setup() {
    EMFPatternLanguageStandaloneSetup.doSetup();
    final EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules runtimeModule = new EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules();
    final Injector injector = Guice.createInjector(runtimeModule);
    final XtextResourceSet resourceSet = injector.<XtextResourceSet>getInstance(XtextResourceSet.class);
    ViatraQueryEngineOptions.setSystemDefaultBackends(ReteBackendFactory.INSTANCE, ReteBackendFactory.INSTANCE, 
      LocalSearchEMFBackendFactory.INSTANCE);
    EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
    Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    EcoreResourceFactoryImpl _ecoreResourceFactoryImpl = new EcoreResourceFactoryImpl();
    _extensionToFactoryMap.put(
      "ecore", _ecoreResourceFactoryImpl);
    return resourceSet;
  }

  private ArrayList<IPatternMatch> matchesOf(final PatternParsingResults queries, final String name) {
    final ArrayList<IPatternMatch> matches = new ArrayList<IPatternMatch>();
    final Consumer<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> _function = (IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification) -> {
      final ViatraQueryMatcher<? extends IPatternMatch> matcher = this.engine.getMatcher(specification);
      matches.addAll(matcher.getAllMatches());
    };
    queries.getQuerySpecification(name).ifPresent(_function);
    return matches;
  }

  private void loadModel(final String path) {
    final Resource modelResource = this.resourceSet.getResource(URI.createFileURI(path), true);
    final Consumer<Resource> _function = (Resource res) -> {
      InputOutput.<Resource>println(res);
    };
    this.resourceSet.getResources().forEach(_function);
  }
}
