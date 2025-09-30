package se.liu.ida.sas.pelab.text2vql.server.util;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Guice;

import se.liu.ida.sas.pelab.text2vql.vql.StatelessVQLSyntaxCheck;

public class EMFPackageManager {

    public static EMFPackageManager INSTANCE = new EMFPackageManager();
    private static final ConcurrentHashMap<File, Resource> loadedResources = new ConcurrentHashMap<>();

    public final ResourceSet resourceSet;

    private EMFPackageManager(){
        this.resourceSet = StatelessVQLSyntaxCheck.init();
    };

    public synchronized Resource loadMetamodelToGlobalPackageRegistry(File metamodel, ResourceSet resourceSet){
        if(loadedResources.containsKey(metamodel)){
            System.out.println("Metamodel is already loaded.");
            return loadedResources.get(metamodel);
        }
        Resource meta = resourceSet.getResource(URI.createFileURI(metamodel.getAbsolutePath()), true);
        loadedResources.put(metamodel, meta);

        getMetamodelsOfResource(meta).forEach(it -> EPackage.Registry.INSTANCE.putIfAbsent(it.getNsURI(), it));
        return meta;
    }

    public List<EPackage> getMetamodelsOfResource(Resource resource){
        return resource.getContents().stream()
            .filter(it -> it instanceof EPackage)
            .map(it -> (EPackage) it)
            .toList();
    }
    

}
