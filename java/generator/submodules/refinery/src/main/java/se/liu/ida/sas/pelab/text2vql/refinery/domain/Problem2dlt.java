package se.liu.ida.sas.pelab.text2vql.refinery.domain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
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

public class Problem2dlt {
	//private ProblemTrace trace;
	private final EPackage railway;
	private final EFactory factory;
	private final ResourceSet resourceSet;
	private Map<Integer,Object> trace = new HashMap();
	public Problem2dlt() {

		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				"ecore", new EcoreResourceFactoryImpl());

		File metamodel = Text2VQLProjectStructure.find("dataset_construction/test_metamodel/dlt.ecore");
		Resource meta = resourceSet.getResource(ResourcesHelper.emfURI(metamodel), true);

		railway = (EPackage) meta.getContents().get(0);
		factory = railway.getEFactoryInstance();
		EPackage.Registry.INSTANCE.put(railway.getNsURI(), railway);
	}
	public List<EObject> toEMF(ModelGenerator generator){
		trace.clear();

		makeNormalClasses(generator, "KafkaFabricNetwork");
		makeNormalClasses(generator, "RaftFabricNetwork");
		makeNormalClasses(generator, "Organization");
		makeNormalClasses(generator, "Host");
		makeNormalClasses(generator, "OrderingNode");
		makeNormalClasses(generator, "EndorsingNode");
		makeNormalClasses(generator, "Channel");
		makeNormalClasses(generator, "ChaincodeInstance");

		makeNormalRelations(generator, "organizations","FabricNetwork");
		makeNormalRelations(generator, "channels","FabricNetwork");
		makeNormalRelations(generator, "hosts","Organization");
		makeNormalRelations(generator, "nodes","Host");
		makeNormalRelations(generator, "orders","OrderingNode");
		makeNormalRelations(generator, "endorses","EndorsingNode");
		makeNormalRelations(generator, "chaincodes", "Channel");
		makeNormalRelations(generator, "orderedBy","Channel");
		makeNormalRelations(generator, "endorsedBy","ChaincodeInstance");

		var cursor = generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation(
				"FabricNetwork")).getAll();
		
		//Resource result = resourceSet.createResource(URI.createURI(""));// new ResourceImpl();
		List<EObject> rootContent = new ArrayList<>();
		while(cursor.move()){
			rootContent.add((EObject) trace.get(cursor.getKey().get(0)));
			//result.getContents().add((EObject) trace.get(cursor.getKey().get(0)));
		};
		return rootContent;
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
				var image = factory.create((EClass) railway.getEClassifier(name));
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
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

	public void save(List<EObject> root, File dir, String fileName) throws IOException {
		var res = resourceSet.createResource(URI.createURI(new File(dir, fileName).toURI().toString()));
		//root.setURI(URI.createURI(new File(dir, fileName).toURI().toString()));
		res.getContents().addAll(root);
		res.save(Collections.EMPTY_MAP);
	}
}
