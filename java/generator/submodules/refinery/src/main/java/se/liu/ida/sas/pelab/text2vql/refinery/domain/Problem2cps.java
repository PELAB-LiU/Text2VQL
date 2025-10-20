package se.liu.ida.sas.pelab.text2vql.refinery.domain;

import org.apache.commons.text.RandomStringGenerator;
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

public class Problem2cps {
	//private ProblemTrace trace;
	private final EPackage railway;
	private final EFactory factory;
	private final ResourceSet resourceSet;
	private Map<Integer,Object> trace = new HashMap<>();
	public Problem2cps() {

		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				"ecore", new EcoreResourceFactoryImpl());

		File metamodel = Text2VQLProjectStructure.find("dataset_construction/test_metamodel/cps.ecore");
		Resource meta = resourceSet.getResource(ResourcesHelper.emfURI(metamodel), true);

		railway = (EPackage) meta.getContents().get(0);
		factory = railway.getEFactoryInstance();
		EPackage.Registry.INSTANCE.put(railway.getNsURI(), railway);
	}
	public EObject toEMF(ModelGenerator generator){
		trace.clear();
		makeEnumTrace(generator);


		makeNormalClasses(generator, "CyberPhysicalSystem");
		makeNormalClasses(generator, "Request");
		makeNormalClasses(generator, "Requirement");
		makeNormalClasses(generator, "ApplicationType");
		makeNormalClasses(generator, "ApplicationInstance");
		makeNormalClasses(generator, "ResourceRequirement");
		makeNormalClasses(generator, "HostType");
		makeNormalClasses(generator, "HostInstance");
		makeNormalClasses(generator, "StateMachine");
		makeNormalClasses(generator, "State");
		makeNormalClasses(generator, "Transition");

		//makeNormalClasses(generator, "Integer");

		makeNormalRelations(generator, "requests","CyberPhysicalSystem");
		makeNormalRelations(generator, "appType","CyberPhysicalSystem");
		makeNormalRelations(generator, "hostTypes","CyberPhysicalSystem");
		makeNormalRelations(generator, "requirements","Request");
		makeNormalRelations(generator, "type","Requirement");
		makeNormalRelations(generator, "request","Requirement");
		makeNormalRelations(generator, "mandatory", "Requirement");
		makeNormalRelations(generator, "cps","ApplicationType");
		makeNormalRelations(generator, "behaviour","ApplicationType");
		makeNormalRelations(generator, "requirements","ApplicationType");
		makeNormalRelations(generator, "instances","ApplicationType");	
		makeNormalRelations(generator, "type","ApplicationInstance");
		makeNormalRelations(generator, "dependOn","ApplicationInstance");
		makeNormalRelations(generator, "allocatedTo","ApplicationInstance");
		makeNormalRelations(generator, "state","ApplicationInstance");
		makeNormalRelations(generator, "type","ResourceRequirement");
		makeNormalRelations(generator, "applications","HostInstance");
		makeNormalRelations(generator, "communicatesWith","HostInstance");
		makeNormalRelations(generator, "initial","StateMachine");
		makeNormalRelations(generator, "states","StateMachine");
		makeNormalRelations(generator, "outgoingTransitions","State");
		makeNormalRelations(generator, "targetState","Transition");


		Random random = new Random(generator.getRandomSeed());
		makeStrings(generator, "id", 
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("Identifiable::id")).getAll(),
				random);
		makeStrings(generator, "dbURL", 
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("CyberPhysicalSystem::dbURL")).getAll(),
				random);
		makeIntegers(generator, "count",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("Requirement::count")).getAll(),
				random);
		makeIntegers(generator, "availablePorts",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("Requirement::availablePorts")).getAll(),
				random);
		makeStrings(generator, "exeFileLocation", 
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("ApplicationType::exeFileLocation")).getAll(),
				random);
		makeStrings(generator, "exeType", 
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("ApplicationType::exeType")).getAll(),
				random);
		makeStrings(generator, "zipFileUrl", 
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("ApplicationType::zipFileUrl")).getAll(),
				random);
		makeIntegers(generator, "exeFileSize",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("ApplicationType::exeFileSize")).getAll(),
				random);
		makeStrings(generator, "dbUser", 
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("ApplicationInstance::dbUser")).getAll(),
				random);
		makeStrings(generator, "dbPassword", 
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("ApplicationInstance::dbPassword")).getAll(),
				random);
		makeIntegers(generator, "priority",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("ApplicationInstance::priority")).getAll(),
				random);
		makeIntegers(generator, "defaultCpu",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("ResourceRequirement::defaultCpu")).getAll(),
				random);
		makeIntegers(generator, "defaultRam",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("ResourceRequirement::defaultRam")).getAll(),
				random);
		makeIntegers(generator, "defaultHdd",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("ResourceRequirement::defaultHdd")).getAll(),
				random);
		makeStrings(generator, "nodeIp", 
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("HostInstance::nodeIp")).getAll(),
				random);
		makeIntegers(generator, "availableCpu",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("HostInstance::availableCpu")).getAll(),
				random);
		makeIntegers(generator, "availableRam",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("HostInstance::availableRam")).getAll(),
				random);
		makeIntegers(generator, "availableHdd",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("HostInstance::availableHdd")).getAll(),
				random);
		makeIntegers(generator, "totalCpu",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("HostInstance::totalCpu")).getAll(),
				random);
		makeIntegers(generator, "totalRam",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("HostInstance::totalRam")).getAll(),
				random);
		makeIntegers(generator, "totalHdd",
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("HostInstance::totalHdd")).getAll(),
				random);
		makeStrings(generator, "action", 
				generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation("Transition::action")).getAll(),
				random);
		
		var cursor = generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation(
				"CyberPhysicalSystem")).getAll();
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
		var signal = (EEnum) railway.getEClassifier("AppState");
		trace.put(generator.getProblemTrace().getNodeId("AppState::Stopped"), signal.getEEnumLiteral("Stopped"));
		trace.put(generator.getProblemTrace().getNodeId("AppState::Running"), signal.getEEnumLiteral("Running"));

		trace.put(generator.getProblemTrace().getNodeId("BOOLEAN::TRUE"), true);
		trace.put(generator.getProblemTrace().getNodeId("BOOLEAN::FALSE"), false);
	}
	private void makeIntegers(ModelGenerator generator, String name, Cursor<Tuple, TruthValue> cursor, Random random){
		while (cursor.move()){
			var hostId = cursor.getKey().get(0);
			var host = (EObject) trace.get(hostId);
			var feature = ((EObject) trace.get(hostId)).eClass().getEStructuralFeature(name);

			if(generator.getProblemTrace().getNodeId("INT::POSITIVE")==cursor.getKey().get(1)){
				host.eSet(feature, random.nextInt(1, 100) );
			}
			if(generator.getProblemTrace().getNodeId("INT::NEGATIVE")==cursor.getKey().get(1)){
				host.eSet(feature, random.nextInt(-10, 0) );
			}
			if(generator.getProblemTrace().getNodeId("INT::ZERO")==cursor.getKey().get(1)){
				host.eSet(feature, 0);
			}

			System.out.println("\tFeature set: "+hostId+ "(image: "+trace.get(hostId).hashCode()
					+") ---["+feature.getName()+"]---> "
					+host.eGet(feature));
		}
	}

	private void makeStrings(ModelGenerator generator, String name, Cursor<Tuple, TruthValue> cursor, Random random){
		RandomStringGenerator source = new RandomStringGenerator.Builder()
     		.withinRange('a', 'z')
			.withinRange('A', 'Z')
			.withinRange('0', '1').get();

		while (cursor.move()){
			var hostId = cursor.getKey().get(0);
			var host = (EObject) trace.get(hostId);
			var feature = ((EObject) trace.get(hostId)).eClass().getEStructuralFeature(name);

			if(generator.getProblemTrace().getNodeId("STRING::RANDOM")==cursor.getKey().get(1)){
				host.eSet(feature, source.generate(random.nextInt(3, 10)));
			}
			if(generator.getProblemTrace().getNodeId("STRING::VALUE1")==cursor.getKey().get(1)){
				host.eSet(feature, "value1");
			}
			if(generator.getProblemTrace().getNodeId("STRING::VALUE2")==cursor.getKey().get(1)){
				host.eSet(feature, "2value");
			}
			if(generator.getProblemTrace().getNodeId("STRING::EMPTY")==cursor.getKey().get(1)){
				host.eSet(feature, new String());
			}
			if(generator.getProblemTrace().getNodeId("STRING::QQ")==cursor.getKey().get(1)){
				host.eSet(feature, "");
			}

			System.out.println("\tFeature set: "+hostId+ "(image: "+trace.get(hostId).hashCode()
					+") ---["+feature.getName()+"]---> "
					+host.eGet(feature));
		}
	}
	public void save(EObject root, File dir, String fileName) throws IOException {
		var res = resourceSet.createResource(URI.createURI(new File(dir, fileName).toURI().toString()));
		res.getContents().add(root);
		res.save(Collections.EMPTY_MAP);

	}

}
