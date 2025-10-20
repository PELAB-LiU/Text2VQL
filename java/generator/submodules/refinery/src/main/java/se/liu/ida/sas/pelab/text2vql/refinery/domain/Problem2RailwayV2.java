package se.liu.ida.sas.pelab.text2vql.refinery.domain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import se.liu.ida.sas.pelab.text2vql.refinery.util.Text2VQLProjectStructure;
import se.liu.ida.sas.pelab.text2vql.utilities.ResourcesHelper;
import tools.refinery.generator.ModelGenerator;
import tools.refinery.logic.term.truthvalue.TruthValue;
import tools.refinery.store.map.Cursor;
import tools.refinery.store.tuple.Tuple;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Problem2RailwayV2 extends Problem2Emf{
	//private ProblemTrace trace;
	private final ResourceSet resourceSet;
	
	private Random random;
	public Problem2RailwayV2() {
		super(getEPackage(Text2VQLProjectStructure.find("dataset_construction/test_metamodel/railway.ecore")));
		resourceSet = epackage.eResource().getResourceSet();
		
		//EPackage.Registry.INSTANCE.put(epackage.getNsURI(), epackage);
	}
	public void configureMapping(ModelGenerator generator){
		this.random = new Random(generator.getRandomSeed());
	}
	public void configureEnum(ModelGenerator generator){
		put(generator.getProblemTrace().getNodeId("Boolean::TRUE"), true);
		put(generator.getProblemTrace().getNodeId("Boolean::FALSE"), false);
	}

	public Object getAttributeValue(ModelGenerator generator, Tuple relation, EAttribute attribute){
        if(attribute.getName().equals("length")){
			return makeIntegers(generator, relation);
		}
		return super.getAttributeValue(generator, relation, attribute);
    }


	private int makeIntegers(ModelGenerator generator, Tuple relation){	
		if(generator.getProblemTrace().getNodeId("Integer::POSITIVE")==relation.get(1)){
			return random.nextInt(1, 100);
		}
		if(generator.getProblemTrace().getNodeId("Integer::NEGATIVE")==relation.get(1)){
			return random.nextInt(-10, 0);
		}
		if(generator.getProblemTrace().getNodeId("Integer::ZERO")==relation.get(1)){
			return 0;
		}
		throw new RuntimeException("Unknown integer type.");
	}


	public EObject toEMF(ModelGenerator generator){
		clear();
		super.map(generator);

		var cursor = generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation(
				"RailwayContainer")).getAll();
		cursor.move();
		return (EObject) get(cursor.getKey().get(0));
	}

	public void save(EObject root, File dir, String fileName) throws IOException {
		var res = resourceSet.createResource(URI.createURI(new File(dir, fileName).toURI().toString()));
		res.getContents().add(root);
		res.save(Collections.EMPTY_MAP);

	}

	public void attributeNotFoundHandler(ModelGenerator generator, EAttribute attribute){
		if("RailwayElement::id".equals(fqn(attribute))) return;
		super.attributeNotFoundHandler(generator, attribute);;
	}
	public void referenceNotFoundHandler(ModelGenerator generator, EReference reference){
		super.referenceNotFoundHandler(generator, reference);
	}
}
