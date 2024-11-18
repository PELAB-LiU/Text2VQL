package se.liu.ida.sas.pelab.text2vql.testing.models;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import se.liu.ida.sas.pelab.text2vql.ResourcesHelper;
import tools.refinery.generator.ModelGenerator;
import tools.refinery.logic.term.truthvalue.TruthValue;
import tools.refinery.store.map.Cursor;
import tools.refinery.store.tuple.Tuple;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class Problem2Railway {
	//private ProblemTrace trace;
	private final EPackage railway;
	private final EFactory factory;
	private final ResourceSet resourceSet;
	private Map<Integer,Object> trace = new HashMap();
	public Problem2Railway() {

		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				"ecore", new EcoreResourceFactoryImpl());

		Resource meta = resourceSet.getResource(ResourcesHelper.emfURI("railway/railway.ecore"), true);

		railway = (EPackage) meta.getContents().get(0);
		factory = railway.getEFactoryInstance();
		EPackage.Registry.INSTANCE.put(railway.getNsURI(), railway);
	}
	public EObject toEMF(ModelGenerator generator){
		trace.clear();
		makeEnumTrace(generator);


		makeNormalClasses(generator, "RailwayContainer");
		makeNormalClasses(generator, "Region");
		makeNormalClasses(generator, "Semaphore");
		makeNormalClasses(generator, "Route");
		makeNormalClasses(generator, "SwitchPosition");
		makeNormalClasses(generator, "Segment");
		makeNormalClasses(generator, "Switch");
		makeNormalClasses(generator, "Sensor");

		//makeNormalClasses(generator, "Integer");

		makeNormalRelations(generator, "routes","RailwayContainer");
		makeNormalRelations(generator, "regions","RailwayContainer");
		makeNormalRelations(generator, "requires","Route");
		makeNormalRelations(generator, "sensors","Region");
		makeNormalRelations(generator, "monitors","Sensor");
		makeNormalRelations(generator, "monitoredBy","TrackElement");
		makeNormalRelations(generator, "connectsTo", "TrackElement");
		makeNormalRelations(generator, "elements","Region");
		makeNormalRelations(generator, "route","SwitchPosition");
		makeNormalRelations(generator, "follows","Route");
		makeNormalRelations(generator, "positions","Switch");
		makeNormalRelations(generator, "target","SwitchPosition");
		makeNormalRelations(generator, "semaphores","Segment");
		makeNormalRelations(generator, "entry","Route");
		makeNormalRelations(generator, "exit","Route");

		makeNormalRelations(generator, "signal","Semaphore");
		makeNormalRelations(generator, "active","Route");
		makeNormalRelations(generator, "position","SwitchPosition");
		makeNormalRelations(generator, "currentPosition","Switch");

		makeIntegers(generator, "length",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("Segment::length")).getAll(),
				new Random(generator.getRandomSeed()));

		var cursor = generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation(
				"RailwayContainer")).getAll();
		cursor.move();
		return (EObject) trace.get(cursor.getKey().get(0));
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
	private void makeEnumTrace(ModelGenerator generator){
		var signal = (EEnum) railway.getEClassifier("Signal");
		trace.put(generator.getProblemTrace().getNodeId("Signal::FAILURE"), signal.getEEnumLiteral("FAILURE"));
		trace.put(generator.getProblemTrace().getNodeId("Signal::GO"), signal.getEEnumLiteral("GO"));
		trace.put(generator.getProblemTrace().getNodeId("Signal::STOP"), signal.getEEnumLiteral("STOP"));

		var position = (EEnum) railway.getEClassifier("Position");
		trace.put(generator.getProblemTrace().getNodeId("Position::FAILURE"), position.getEEnumLiteral("FAILURE"));
		trace.put(generator.getProblemTrace().getNodeId("Position::STRAIGHT"), position.getEEnumLiteral("STRAIGHT"));
		trace.put(generator.getProblemTrace().getNodeId("Position::DIVERGING"), position.getEEnumLiteral("DIVERGING"));

		trace.put(generator.getProblemTrace().getNodeId("Boolean::TRUE"), true);
		trace.put(generator.getProblemTrace().getNodeId("Boolean::FALSE"), false);
	}
	private void makeIntegers(ModelGenerator generator, String name, Cursor<Tuple, TruthValue> cursor, Random random){
		while (cursor.move()){
			var hostId = cursor.getKey().get(0);
			var host = (EObject) trace.get(hostId);
			var feature = ((EObject) trace.get(hostId)).eClass().getEStructuralFeature(name);

			if(generator.getProblemTrace().getNodeId("Integer::POSITIVE")==cursor.getKey().get(1)){
				host.eSet(feature, random.nextInt(1, 100) );
			}
			if(generator.getProblemTrace().getNodeId("Integer::NEGATIVE")==cursor.getKey().get(1)){
				host.eSet(feature, random.nextInt(-10, 0) );
			}
			if(generator.getProblemTrace().getNodeId("Integer::ZERO")==cursor.getKey().get(1)){
				host.eSet(feature, 0);
			}

			System.out.println("\tFeature set: "+hostId+ "(image: "+trace.get(hostId).hashCode()
					+") ---["+feature.getName()+"]---> "
					+host.eGet(feature));
		}
	}
	public void save(EObject root, String path) throws IOException {
		var res = resourceSet.createResource(URI.createURI(path));
		res.getContents().add(root);
		res.save(Collections.EMPTY_MAP);

	}

}
