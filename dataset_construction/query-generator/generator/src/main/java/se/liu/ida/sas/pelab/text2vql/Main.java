package se.liu.ida.sas.pelab.text2vql;

import com.google.common.io.Resources;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import se.liu.ida.sas.pelab.text2vql.templates.Ecore2Problem;
import se.liu.ida.sas.pelab.text2vql.templates.VQLTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            runVQLRegrow();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void runEPackage2Problem(String[] args) throws URISyntaxException {
        EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        var resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());

        Resource meta = resourceSet.getResource(URI.createURI(
                        Main.class.getClassLoader().getResource("railway/railway.ecore").toURI().toString()),
                true
        );
        EPackage railway = (EPackage) meta.getContents().getFirst();
        System.out.println(Ecore2Problem.map(railway));
    }

    public static void runVQLRegrow() throws URISyntaxException, IOException {
        EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        var resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());

        Resource resource = resourceSet.getResource(URI.createURI(
                Main.class.getClassLoader().getResource("railway/railway.ecore").toURI().toString()),
                true
        );

        EPackage domain = (EPackage) resource.getContents().get(0);
        EPackage.Registry.INSTANCE.put(domain.getNsURI(), domain);


        EMFPatternLanguageStandaloneSetup.doSetup();

        String vql = Resources.toString(
                Main.class.getClassLoader().getResource("railway/railway.vql").toURI().toURL(),
                StandardCharsets.UTF_8
        );
        PatternParsingResults parsed = PatternParserBuilder.instance().parse(vql);

        System.out.println("Regrow:");
        parsed.getPatterns().forEach(pattern -> {
            System.out.println(VQLTemplate.query(pattern));
        });

        var correct = parsed.hasError();
        if(correct){

        } else {
            // errors?.forEach[println(it)]
            System.out.println("Input has errors.");
            for(Object o : parsed.getErrors()){
                System.out.println(o.toString());
            }
        }
    }
}