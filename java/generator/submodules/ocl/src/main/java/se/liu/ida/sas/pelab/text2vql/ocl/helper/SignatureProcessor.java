package se.liu.ida.sas.pelab.text2vql.ocl.helper;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.ocl.ecore.PrimitiveType;
import org.eclipse.ocl.ecore.BagType;
import org.eclipse.ocl.ecore.SetType;
import org.eclipse.ocl.ecore.TupleType;

public class SignatureProcessor {
    public String process(Object type){
        var builder = new StringBuilder();
        process(builder, type);
        return builder.toString();
    }
    public StringBuilder process(StringBuilder builder, Object type){
        switch (type) {
            case BagType bag:
                builder.append("Bag(");
                process(builder, bag.getElementType());
                builder.append(")");
                return builder;
            case SetType set:
                builder.append("Set(");
                process(builder, set.getElementType());
                builder.append(")");
                return builder;
            case TupleType tuple:
                builder.append("Tuple(");
                for(var feature : tuple.getEStructuralFeatures()){
                    builder.append(feature.getName());
                    builder.append(": ");
                    process(builder, feature.getEType());
                    builder.append(",");
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append(")");

                System.out.println(tuple.getEStructuralFeatures());
                //process(builder, bag.getElementType());
                //builder.append(")");
                return builder;
            case PrimitiveType primitive:
                builder.append(primitive.getName());
                return builder;
            case EClass clazz:
                builder.append(clazz.getName());
                return builder;

            default:
            System.out.println(type);
                break;
        }
        return builder;
    }
}
