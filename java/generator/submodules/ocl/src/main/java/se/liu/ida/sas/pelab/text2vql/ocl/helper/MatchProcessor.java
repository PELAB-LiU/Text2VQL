package se.liu.ida.sas.pelab.text2vql.ocl.helper;

import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ocl.Query;
import org.eclipse.ocl.util.Bag;
import org.eclipse.ocl.util.Tuple;
import se.liu.ida.sas.pelab.text2vql.ocl.ContainerException;
import se.liu.ida.sas.pelab.text2vql.ocl.DataException;
import se.liu.ida.sas.pelab.text2vql.utilities.evaluation.MatchSetEvaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchProcessor {
    private final Pattern regex;
    public MatchProcessor(String regex){
        this.regex = Pattern.compile(regex);
    }
    public MatchProcessor(Pattern regex){
        this.regex = regex;
    }

    public List<String> processContainer(Object container){
        final List<String> data = new ArrayList<>();
        if(container instanceof Bag<?> bag){
            System.out.println("#Bag: "+bag.size());
            bag.forEach(t -> {
                String result = processData(t);
                data.add(result);
            });
            return data;
        } else if (container instanceof Set<?> set) {
            System.out.println("#Set: "+set.size());
            set.forEach(t -> {
                String result = processData(t);
                data.add(result);
            });
            return data;
        } else {
            throw new ContainerException(container);
        }
    }
    public String processData(Object data){
        if(data instanceof Tuple<?,?> tuple){
            StringBuilder builder = new StringBuilder();

            Matcher m = regex.matcher(tuple.toString());
            while (m.find()) {
                builder.append(m.group().replaceAll("DynamicEObjectImpl",""));
            }
            System.out.println("\t"+builder.toString());
            return builder.toString();
        } else if (data instanceof DynamicEObjectImpl eobject){
            StringBuilder builder = new StringBuilder();
            Matcher m = regex.matcher(eobject.toString());
            while (m.find()) {
                builder.append(m.group().replaceAll("DynamicEObjectImpl",""));
            }
            System.out.println("\t"+builder.toString());
            return builder.toString();
        } else if (data instanceof Boolean bool){
            System.out.println("\t"+bool.toString());
            return bool.toString();
        }{
            throw new DataException(data);
        }
    }
}
