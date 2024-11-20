package se.liu.ida.sas.pelab.text2vql.refinery.transformation;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguageFactory;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import tools.refinery.generator.ModelGenerator;
import tools.refinery.logic.term.truthvalue.TruthValue;
import tools.refinery.store.map.Cursor;
import tools.refinery.store.tuple.Tuple;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Problem2VQL {
    //private ProblemTrace trace;
    private final BiMap<String, String> namemap = HashBiMap.create();
    private final PatternLanguagePackage vql;
    private final PatternLanguageFactory factory;
    private final ResourceSet resourceSet;
    private Map<Integer,Object> trace = new HashMap<>();
    public Problem2VQL() throws URISyntaxException {
        vql = PatternLanguagePackage.eINSTANCE;
        factory = PatternLanguageFactory.eINSTANCE;
        EPackage.Registry.INSTANCE.put(PatternLanguagePackage.eNS_URI, PatternLanguagePackage.eINSTANCE);

        resourceSet = new ResourceSetImpl();
        //resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
        //        "ecore", new EcoreResourceFactoryImpl());
        //model = factory.createPatternModel();
    }
    public PatternModel toPatternModel(ModelGenerator generator){
        trace.clear();
        makeEnumTrace(generator, "Direction", "IN", "OUT", "INOUT");
        makeEnumTrace(generator, "CompareFeature", "EQ", "NEQ");


        /**
         * Structure
         */
        makeNormalClasses(generator, "PatternModel");
        makeNormalClasses(generator, "Pattern");
        makeNormalClasses(generator, "PatternBody");
        /**
         * Types
         */
        /**
         * Constraints
         */
        makeNormalClasses(generator, "CompareConstraint");
        makeNormalClasses(generator, "CheckConstraint");
        makeNormalClasses(generator, "PatternCompositionConstraint");
        makeNormalClasses(generator, "PathExpressionConstraint");
        makeNormalClasses(generator, "TypeCheckConstraint");
        makeNormalClasses(generator, "ClassifierConstraint");
        /**
         * Expressions
         */
        makeNormalClasses(generator, "Parameter");
        makeNormalClasses(generator, "LocalVariable");
        makeNormalClasses(generator, "EnumValue");
        makeNormalClasses(generator, "VariableReference");
        makeNormalClasses(generator, "FunctionValue");
        makeNormalClasses(generator, "AggregatedValue");
        makeNormalClasses(generator, "StringLiteral");
        makeNormalClasses(generator, "NumberLiteral");
        makeNormalClasses(generator, "BooleanLiteral");
        makeNormalClasses(generator, "ListValue");
        /**
         * Other
         */
        makeNormalClasses(generator, "PatternCall");


        makeNormalRelations(generator, "patterns","PatternModel");
        makeNormalRelations(generator, "parameters","Pattern");
        makeNormalRelations(generator, "bodies","Pattern");
        makeNormalRelations(generator, "variables","PatternBody");
        makeNormalRelations(generator, "constraints","PatternBody");
        /**
         * Constraints
         */
        makeNormalRelations(generator, "equality","CompareConstraint");
        makeNormalRelations(generator, "rhs","CompareConstraint");
        makeNormalRelations(generator, "lhs","CompareConstraint");
        makeNormalRelations(generator, "expression","CheckConstraint");
        makeNormalRelations(generator, "negative","PatternCompositionConstraint");
        makeNormalRelations(generator, "call", "PatternCompositionConstraint");
        makeNormalRelations(generator, "sourceType","PathExpressionConstraint");
        makeNormalRelations(generator, "edge","PathExpressionConstraint");
        makeNormalRelations(generator, "type","TypeCheckConstraint");
        makeNormalRelations(generator, "type","ClassifierConstraint");
        /**
         * Expressions
         */
        makeNormalRelations(generator, "name","Variable");
        makeNormalRelations(generator, "direction","Parameter");
        makeNormalRelations(generator, "referredParameter","ParameterRef");
        makeNormalRelations(generator, "variable","VariableReference");
        makeNormalRelations(generator, "expression","FunctionValue");
        makeNormalRelations(generator, "aggregator","AggregatedValue");
        makeNormalRelations(generator, "call","AggregatedValue");
        /**
         * Other
         */
        makeNormalRelations(generator, "patternRef","PatternCall");

        /*makeIntegers(generator, "length",
                generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("Segment::length")).getAll(),
                new Random(generator.getRandomSeed()));

        var cursor = generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation(
                "RailwayContainer")).getAll();
        cursor.move();
        return (EObject) trace.get(cursor.getKey().get(0));*/
        return null;
    }
    private void makeEnumTrace(ModelGenerator generator, String name, String... literals){
        var enum_ = (EEnum) vql.getEClassifier(name);
        for(String literal : literals){
            trace.put(generator.getProblemTrace().getNodeId(name+"::"+literal), enum_.getEEnumLiteral(literal));
        }
    }
    private void makeNormalClasses(ModelGenerator generator, String name){
        System.out.println("Mapping class: "+name);
        mapClasses(name, generator.getPartialInterpretation(
                generator.getProblemTrace().getPartialRelation(name)).getAll());
    }
    private void mapClasses(String name, Cursor<Tuple, TruthValue> cursor){
        while (cursor.move()){
            var id = cursor.getKey().get(0);
            try{
                var image = factory.create((EClass) vql.getEClassifier(name));
                trace.put(id,image);
                System.out.println("\tInstance added: "+id+ " (image: " +image.hashCode()+ ")");
            } catch (NullPointerException | ClassCastException e){}
        }
    }

    private void makeNormalRelations(ModelGenerator generator, String name, String host){
        System.out.println("Mapping relation: "+name);
        mapRelations(name, generator.getPartialInterpretation(
                generator.getProblemTrace().getPartialRelation(host+"::"+name)).getAll());
    }
    private void mapRelations(String name, Cursor<Tuple, TruthValue> cursor){
        while (cursor.move()){
            var hostId = cursor.getKey().get(0);
            var targetId = cursor.getKey().get(1);
            var feature = ((EObject) trace.get(hostId)).eClass().getEStructuralFeature(name);

            if(feature.isMany()){
                var list = (EList) ((EObject) trace.get(hostId)).eGet(feature);
                list.add(trace.get(targetId));
                System.out.println("\tInserted to feature: "+hostId+ "(image: "+trace.get(hostId).hashCode()
                        +") ---["+feature.getName()+"]---> "
                        +targetId+"(image:"+trace.get(targetId).hashCode()+")");
            } else {
                ((EObject) trace.get(hostId)).eSet(feature, trace.get(targetId));
                System.out.println("\tFeature set: "+hostId+ "(image: "+trace.get(hostId).hashCode()
                        +") ---["+feature.getName()+"]---> "
                        +targetId+"(image:"+trace.get(targetId).hashCode()+")");
            }
        }
    }
}
