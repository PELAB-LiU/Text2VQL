package se.liu.ida.sas.pelab.text2vql.java;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class JavaTester {
    public static void main(String[] args) throws ClassNotFoundException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
        
        var cj =  new CompileJava();

        var gencode = cj.generateJavaFile("HelloWorld", null);
        var classes = cj.compileJavaFile(gencode);
        cj.run("HelloWorld", null, classes);

    }
}
