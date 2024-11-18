package se.liu.ida.sas.pelab.text2vql.testing.syntax;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class EvaluateDatabase {
    private static final String JDBC_URL = ("jdbc:sqlite:" + System.getenv("JDBC_URL"));

    private static final String MAKE_ABSOLUTE = System.getenv("PROJECT_PATH");

    private static final String OUTPUT = System.getenv("OUTPUT");

    public static String getPrefix(final String metamodel) {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());
        Resource resource = resourceSet.getResource(
                URI.createFileURI(new File(metamodel).getAbsolutePath()), true);

        final EPackage pkg = ((EPackage) resource.getContents().get(0));
        return "import \"" + pkg.getNsURI() + "\"\n";
    }

    public static void main(final String[] args) throws SQLException, IOException {
        Connection connection = DriverManager.getConnection(EvaluateDatabase.JDBC_URL);
        String sqlQuery = "SELECT * FROM pairs";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        ArrayList<Integer> list_ids = new ArrayList<>();
        while (resultSet.next()) {
            String nl = resultSet.getString("nl");
            String pattern = resultSet.getString("pattern");
            String metamodel = resultSet.getString("metamodel");
            try {
                String header = EvaluateDatabase.getPrefix((EvaluateDatabase.MAKE_ABSOLUTE + metamodel));
                pattern = header + "import \"http://www.eclipse.org/emf/2002/Ecore\"\n" + pattern;
                System.out.println("=============================================\n" + pattern);
                if (Evaluate.evaluate((EvaluateDatabase.MAKE_ABSOLUTE + metamodel), pattern)) {
                    System.out.println("====> CORRECT");
                    list_ids.add(resultSet.getInt("id"));
                } else {
                    System.out.println("====> FAULTY");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EvaluateDatabase.saveListToFile(list_ids, EvaluateDatabase.OUTPUT);
    }

    public static void saveListToFile(final ArrayList<Integer> list, final String filePath) throws IOException {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        for (final Integer item : list) {
            writer.write(item.toString());
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }
}
