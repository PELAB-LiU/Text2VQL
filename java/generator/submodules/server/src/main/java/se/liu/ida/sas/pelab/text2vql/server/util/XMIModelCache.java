package se.liu.ida.sas.pelab.text2vql.server.util;


import java.io.File;
import java.io.IOException;
import java.util.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * A cache for loading and storing EMF XMI models.
 */
public class XMIModelCache {

    private final Map<File, List<EObject>> cache = new HashMap<>();
    private final ResourceSet resourceSet;

    public XMIModelCache() {
        // Initialize a ResourceSet and register default XMI factory
        resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry()
                   .getExtensionToFactoryMap()
                   .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    }

    public List<EObject> getModels(File fileOrDir, Resource ecore) throws IOException {
        return getModels(fileOrDir, ecore, -1);
    }
    public List<EObject> getModels(File fileOrDir, Resource ecore, int maxmodels) throws IOException {
        if(cache.containsKey(fileOrDir)){
            return cache.get(fileOrDir);
        }

        final ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("xmi", new XMIResourceFactoryImpl());
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("ecore", new XMIResourceFactoryImpl());
        
        
        //Resource ecoreResource = resourceSet.getResource(URI.createFileURI(ecore.getAbsolutePath()), true);
        ecore.getContents().forEach(content ->{
            if(content instanceof EPackage ePackage){
                rs.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
            }
        });

        if (fileOrDir.isFile()) {
            System.out.println(fileOrDir.getAbsolutePath());
            Resource res = rs.getResource(URI.createFileURI(fileOrDir.getAbsolutePath()), true);
            List<EObject> models = new ArrayList<EObject>(res.getContents());
            cache.put(fileOrDir, models);
            return models;
        } else if (fileOrDir.isDirectory()) {
            File[] xmiFiles = fileOrDir.listFiles((dir, name) -> name.endsWith(".xmi"));
            List<EObject> models = new ArrayList<EObject>();
            if (xmiFiles != null) {
                for (File xmi : xmiFiles) {
                    if(maxmodels==0){
                        break;
                    }

                    System.out.println(xmi.getAbsolutePath());
                    Resource res = rs.getResource(URI.createFileURI(xmi.getAbsolutePath()), true);
                    models.addAll(res.getContents());
                    maxmodels--;
                    
                }
            }
            cache.put(fileOrDir, models);
            return models;
        }
        return null;
    }

    public void clear() {
        cache.clear();
    }
}