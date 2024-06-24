package se.liu.ida.sas.pelab.vqlsyntaxcheck

import java.sql.DriverManager
import java.sql.Connection
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl
import org.eclipse.emf.common.util.URI
import java.io.File
import org.eclipse.emf.ecore.EPackage
import java.util.ArrayList
import java.io.BufferedWriter
import java.io.FileWriter

class EvaluateDatabase {
	private static final String JDBC_URL = "jdbc:sqlite:" + System.getenv("JDBC_URL");
	private static final String MAKE_ABSOLUTE = System.getenv("PROJECT_PATH");
	private static final String OUTPUT = System.getenv("OUTPUT");
	
	def static getPrefix(String metamodel){
		val ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
			"ecore", new EcoreResourceFactoryImpl());
		
		val resource = resourceSet.getResource(
			URI.createFileURI(new File(metamodel).absolutePath), true
		);
		
		
		val pkg = resource.contents.get(0) as EPackage
		return "import \"" + pkg.nsURI + "\"\n"
		
	}
	
	def static void main(String[] args) {
		val connection = DriverManager.getConnection(JDBC_URL);
		val sqlQuery = "SELECT * FROM pairs"
		val statement = connection.createStatement()
		val resultSet = statement.executeQuery(sqlQuery)

		val list_ids = new ArrayList<Integer>();
		while (resultSet.next()) {
			val nl = resultSet.getString("nl");
			var pattern = resultSet.getString("pattern");
			val metamodel = resultSet.getString("metamodel");
			try {
				val header = getPrefix(MAKE_ABSOLUTE + metamodel);
				pattern = header + "import \"http://www.eclipse.org/emf/2002/Ecore\"\n" + pattern;
				println("=============================================\n"+pattern)
				if (Evaluate.evaluate(MAKE_ABSOLUTE + metamodel, pattern)){
					println("====> CORRECT")
					list_ids.add(resultSet.getInt("id"));	
				} else {
					println("====> FAULTY")
				}
				if(header.contains("import \"http://example.org/\"")){
					Thread.sleep(5000)
				}
			} catch(Exception e){
					e.printStackTrace
			}
			
		}

		saveListToFile(list_ids, OUTPUT)
		
	}
	
	def static void saveListToFile(ArrayList<Integer> list, String filePath) {
		
		val filew = new FileWriter(filePath)
        val writer = new BufferedWriter(filew)
            // Write each item to the file
        for (item : list) {
                writer.write(item.toString)
                writer.newLine // Add a new line after each item
            }
        writer.flush
        writer.close
    }
	
}