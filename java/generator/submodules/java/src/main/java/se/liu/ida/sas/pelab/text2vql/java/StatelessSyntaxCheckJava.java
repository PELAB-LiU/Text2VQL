package se.liu.ida.sas.pelab.text2vql.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

import se.liu.ida.sas.pelab.text2vql.java.compiler.CompileJava;

public interface StatelessSyntaxCheckJava {
    default ParseResult check(String query, Resource metamodels, File domainJar){
        try {
            Path tmpwd = Files.createTempDirectory("text2vql-java-syntax");
            CompileJava compiler = new CompileJava(tmpwd);
            File queryJava = compiler.generateJavaFile(query, getMetamodelsOfResource(metamodels));

            if(queryJava==null){
                return new ParseResult(false, "Query class not found.");
            }

            var result = compiler.compileJavaFile(domainJar, queryJava);

            StringBuilder diagnostics = new StringBuilder();
            result.getValue().forEach(issue -> {
                diagnostics.append(issue).append(System.lineSeparator());
            });

            return new ParseResult(result.getKey() != null, diagnostics.toString());
        } catch (IOException e) {
            return new ParseResult(false, e.getMessage());
        }
    }

    static List<EPackage> getMetamodelsOfResource(Resource resource){
        return resource.getContents().stream()
            .filter(it -> it instanceof EPackage)
            .map(it -> (EPackage) it)
            .toList();
    }
    public static record ParseResult(Boolean isCorrect, String diagnostics){}
}
