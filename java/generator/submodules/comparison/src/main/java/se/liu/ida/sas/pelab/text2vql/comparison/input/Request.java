package se.liu.ida.sas.pelab.text2vql.comparison.input;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public record Request(String wd, Configuration[] configurations, TestCase[] tests) {
    public Stream<TestCase> of(String domain){
        return Arrays.stream(tests).filter(test -> Objects.equals(test.domain(), domain));
    }
    public Set<String> domains(){
        Set<String> definedDomains = new HashSet<>();
        for(Configuration c : configurations){
            definedDomains.add(c.domain());
        }
        return definedDomains;
    }
}
