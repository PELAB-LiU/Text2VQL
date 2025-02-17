package se.liu.ida.sas.pelab.text2vql.ocl;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.syntax.SyntaxChecker;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

public class SyntaxCheckOCL extends SyntaxChecker {
    @Override
    public boolean test(String query, File metamodel) {
        return false;
        /*try {
            EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
            OCL ocl = OCL.newInstanceAbstract(environmentFactory);
            OCLHelper helper = ocl.createOCLHelper();
            helper.setContext(railway.getEClassifier("RailwayContainer"));
            OCLExpression expression = helper.createQuery(query);
            ocl.createQuery(expression);
            return true;
        } catch (ParserException e) {
            e.printStackTrace();
            return false;
        }*/
    }

    public SyntaxCheckOCL(String dbURL, String queryColumn, String metamodels, File output) throws SQLException, IOException {
        super(dbURL, queryColumn, metamodels, output);
    }
    public static void main(String[] args) throws SQLException, IOException {
        /*
         * Process command line arguments
         */
        String dbURL = Arrays.stream(args).filter(arg -> arg.startsWith("dburl=")).map(arg -> arg.replaceFirst("dburl=","")).findFirst().orElseGet(()->"jdbc:sqlite:/config/text2vql/dataset_construction/dataset.db");
        String metamodels = Arrays.stream(args).filter(arg -> arg.startsWith("metamodels=")).map(arg -> arg.replaceFirst("metamodels=","")).findFirst().orElseGet(()->"/config/text2vql/dataset_construction/");
        File out = Arrays.stream(args).filter(arg -> arg.startsWith("out=")).map(arg -> arg.replaceFirst("out=","")).map(File::new).findFirst().orElseGet(()->null);

        //FIXME pattern should be called something else
        SyntaxCheckOCL checker = new SyntaxCheckOCL(dbURL, "pattern", metamodels, out);
        checker.run();
    }
}
