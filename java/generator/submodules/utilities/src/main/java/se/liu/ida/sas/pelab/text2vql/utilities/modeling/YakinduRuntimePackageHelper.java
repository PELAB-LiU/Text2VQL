package se.liu.ida.sas.pelab.text2vql.utilities.modeling;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import se.liu.ida.sas.pelab.text2vql.utilities.ResourcesHelper;

public class YakinduRuntimePackageHelper extends PackageHelper{
    public YakinduRuntimePackageHelper() {
        super(loadEPackage());
    }
    private static EPackage loadEPackage(){
        var resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());
        Resource meta = resourceSet.getResource(URI.createFileURI("/config/text2vql/dataset_construction/seed/yakindu_simplified.ecore"),true);
        return (EPackage) meta.getContents().getFirst();
    }
}
