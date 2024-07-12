package se.liu.ida.sas.pelab.querycomparison;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Pair;

@SuppressWarnings("all")
public class QueryUtility {
  public static PatternParsingResults parseA(final String query) {
    PatternParsingResults parsed = null;
    try {
      PatternParserBuilder _instance = PatternParserBuilder.instance();
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("import \"railway\"");
      _builder.newLine();
      _builder.append("import \"http://www.eclipse.org/emf/2002/Ecore\"");
      _builder.newLine();
      _builder.newLine();
      _builder.append("pattern totalLength(length: java Integer){");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("cnt == count find segment(_);");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("check(cnt>0);");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("length == sum length(segment(_));");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      parsed = _instance.parse(_builder.toString());
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        PatternParserBuilder _instance_1 = PatternParserBuilder.instance();
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("import \"railway\"");
        _builder_1.newLine();
        _builder_1.append("import \"http://www.eclipse.org/emf/2002/Ecore\"");
        _builder_1.newLine();
        _builder_1.newLine();
        _builder_1.append("Fail.On.Parse.Exception.");
        _builder_1.newLine();
        _builder_1.newLine();
        parsed = _instance_1.parse(_builder_1.toString());
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    boolean _hasError = parsed.hasError();
    if (_hasError) {
      InputOutput.<String>println("Input query contains errors.");
      InputOutput.<String>println(query);
      final Consumer<Issue> _function = (Issue it) -> {
        InputOutput.<Issue>println(it);
      };
      parsed.getErrors().forEach(_function);
    }
    return parsed;
  }

  public static PatternParsingResults parseB(final String query) {
    PatternParserBuilder _instance = PatternParserBuilder.instance();
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("import \"railway\"");
    _builder.newLine();
    _builder.append("import \"http://www.eclipse.org/emf/2002/Ecore\"");
    _builder.newLine();
    _builder.newLine();
    _builder.append("pattern bigRegion(region: Region){");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("Region.elements(region, segment);");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("Segment.length(segment, length);");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("sum == sum length;");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("check(sum>=50);");
    _builder.newLine();
    _builder.append("} or {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("Region.sensors(region, _);");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("cnt == count find Region.sensors(region, _);");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("check(cnt>=10);");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.newLine();
    _builder.newLine();
    final PatternParsingResults parsed = _instance.parse(_builder.toString());
    boolean _hasError = parsed.hasError();
    if (_hasError) {
      InputOutput.<String>println("Input query contains errors.");
      InputOutput.<String>println(query);
      final Consumer<Issue> _function = (Issue it) -> {
        InputOutput.<Issue>println(it);
      };
      parsed.getErrors().forEach(_function);
    }
    return parsed;
  }

  public static PatternParsingResults parse(final String query) {
    PatternParsingResults parsed = null;
    try {
      PatternParserBuilder _instance = PatternParserBuilder.instance();
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("import \"railway\"");
      _builder.newLine();
      _builder.append("import \"http://www.eclipse.org/emf/2002/Ecore\"");
      _builder.newLine();
      _builder.newLine();
      _builder.append(query);
      _builder.newLineIfNotEmpty();
      parsed = _instance.parse(_builder.toString());
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        PatternParserBuilder _instance_1 = PatternParserBuilder.instance();
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("import \"railway\"");
        _builder_1.newLine();
        _builder_1.append("import \"http://www.eclipse.org/emf/2002/Ecore\"");
        _builder_1.newLine();
        _builder_1.newLine();
        _builder_1.append("Fail.On.Parse.Exception.");
        _builder_1.newLine();
        _builder_1.newLine();
        parsed = _instance_1.parse(_builder_1.toString());
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    boolean _hasError = parsed.hasError();
    if (_hasError) {
      InputOutput.<String>println("Input query contains errors.");
      InputOutput.<String>println(query);
      final Consumer<Issue> _function = (Issue it) -> {
        InputOutput.<Issue>println(it);
      };
      parsed.getErrors().forEach(_function);
    }
    return parsed;
  }

  public static List<Object[]> matches(final PatternParsingResults queries, final String query, final ViatraQueryEngine engine) {
    final LinkedList<IPatternMatch> matches = new LinkedList<IPatternMatch>();
    final Consumer<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> _function = (IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification) -> {
      final ViatraQueryMatcher<? extends IPatternMatch> matcher = engine.getMatcher(specification);
      matches.addAll(matcher.getAllMatches());
    };
    queries.getQuerySpecification(query).ifPresent(_function);
    final Function1<IPatternMatch, Object[]> _function_1 = (IPatternMatch it) -> {
      return QueryUtility.asArray(it);
    };
    return ListExtensions.<IPatternMatch, Object[]>map(matches, _function_1);
  }

  public static boolean matchesEqual(final PatternParsingResults queries, final String query, final ViatraQueryEngine engine, final List<String> base) {
    final Optional<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> op = queries.getQuerySpecification(query);
    boolean _isPresent = op.isPresent();
    if (_isPresent) {
      final IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification = op.get();
      final ViatraQueryMatcher<? extends IPatternMatch> matcher = engine.getMatcher(specification);
      Iterator<? extends IPatternMatch> _iterator = matcher.streamAllMatches().iterator();
      Pair<Boolean, Integer> _mappedTo = Pair.<Boolean, Integer>of(Boolean.valueOf(true), Integer.valueOf(0));
      final Function2<Pair<Boolean, Integer>, IPatternMatch, Pair<Boolean, Integer>> _function = (Pair<Boolean, Integer> status, IPatternMatch match) -> {
        Pair<Boolean, Integer> _xblockexpression = null;
        {
          Boolean equal = status.getKey();
          Integer count = status.getValue();
          if ((equal).booleanValue()) {
            final String array = Arrays.toString(QueryUtility.asArray(match));
            final int contained = Collections.<String>binarySearch(base, array);
            equal = Boolean.valueOf((contained >= 0));
            count++;
          }
          _xblockexpression = Pair.<Boolean, Integer>of(equal, count);
        }
        return _xblockexpression;
      };
      final Pair<Boolean, Integer> result = IteratorExtensions.fold(_iterator, _mappedTo, _function);
      return ((result.getKey()).booleanValue() && ((result.getValue()).intValue() == base.size()));
    } else {
      return false;
    }
  }

  public static Object[] asArray(final IPatternMatch match) {
    final int cnt = ((Object[])Conversions.unwrapArray(match.parameterNames(), Object.class)).length;
    final Object[] array = new Object[cnt];
    IntegerRange _upTo = new IntegerRange(0, (cnt - 1));
    for (final Integer i : _upTo) {
      array[(i).intValue()] = match.get((i).intValue());
    }
    return array;
  }

  public static ArrayList<Object[]> common(final List<Object[]> ls1, final List<Object[]> ls2) {
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

  public static ArrayList<Object[]> minus(final List<Object[]> ls1, final List<Object[]> ls2) {
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

  public static boolean isEqual(final List<Object[]> ls1, final List<Object[]> ls2) {
    int _size = ls1.size();
    int _size_1 = ls2.size();
    boolean _tripleNotEquals = (_size != _size_1);
    if (_tripleNotEquals) {
      return false;
    }
    int commons = JavaQueryUtility.countCommon(ls1, ls2);
    int _size_2 = ls1.size();
    boolean _tripleNotEquals_1 = (commons != _size_2);
    if (_tripleNotEquals_1) {
      return false;
    }
    return true;
  }

  public static List<String> matchesSortedString(final PatternParsingResults queries, final String query, final ViatraQueryEngine engine) {
    final List<Object[]> matches = QueryUtility.matches(queries, query, engine);
    final Function1<Object[], String> _function = (Object[] it) -> {
      return Arrays.toString(it);
    };
    List<String> _map = ListExtensions.<Object[], String>map(matches, _function);
    final ArrayList<String> string = new ArrayList<String>(_map);
    Collections.<String>sort(string);
    return string;
  }

  private static volatile boolean error = false;

  public static boolean isEqualString(final List<String> ls1, final List<String> ls2) {
    int _size = ls1.size();
    int _size_1 = ls2.size();
    boolean _tripleNotEquals = (_size != _size_1);
    if (_tripleNotEquals) {
      return false;
    }
    Collections.<String>sort(ls1);
    Collections.<String>sort(ls2);
    final IntConsumer _function = (int idx) -> {
      if ((!QueryUtility.error)) {
        final boolean equal = ls1.get(idx).equals(ls2.get(idx));
        if ((!equal)) {
          QueryUtility.error = true;
        }
      }
    };
    IntStream.range(0, ls1.size()).parallel().forEach(_function);
    return (!QueryUtility.error);
  }

  public static int countCommonOnSortedString(final List<String> ls1, final List<String> ls2) {
    final Function2<Integer, String, Integer> _function = (Integer accu, String entry) -> {
      Integer _xifexpression = null;
      int _binarySearch = Collections.<String>binarySearch(ls2, entry);
      boolean _greaterEqualsThan = (_binarySearch >= 0);
      if (_greaterEqualsThan) {
        _xifexpression = Integer.valueOf(((accu).intValue() + 1));
      } else {
        _xifexpression = accu;
      }
      return _xifexpression;
    };
    return (int) IterableExtensions.<String, Integer>fold(ls1, Integer.valueOf(0), _function);
  }
}
