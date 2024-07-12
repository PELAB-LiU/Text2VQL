package se.liu.ida.sas.pelab.vqlsyntaxcheck;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;

@SuppressWarnings("all")
public class EvaluateDatabase {
  private static final String JDBC_URL = ("jdbc:sqlite:" + System.getenv("JDBC_URL"));

  private static final String MAKE_ABSOLUTE = System.getenv("PROJECT_PATH");

  private static final String OUTPUT = System.getenv("OUTPUT");

  public static String getPrefix(final String metamodel) {
    final ResourceSet resourceSet = new ResourceSetImpl();
    Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    EcoreResourceFactoryImpl _ecoreResourceFactoryImpl = new EcoreResourceFactoryImpl();
    _extensionToFactoryMap.put(
      "ecore", _ecoreResourceFactoryImpl);
    final Resource resource = resourceSet.getResource(
      URI.createFileURI(new File(metamodel).getAbsolutePath()), true);
    EObject _get = resource.getContents().get(0);
    final EPackage pkg = ((EPackage) _get);
    String _nsURI = pkg.getNsURI();
    String _plus = ("import \"" + _nsURI);
    return (_plus + "\"\n");
  }

  public static void main(final String[] args) {
    try {
      final Connection connection = DriverManager.getConnection(EvaluateDatabase.JDBC_URL);
      final String sqlQuery = "SELECT * FROM pairs";
      final Statement statement = connection.createStatement();
      final ResultSet resultSet = statement.executeQuery(sqlQuery);
      final ArrayList<Integer> list_ids = new ArrayList<Integer>();
      while (resultSet.next()) {
        {
          final String nl = resultSet.getString("nl");
          String pattern = resultSet.getString("pattern");
          final String metamodel = resultSet.getString("metamodel");
          try {
            final String header = EvaluateDatabase.getPrefix((EvaluateDatabase.MAKE_ABSOLUTE + metamodel));
            pattern = ((header + "import \"http://www.eclipse.org/emf/2002/Ecore\"\n") + pattern);
            InputOutput.<String>println(("=============================================\n" + pattern));
            boolean _evaluate = Evaluate.evaluate((EvaluateDatabase.MAKE_ABSOLUTE + metamodel), pattern);
            if (_evaluate) {
              InputOutput.<String>println("====> CORRECT");
              list_ids.add(Integer.valueOf(resultSet.getInt("id")));
            } else {
              InputOutput.<String>println("====> FAULTY");
            }
            boolean _contains = header.contains("import \"http://example.org/\"");
            if (_contains) {
              Thread.sleep(5000);
            }
          } catch (final Throwable _t) {
            if (_t instanceof Exception) {
              final Exception e = (Exception)_t;
              e.printStackTrace();
            } else {
              throw Exceptions.sneakyThrow(_t);
            }
          }
        }
      }
      EvaluateDatabase.saveListToFile(list_ids, EvaluateDatabase.OUTPUT);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  public static void saveListToFile(final ArrayList<Integer> list, final String filePath) {
    try {
      final FileWriter filew = new FileWriter(filePath);
      final BufferedWriter writer = new BufferedWriter(filew);
      for (final Integer item : list) {
        {
          writer.write(item.toString());
          writer.newLine();
        }
      }
      writer.flush();
      writer.close();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
