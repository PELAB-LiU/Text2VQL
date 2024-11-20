package se.liu.ida.sas.pelab.text2vql.vql.matchset

import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import java.util.ArrayList
import java.util.List
import java.util.Arrays
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import java.util.LinkedList
import java.util.Collections
import java.util.Objects
import java.util.stream.IntStream
import com.google.common.base.Stopwatch

class QueryUtility {
	static def parseA(String query) {
		var PatternParsingResults parsed
		try {
			parsed = PatternParserBuilder.instance.parse('''
				import "railway"
				import "http://www.eclipse.org/emf/2002/Ecore"
				
				pattern totalLength(length: java Integer){
				    cnt == count find segment(_);
				    check(cnt>0);
				    length == sum length(segment(_));
				}
				
			''')
		} catch (Exception e) {
			parsed = PatternParserBuilder.instance.parse('''
				import "railway"
				import "http://www.eclipse.org/emf/2002/Ecore"
				
				Fail.On.Parse.Exception.
				
			''')
		}

		if (parsed.hasError) {
			println("Input query contains errors.")
			println(query)
			parsed.errors.forEach[println(it)]
		}

		return parsed
	}

	static def parseB(String query) {
		val parsed = PatternParserBuilder.instance.parse('''
			import "railway"
			import "http://www.eclipse.org/emf/2002/Ecore"
			
			pattern bigRegion(region: Region){
			    Region.elements(region, segment);
			    Segment.length(segment, length);
			    sum == sum length;
			    check(sum>=50);
			} or {
			    Region.sensors(region, _);
			    cnt == count find Region.sensors(region, _);
			    check(cnt>=10);
			}
			
			
			
		''')

		if (parsed.hasError) {
			println("Input query contains errors.")
			println(query)
			parsed.errors.forEach[println(it)]
		}

		return parsed
	}

	static def parse(String query) {
		var PatternParsingResults parsed
		try {
			parsed = PatternParserBuilder.instance.parse('''
				import "railway"
				import "http://www.eclipse.org/emf/2002/Ecore"
				
				«query»

			''')
		} catch (Exception e) {
			parsed = PatternParserBuilder.instance.parse('''
				import "railway"
				import "http://www.eclipse.org/emf/2002/Ecore"
				
				Fail.On.Parse.Exception.
				
			''')
		}

		if (parsed.hasError) {
			println("Input query contains errors.")
			println(query)
			parsed.errors.forEach[println(it)]
		}

		return parsed
	}

	static def List<Object[]> matches(PatternParsingResults queries, String query, ViatraQueryEngine engine) {
		val matches = new LinkedList;
		queries.getQuerySpecification(query).ifPresent([ specification |
			val matcher = engine.getMatcher(specification)
			matches.addAll(matcher.allMatches)
		])
		return matches.map([asArray(it)])

	}

	static def boolean matchesEqual(PatternParsingResults queries, String query, ViatraQueryEngine engine,
		List<String> base) {
		val op = queries.getQuerySpecification(query)
		if (op.present) {
			val specification = op.get
			val matcher = engine.getMatcher(specification)
			val result = matcher.streamAllMatches.iterator.fold(true -> 0, [ status, match |
				var equal = status.key
				var count = status.value
				if (equal) {
					val array = Arrays.toString(asArray(match))
					val contained = Collections.binarySearch(base, array)
					equal = contained >= 0
					count++
				}
				equal -> count
			])
			return result.key && result.value === base.size;
		} else {
			return false
		}

	}

	static def asArray(IPatternMatch match) {
		val cnt = match.parameterNames.length
		val array = newArrayOfSize(cnt);
		for (i : 0 .. cnt - 1) {
			// array.set(i,System.identityHashCode(match.get(i)))
			array.set(i, match.get(i))
		}
		return array
	}

	static def common(List<Object[]> ls1, List<Object[]> ls2) {
		val common = newArrayList
		for (item1 : ls1) {
			for (item2 : ls2) {
				if (Arrays.equals(item1, item2)) {
					common.add(item1);
				}
			}
		}
		return common
	}

	static def minus(List<Object[]> ls1, List<Object[]> ls2) {
		val common = newArrayList
		for (item1 : ls1) {
			var contained = false
			for (item2 : ls2) {
				contained = contained || Arrays.equals(item1, item2)
			}
			if (!contained) {
				common.add(item1);
			}
		}
		return common
	}

	static def boolean isEqual(List<Object[]> ls1, List<Object[]> ls2) {
		if (ls1.size !== ls2.size)
			return false
		var commons = JavaQueryUtility.countCommon(ls1, ls2);
		if (commons !== ls1.size)
			return false
		return true

	}

	static def List<String> matchesSortedString(PatternParsingResults queries, String query, ViatraQueryEngine engine) {
		val matches = matches(queries, query, engine);
		val string = new ArrayList(matches.map([Arrays.toString(it)]))
		Collections.sort(string)
		return string

	}

	static volatile boolean error = false;

	static def boolean isEqualString(List<String> ls1, List<String> ls2) {
		if (ls1.size !== ls2.size)
			return false
		Collections.sort(ls1);
		Collections.sort(ls2);

		IntStream.range(0, ls1.size).parallel().forEach([ idx |
			if (!error) {
				val equal = ls1.get(idx).equals(ls2.get(idx)) // Objects.equals(ls1.get(idx), ls2.get(idx))
				if (!equal)
					error = true;
			}
		])
		return !error
	/*for(var i=0; i<ls1.size; i++){
	 * 	val equal = Objects.equals(ls1.get(i), ls2.get(i))
	 * 	if(! equal)
	 * 		return false
	 }*/
	/*
	 * var commons = countCommonOnSortedString(ls1, ls2);
	 * if(commons !== ls1.size)
	 return false*/
	// return true
	}

	static def int countCommonOnSortedString(List<String> ls1, List<String> ls2) {
		return ls1.fold(0, [ accu, entry |
			if (Collections.binarySearch(ls2, entry) >= 0)
				accu + 1
			else
				accu
		])
	}
}
