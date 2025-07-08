package se.liu.ida.sas.pelab.text2vql.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Functions.Function1;

public class CompileJava {
    private String wd = "generated";
    private boolean deleteOnExit = false;
    private JavaTemplate template = new JavaTemplate();


    public File generateJavaFile(String name, String query) throws IOException{
        var file = new File(wd, name+".java");
        var writer = new FileWriter(file);
        writer.write(template.generateJavaCode(name));
        writer.flush();
        writer.close();

        if(deleteOnExit){
            file.deleteOnExit();
        }
        return file;
    }

    public File[] compileJavaFile(File... files){
        var compilerOptions = Arrays.asList("-classpath", System.getProperty("java.class.path"));
        
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        var compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));

        CompilationTask task = compiler.getTask(null, null, null, compilerOptions, null, compilationUnits);
        boolean success = task.call();
        if(success){
            var compiled = new ArrayList<File>();
            for(var file : files){
                String fileName = file.getName();
                String baseName = fileName.substring(0, fileName.length() - 5); // remove ".java"
                File classFile = new File(file.getParent(), baseName + ".class");
                compiled.add(classFile);
            }
            return compiled.toArray(new File[compiled.size()]);
        }
        return null;
    }

    public void run(String classname, Function0<Object[]> args, File... files) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException{
        run(classname, null, null, () -> new Object[]{new String[]{}}, files);
    }

    public void run(String classname, Function1<Class<?>, Method> method, Function1<Class<?>, Object> instance, Function0<Object[]> args, File... files) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException{
        File currentDir = new File(wd).getAbsoluteFile();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { currentDir.toURI().toURL() });
        Class<?> loadedClass = Class.forName(classname, true, classLoader);

        Method targetMethod = method!=null ? method.apply(loadedClass) : loadedClass.getMethod("main", String[].class);

        Object targetInstance = null;
        if(instance!=null){
            targetInstance = instance.apply(loadedClass);
        }
        
        targetMethod.invoke(targetInstance, args.apply()); // Cas
    }
}
