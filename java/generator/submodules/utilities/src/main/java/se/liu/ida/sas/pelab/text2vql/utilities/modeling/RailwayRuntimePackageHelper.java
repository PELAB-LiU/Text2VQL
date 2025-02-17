package se.liu.ida.sas.pelab.text2vql.utilities.modeling;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;

public class RailwayRuntimePackageHelper  extends PackageHelper{
    public final EPackage railway;
    public final EClass RailwayContainer;
    public final EClass Region;
    public final EClass Segment;

    public final EStructuralFeature regions;
    public final EStructuralFeature elements;
    public final EStructuralFeature length;
    public RailwayRuntimePackageHelper(EPackage railway){
        super(railway);
        this.railway = railway;

        RailwayContainer = (EClass) railway.getEClassifier("RailwayContainer");
        Region = (EClass) railway.getEClassifier("Region");
        Segment = (EClass) railway.getEClassifier("Segment");

        regions = RailwayContainer.getEStructuralFeature("regions");
        elements = Region.getEStructuralFeature("elements");
        length = Segment.getEStructuralFeature("length");
    }
}
