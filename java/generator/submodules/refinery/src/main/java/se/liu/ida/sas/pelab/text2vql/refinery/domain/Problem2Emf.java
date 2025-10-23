package se.liu.ida.sas.pelab.text2vql.refinery.domain;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import se.liu.ida.sas.pelab.text2vql.utilities.ResourcesHelper;
import tools.refinery.generator.ModelGenerator;
import tools.refinery.logic.term.truthvalue.TruthValue;
import tools.refinery.store.map.Cursor;
import tools.refinery.store.tuple.Tuple;

//Similar operation to EMF Copier.
public class Problem2Emf extends HashMap<Integer, Object>{
    protected final EPackage epackage;
    protected final EFactory factory;

    public Problem2Emf(EPackage ePackage){
        this.epackage = ePackage;
        this.factory = ePackage.getEFactoryInstance();
    }
    public void map(ModelGenerator generator){
        clear();
        configureMapping(generator);

        getEnums().forEach(it -> makeEnum(generator, it));
        configureEnum(generator);

        getInstantiableClasses().forEach(classType ->{
            makeNormalClasses(generator, classType);
        });
        configureNodes(generator);

        getAllEReferences().forEach(it -> makeReferences(generator, it));
        configureReferences(generator);

        getAllEAttribute().forEach(it -> makeAttribute(generator, it));
        configureAttribute(generator);
    }

    // Override this to run code before mapping.
    // Like map Enums
    public void configureMapping(ModelGenerator generator){};
    public void configureEnum(ModelGenerator generator){};
    public void configureNodes(ModelGenerator generator){};
    public void configureReferences(ModelGenerator generator){};
    public void configureAttribute(ModelGenerator generator){};

    private Stream<EEnum> getEnums(){
        return epackage.getEClassifiers().stream()
            .filter(this::filerEEnum)
            .map(it -> (EEnum) it);
    }
    private void makeEnum(ModelGenerator generator, EEnum eenum){
		System.out.println("Mapping: "+eenum.getName());
        for(EEnumLiteral literal : eenum.getELiterals()){
            put(generator.getProblemTrace().getNodeId(eenum.getName()+"::"+literal.getName()), literal);
        }
	}

    private Stream<EClass> getInstantiableClasses(){
        return epackage.getEClassifiers().stream()
                .filter(this::filerEClass)
                .map(it -> (EClass) it)
                .filter(it -> !it.isAbstract());
    }


    private void makeNormalClasses(ModelGenerator generator, EClass eclass){
		System.out.println("Mapping: "+eclass.getName());
		mapClasses(eclass, generator.getPartialInterpretation(
				generator.getProblemTrace().getPartialRelation(eclass.getName())).getAll());
	}
    private void mapClasses(EClass eclass, Cursor<Tuple, TruthValue> cursor){
		while (cursor.move()){
			var id = cursor.getKey().get(0);
			try{
				var image = factory.create(eclass);
				put(id,image);
				System.out.println("\tInstance added: "+id+ " (image: " +image.hashCode()+ ")");
			} catch (NullPointerException | ClassCastException e){}
        }
	}

    private Stream<EReference> getAllEReferences() {
        return this.epackage.getEClassifiers().stream()
                .filter(this::filerEClass)
                .map(it -> (EClass) it)
                .flatMap(eClass -> eClass.getEReferences().stream());
    }
    private void makeReferences(ModelGenerator generator, EReference reference){
		System.out.println("Mapping reference: "+fqn(reference));
        String fqn = reference.getEContainingClass().getName()+"::"+reference.getName();
        try {
            mapRelations(generator, reference, generator.getPartialInterpretation(
				generator.getProblemTrace().getPartialRelation(fqn)).getAll());
        } catch(IllegalArgumentException e){
            referenceNotFoundHandler(generator, reference);
        }
		
	}
    public void referenceNotFoundHandler(ModelGenerator generator, EReference reference){
        throw new RuntimeException("Unrecognized clause: "+fqn(reference));
    }
	@SuppressWarnings("unchecked")
    private void mapRelations(ModelGenerator generator, EReference reference, Cursor<Tuple, TruthValue> cursor){
		while (cursor.move()){
			var hostId = cursor.getKey().get(0);
			var targetId = cursor.getKey().get(1);
			var feature = ((EObject) get(hostId)).eClass().getEStructuralFeature(reference.getName());

			if(feature.isMany()){
				@SuppressWarnings("rawtypes")
                var list = (EList) ((EObject) get(hostId)).eGet(feature);
				list.add(getReferenceValue(generator, cursor.getKey(), reference));
				System.out.println("\tInserted to feature: "+hostId+ "(image: "+get(hostId).hashCode()
						+") ---["+feature.getName()+"]---> "
						+targetId+"(image:"+get(targetId).hashCode()+")");
			} else {
				((EObject) get(hostId)).eSet(feature, getReferenceValue(generator, cursor.getKey(), reference));
				System.out.println("\tFeature set: "+hostId+ "(image: "+get(hostId).hashCode()
						+") ---["+feature.getName()+"]---> "
						+targetId+"(image:"+get(targetId).hashCode()+")");
			}
		}
	}
    public EObject getReferenceValue(ModelGenerator generator, Tuple relation, EReference reference){
        return (EObject) get(relation.get(1));
    }

    private Stream<EAttribute> getAllEAttribute() {
        return this.epackage.getEClassifiers().stream()
                .filter(this::filerEClass)
                .map(it -> (EClass) it)
                .flatMap(eClass -> eClass.getEAttributes().stream());
    }
    private void makeAttribute(ModelGenerator generator, EAttribute attribute){
		System.out.println("Mapping attribute: "+fqn(attribute));
        String fqn = attribute.getEContainingClass().getName()+"::"+attribute.getName();
        try {
            mapAttribute(generator, attribute, generator.getPartialInterpretation(
				generator.getProblemTrace().getPartialRelation(fqn)).getAll());
        } catch(IllegalArgumentException e){
            attributeNotFoundHandler(generator, attribute);
        }
	}
    public void attributeNotFoundHandler(ModelGenerator generator, EAttribute attribute){
        throw new RuntimeException("Unrecognized clause: "+fqn(attribute));
    }
	@SuppressWarnings("unchecked")
    private void mapAttribute(ModelGenerator generator, EAttribute attribute, Cursor<Tuple, TruthValue> cursor){
		while (cursor.move()){
			var hostId = cursor.getKey().get(0);
			var targetId = cursor.getKey().get(1);
			var feature = ((EObject) get(hostId)).eClass().getEStructuralFeature(attribute.getName());

			if(feature.isMany()){
				@SuppressWarnings("rawtypes")
                var list = (EList) ((EObject) get(hostId)).eGet(feature);
                Object value = getAttributeValue(generator, cursor.getKey(),  attribute);
				list.add(value);
				System.out.println("\tInserted to feature: "+hostId+ "(image: "+get(hostId).hashCode()
						+") ---["+feature.getName()+"]---> "
						+targetId+"(image:"+valueof(targetId, value)+")");
			} else {
                Object value = getAttributeValue(generator, cursor.getKey(),  attribute);
				((EObject) get(hostId)).eSet(feature, getAttributeValue(generator, cursor.getKey(),  attribute));
				System.out.println("\tFeature set: "+hostId+ "(image: "+get(hostId).hashCode()
						+") ---["+feature.getName()+"]---> "
						+targetId+"(image:"+valueof(targetId, value).hashCode()+")");
			}
		}
	}
    public Object getAttributeValue(ModelGenerator generator, Tuple relation, EAttribute attribute){
        return get(relation.get(1));
    }
    private String valueof(int key, Object value){
        if(containsKey(key)){
            return Objects.toString(get(key).hashCode());
        } else {
            return Objects.toString(value);
        }
    }

    private boolean filerEClass(EClassifier cfr){
        return cfr instanceof EClass;
    }
    private boolean filerEEnum(EClassifier cfr){
        return cfr instanceof EEnum;
    }

    public static EPackage getEPackage(File metamodel){
		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				"ecore", new EcoreResourceFactoryImpl());
		Resource meta = resourceSet.getResource(ResourcesHelper.emfURI(metamodel), true);

		return (EPackage) meta.getContents().get(0);
	}
    public static String fqn(EStructuralFeature feature){
        return feature.getEContainingClass().getName()+"::"+feature.getName();
    }
}
