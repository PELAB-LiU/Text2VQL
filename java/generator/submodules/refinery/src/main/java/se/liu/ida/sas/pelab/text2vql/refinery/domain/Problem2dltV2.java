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

public class Problem2dltV2 extends Problem2Emf{
	//private ProblemTrace trace;
	private final ResourceSet resourceSet;
	
	public Problem2dltV2() {
		super(getEPackage(Text2VQLProjectStructure.find("dataset_construction/test_metamodel/dlt.ecore")));
		resourceSet = epackage.eResource().getResourceSet();
		
		//EPackage.Registry.INSTANCE.put(epackage.getNsURI(), epackage);
	}

	public List<EObject> toEMF(ModelGenerator generator){
		clear();
		super.map(generator);

		var cursor = generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation(
				"FabricNetwork")).getAll();
		List<EObject> rootContent = new ArrayList<>();
		while(cursor.move()){
			rootContent.add((EObject) get(cursor.getKey().get(0)));
			//result.getContents().add((EObject) trace.get(cursor.getKey().get(0)));
		};
		return rootContent;
	}

	public void save(List<EObject> root, File dir, String fileName) throws IOException {
		var res = resourceSet.createResource(URI.createURI(new File(dir, fileName).toURI().toString()));
		//root.setURI(URI.createURI(new File(dir, fileName).toURI().toString()));
		res.getContents().addAll(root);
		res.save(Collections.EMPTY_MAP);
	}
}
