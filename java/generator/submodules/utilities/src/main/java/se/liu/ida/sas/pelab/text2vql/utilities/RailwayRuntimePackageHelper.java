package se.liu.ida.sas.pelab.text2vql.utilities;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;

public class RailwayRuntimePackageHelper {
    public final EFactory factory;
    public final EPackage railway;
    public final EClass RailwayContainer;
    public final EClass Region;
    public final EClass Segment;

    public final EStructuralFeature regions;
    public final EStructuralFeature elements;
    public final EStructuralFeature length;
    public RailwayRuntimePackageHelper(EPackage railway){
        this.railway = railway;
        factory = railway.getEFactoryInstance();

        RailwayContainer = (EClass) railway.getEClassifier("RailwayContainer");
        Region = (EClass) railway.getEClassifier("Region");
        Segment = (EClass) railway.getEClassifier("Segment");

        regions = RailwayContainer.getEStructuralFeature("regions");
        elements = Region.getEStructuralFeature("elements");
        length = Segment.getEStructuralFeature("length");
    }
    public EObject make(String eClass){
        EClass cls = (EClass) railway.getEClassifier(eClass);
        return factory.create(cls);
    }
    public <T> void set(EObject object, String feature, T value){
        EStructuralFeature relation = object.eClass().getEStructuralFeature(feature);
        object.eSet(relation, value);
    }

    public <T> void add(EObject object, String feature, T value){
        EStructuralFeature relation = object.eClass().getEStructuralFeature(feature);
        ((EList) object.eGet(relation)).add(value);
    }
}
