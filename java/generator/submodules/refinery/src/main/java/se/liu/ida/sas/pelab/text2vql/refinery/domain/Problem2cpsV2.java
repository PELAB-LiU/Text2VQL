package se.liu.ida.sas.pelab.text2vql.refinery.domain;

import org.apache.commons.text.RandomStringGenerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.ResourceSet;

import se.liu.ida.sas.pelab.text2vql.utilities.Text2VQLProjectStructure;
import tools.refinery.generator.ModelGenerator;
import tools.refinery.store.tuple.Tuple;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Problem2cpsV2 extends Problem2Emf{
	//private ProblemTrace trace;
	private final ResourceSet resourceSet;
	
	private Random random;
	public Problem2cpsV2() {
		super(getEPackage(Text2VQLProjectStructure.find("dataset_construction/test_metamodel/cps.ecore")));
		resourceSet = epackage.eResource().getResourceSet();
		
		//EPackage.Registry.INSTANCE.put(epackage.getNsURI(), epackage);
	}
	public void configureMapping(ModelGenerator generator){
		this.random = new Random(generator.getRandomSeed());
	}
	public void configureEnum(ModelGenerator generator){
		put(generator.getProblemTrace().getNodeId("BOOLEAN::TRUE"), true);
		put(generator.getProblemTrace().getNodeId("BOOLEAN::FALSE"), false);
	}

	public Object getAttributeValue(ModelGenerator generator, Tuple relation, EAttribute attribute){
        switch (fqn(attribute)) {
        	case "Identifiable::identifier":
        	case "CyberPhysicalSystem::dbUrl":
	        case "ApplicationType::exeFileLocation":
    	    case "ApplicationType::exeType":
        	case "ApplicationType::zipFileUrl":
	        case "ApplicationInstance::dbUser":
    	    case "ApplicationInstance::dbPassword":
        	case "HostInstance::nodeIp":
	        case "Transition::action":
    	        return makeStrings(generator, relation);

	        case "Requirement::count":
    	    case "Requirement::availablePorts":
	        case "ApplicationInstance::priority":
    	    case "ResourceRequirement::defaultCpu":
        	case "ResourceRequirement::defaultRam":
	        case "ResourceRequirement::defaultHdd":
    	    case "HostInstance::availableCpu":
        	case "HostInstance::availableRam":
	        case "HostInstance::availableHdd":
    	    case "HostInstance::totalCpu":
        	case "HostInstance::totalRam":
        	case "HostInstance::totalHdd":
            	return makeIntegers(generator, relation);
			
			case "ApplicationType::exeFileSize":
				return (long) makeIntegers(generator, relation);
			
	        default:
    	        return super.getAttributeValue(generator, relation, attribute);
		}
    }
	private String makeStrings(ModelGenerator generator, Tuple relation){
		var source = new RandomStringGenerator.Builder()
     		.withinRange('a', 'z')
			.withinRange('A', 'Z')
			.withinRange('0', '1');

		if(generator.getProblemTrace().getNodeId("STRING::RANDOM")==relation.get(1)){
			return source.get().generate(random.nextInt(3, 10));
		}
		if(generator.getProblemTrace().getNodeId("STRING::RANDOM_WS")==relation.get(1)){
			return source.withinRange('\t', '\r')
					.withinRange(' ', ' ')
					.withinRange('\u00A0', '\u00A0').get().generate(random.nextInt(3, 10));
		}
		if(generator.getProblemTrace().getNodeId("STRING::VALUE1")==relation.get(1)){
			return "value1---";
		}
		if(generator.getProblemTrace().getNodeId("STRING::VALUE2")==relation.get(1)){
			return "2value";
		}
		if(generator.getProblemTrace().getNodeId("STRING::EMPTY")==relation.get(1)){
			return new String();
		}
		if(generator.getProblemTrace().getNodeId("STRING::QQ")==relation.get(1)){
			return "";
		}
		throw new RuntimeException("Unknown string type.");
	}

	private int makeIntegers(ModelGenerator generator, Tuple relation){	
		if(generator.getProblemTrace().getNodeId("INT::POSITIVE")==relation.get(1)){
			return random.nextInt(1, 100);
		}
		if(generator.getProblemTrace().getNodeId("INT::NEGATIVE")==relation.get(1)){
			return random.nextInt(-10, 0);
		}
		if(generator.getProblemTrace().getNodeId("INT::ZERO")==relation.get(1)){
			return 0;
		}
		throw new RuntimeException("Unknown integer type.");
	}


	public EObject toEMF(ModelGenerator generator){
		clear();
		super.map(generator);

		var cursor = generator.getPartialInterpretation(generator.getProblemTrace().getPartialRelation(
				"CyberPhysicalSystem")).getAll();
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
