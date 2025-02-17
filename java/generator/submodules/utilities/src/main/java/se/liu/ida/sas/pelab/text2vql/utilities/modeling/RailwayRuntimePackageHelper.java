package se.liu.ida.sas.pelab.text2vql.utilities.modeling;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import se.liu.ida.sas.pelab.text2vql.utilities.ResourcesHelper;

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
    public  RailwayRuntimePackageHelper(){
        this(getEPackage());
    }
    private static EPackage getEPackage(){
        var resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());
        Resource meta = resourceSet.getResource(ResourcesHelper.emfURI("railway/railway.ecore"),true);
        return (EPackage) meta.getContents().getFirst();
    }
}
