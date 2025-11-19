package se.liu.ida.sas.pelab.text2vql.comparison.input;

import java.util.HashSet;
import java.util.Set;

public record Request(String wd, Configuration[] configurations, TestCase[] tests) {
    public Set<String> domains(){
        Set<String> definedDomains = new HashSet<>();
        for(Configuration c : configurations){
            definedDomains.add(c.domain());
        }
        return definedDomains;
    }
}
