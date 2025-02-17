package se.liu.ida.sas.pelab.text2vql.vql;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.xtext.parser.ParseException;
import org.eclipse.xtext.resource.XtextResourceSet;
import se.liu.ida.sas.pelab.text2vql.utilities.syntax.SyntaxChecker;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SyntaxCheckVQL extends SyntaxChecker {
    private final Injector injector;
    private final Set<String> initial;
    public static void main(String[] args) throws SQLException, IOException {
        /*
         * Process command line arguments
         */
        String dbURL = Arrays.stream(args).filter(arg -> arg.startsWith("dburl=")).map(arg -> arg.replaceFirst("dburl=","")).findFirst().orElseGet(()->"jdbc:sqlite:/config/text2vql/dataset_construction/dataset.db");
        String metamodels = Arrays.stream(args).filter(arg -> arg.startsWith("metamodels=")).map(arg -> arg.replaceFirst("metamodels=","")).findFirst().orElseGet(()->"/config/text2vql/dataset_construction/");
        File out = Arrays.stream(args).filter(arg -> arg.startsWith("out=")).map(arg -> arg.replaceFirst("out=","")).map(File::new).findFirst().orElseGet(()->null);

                //FIXME pattern should be called something else
        SyntaxCheckVQL checker = new SyntaxCheckVQL(dbURL, "pattern", metamodels, out);
        checker.run();
    }

    public SyntaxCheckVQL(String dbURL, String queryColumn, String metamodels, File output) throws SQLException, IOException {
        super(dbURL, queryColumn, metamodels, output);

        EMFPatternLanguageStandaloneSetup.doSetup();
        StandaloneParserWithSeparateModules runtimeModule = new StandaloneParserWithSeparateModules();
        injector = Guice.createInjector(runtimeModule);
        EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);

        initial = new HashSet<String>(EPackage.Registry.INSTANCE.keySet());
    }

    @Override
    public boolean test(String query, File metamodel) {
        StringBuilder builder = new StringBuilder();
        /*
         * Reset registry
         */
        Set<String> current = new HashSet<String>(EPackage.Registry.INSTANCE.keySet());
        current.removeIf(initial::contains);
        current.forEach(EPackage.Registry.INSTANCE::remove);
        /*
         * Setup resource set
         */
        XtextResourceSet resources = injector.getInstance(XtextResourceSet.class);
        resources.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());
        /*
         * Load and fix metamodels
         * Add import to query
         */
        Resource meta = resources.getResource(URI.createFileURI(metamodel.getAbsolutePath()), true);
        meta.getContents().forEach(res -> {
            if(res instanceof EPackage epackage){
                if(epackage.getNsURI()==null){
                    epackage.setNsURI("nsUriFix-"+epackage.getName()+"-"+epackage.hashCode());
                }
                EPackage.Registry.INSTANCE.put(epackage.getNsURI(), epackage);
                builder.append("import \"%s\"".formatted(epackage.getNsURI())).append(System.lineSeparator());
            }
        });
        /*
         * Finalize query
         */
        builder.append("import \"http://www.eclipse.org/emf/2002/Ecore\"").append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append(query).append(System.lineSeparator());
        /*
         * Test query
         */
        try {
            PatternParsingResults result = PatternParserBuilder.instance().parse(builder.toString());

            if(result.hasError()){
                StringBuilder error = new StringBuilder();
                error.append("Erroneous query:\n");
                error.append(builder);
                error.append("Message:");
                result.getErrors().forEach(err -> {
                    error.append(err.getMessage());
                    error.append(System.lineSeparator());
                });
                error.append("Metamodel: ").append(metamodel.getAbsolutePath());
                logMessage(error.toString());
                if (builder.toString().contains("check")){
                    try {
                        System.out.println("Fail");
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return false;
            } else {
                logMessage("Pass");
                System.out.println("Pass:\n"+builder);
                if (builder.toString().contains("check")){
                    try {
                        System.out.println("Pass");
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return true;
            }
        } catch (ParseException e){
            StringBuilder error = new StringBuilder();
            error.append("Erroneous query:").append(System.lineSeparator());
            error.append(builder).append(System.lineSeparator());
            error.append("Exception: ").append(e.getMessage());
            logMessage(error.toString());
            e.printStackTrace();
            return false;
        }
    }
}
