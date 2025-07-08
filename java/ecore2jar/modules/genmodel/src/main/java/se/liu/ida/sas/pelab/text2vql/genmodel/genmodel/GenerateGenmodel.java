package se.liu.ida.sas.pelab.text2vql.genmodel.genmodel;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;

import se.liu.ida.sas.pelab.text2vql.genmodel.genmodel.fixes.FixEDataTypeMissingInstanceClassName;
import se.liu.ida.sas.pelab.text2vql.genmodel.genmodel.fixes.ChangeSaveLocation;
import se.liu.ida.sas.pelab.text2vql.genmodel.genmodel.fixes.FixMissingNSURI;
import se.liu.ida.sas.pelab.text2vql.genmodel.genmodel.fixes.FixEnumLiteralName;

import se.liu.ida.sas.pelab.text2vql.genmodel.genmodel.fixes.FixUnresolvedProxy;
// GenPackage example
// https://github.com/eclipse-emf/org.eclipse.emf/blob/b5bc13ab0e1f1850d965e10da446f0240d143feb/tests/org.eclipse.emf.test.tools/src/org/eclipse/emf/test/tools/codegen/GenModelTest.java

@Mojo(name = "generateGenmodel")
public class GenerateGenmodel extends AbstractMojo{
    
    @Parameter(property = "metamodel", required = true)
    private String ecore;

    @Parameter(property = "genmodel", required = true)
    private String target;

    @Parameter(property = "modeldir", required = true)
    private String modeldir;

    @Parameter(property = "basepackage")
    private String basepackage;

    @Override
    public void execute() throws MojoExecutionException{
        try {
            System.out.println("Generating genmodel for metamodel: "+ecore);
            var resourceSet = new ResourceSetImpl();
            /*resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                    "ecore", new EcoreResourceFactoryImpl());*/
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                    "genmodel", new XMIResourceFactoryImpl());
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                    "ecore", new XMIResourceFactoryImpl());

            Resource meta = resourceSet.getResource(URI.createFileURI(ecore),true);
            //EPackage ePackage = (EPackage) meta.getContents().getFirst();
            List<EPackage> list = meta.getContents().stream()
                .filter(e -> e instanceof EPackage)
                .map(e -> (EPackage) e)
                .map(new FixEDataTypeMissingInstanceClassName()::fix)
                .map(new FixMissingNSURI()::fix)
                .map(new FixEnumLiteralName()::fix)
                .map(new FixUnresolvedProxy()::fix)
                .map(new ChangeSaveLocation(target)::fix)
                .collect(Collectors.toList());
            meta.save(Map.of());

            GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
            genModel.setModelDirectory(modeldir);
            //genModel.initialize(Collections.singletonList(ePackage));
            System.out.println("Number of metamodels:"+list.size());
            genModel.initialize(list);
            GenPackage genPackage = genModel.getGenPackages().get(0);
            if(basepackage!=null){
                genPackage.setBasePackage(basepackage);
            }

            Resource genModelResource = resourceSet.createResource(URI.createFileURI(target));
            genModelResource.getContents().add(genModel);
            genModelResource.save(Collections.emptyMap());
            System.out.println("GenModel saved to: " + target);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoExecutionException("Failed to serialize GenModel", e);

        }
    }
}
