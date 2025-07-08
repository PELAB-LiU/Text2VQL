package se.liu.ida.sas.pelab.text2vql.yamtl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.junit.jupiter.api.BeforeAll;

import yamtl.core.YAMTLModuleGroovy;

public class Util extends YAMTLModuleGroovy{
    
    public List<String> process(Object matches, String... queries){
        final List<String> data = new ArrayList<>();
        /*final List<String> relevants = Arrays.asList(queries);

        for(PatternMatch match : matches.getMatches()){
            if(relevants.isEmpty() || relevants.contains(match.getPattern().getName())){

                var builder = new StringBuilder();
                Matcher matcher = MatchSetEvaluator.regex_object.matcher(match.toString());
                while (matcher.find()) {
                    builder.append(matcher.group().replaceAll("DynamicEObjectImpl",""));
                }
                data.add(builder.toString());
            }   
        }*/

        return data;
    }
    
    /*protected EObject make(String eclass){
        return packageHelper.make(eclass);
    }
    protected EObject make(EObject parent, String relation, String eclass){
        return packageHelper.make(parent, relation, eclass);
    }
    protected <T> void link(EObject source, String relation, T value){
        packageHelper.link(source, relation, value);
    }*/

    protected Resource makeEmpty(){
        var resource = new ResourceImpl();
        return resource;
    }

    @BeforeAll
    static protected void configure(){
        //EPackage.Registry.INSTANCE.put(packageHelper.epackage.getNsURI(), packageHelper.epackage);
        EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
    }
    
}
