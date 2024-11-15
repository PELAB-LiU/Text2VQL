package se.liu.ida.sas.pelab.text2vql.helpers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import se.liu.ida.sas.pelab.text2vql.ResourcesHelper;

public class RailwayLoader {
    public static Resource loadRailwayModel(ResourceSet resourceSet){
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "xmi", new EcoreResourceFactoryImpl());

        return resourceSet.getResource(
                ResourcesHelper.emfURI("railway/railway.xmi"), true);
    }
    public static Resource loadRailway(){
        EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        var resourceSet = new ResourceSetImpl();
        loadRailwayPackage(resourceSet);
        return loadRailwayModel(resourceSet);
    }

    public static EPackage loadRailwayPackage(ResourceSet resourceSet){
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());
        Resource meta = resourceSet.getResource(ResourcesHelper.emfURI("railway/railway.ecore"),true);
        EPackage railway = (EPackage) meta.getContents().getFirst();
        EPackage.Registry.INSTANCE.put(railway.getNsURI(), railway);

        return railway;
    }
}
