package se.liu.ida.sas.pelab.text2vql.utilities.syntax;

import se.liu.ida.sas.pelab.text2vql.utilities.csv.CSVHandler;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class SyntaxChecker {
    private final Connection connection;
    private final String dbQuery;
    private final String metamodels;

    private final CSVHandler output;
    private static final String id = "id";
    private static final String result = "syntax_ok";
    private static final String message = "message";
    public SyntaxChecker(String dbURL, String queryColumn, String metamodels, File output) throws SQLException, IOException {
        connection = DriverManager.getConnection(dbURL);
        dbQuery = """
                SELECT id, nl, <query> as query, metamodel FROM pairs
                """.replaceFirst("<query>", queryColumn);
        this.metamodels = metamodels;
        this.output = new CSVHandler(output, id, result, message);
    }
    public void run() throws SQLException {
        var result = connection.createStatement().executeQuery(dbQuery);
        while (result.next()){
            boolean parsed = false;
            String nl = result.getString("nl");

            int id = result.getInt("id");
            System.out.println("ID: "+ id);
            output.put(SyntaxChecker.id, id);
            String query = result.getString("query");
            String metamodel = result.getString("metamodel");

            try{
                var file = new File(metamodels,metamodel);
                parsed = file.exists() && test(query, file);
            } catch (Exception e){
                logMessage("[Syntax check failed with exception] "+e.getMessage());
            } finally {
                output.put(SyntaxChecker.result, parsed);
            }
            output.commit();
        }
    }
    protected void logMessage(String message){
        System.out.println(message);
        output.put(SyntaxChecker.message, message);
    }
    abstract public boolean test(String query, File metamodel);
}
