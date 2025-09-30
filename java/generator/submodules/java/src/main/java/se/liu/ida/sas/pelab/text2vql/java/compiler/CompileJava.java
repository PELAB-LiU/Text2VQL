package se.liu.ida.sas.pelab.text2vql.java.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Functions.Function1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompileJava {
    private Path wd;
    private boolean deleteOnExit = false;
    private JavaTemplate template = new JavaTemplate();

    public CompileJava(Path wd){
        this.wd = wd;
    }

    public String extractClassName(String source){
        System.out.println(source);
        Pattern pattern = Pattern.compile("\\bclass\\s+(\\w+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public File generateJavaFile(String query, List<EPackage> metamodels) throws IOException{
        String classname = extractClassName(query);
        if(classname==null){
            return null;
        }

        String[] packages = metamodels.stream().map(epackage -> epackage.getName()).toArray(String[]::new);


        File file = new File(wd.toFile(), classname+".java");
        FileWriter writer = new FileWriter(file);
        writer.write(template.generateJavaCode(query, packages));
        writer.flush();
        writer.close();

        if(deleteOnExit){
            file.deleteOnExit();
        }
        return file;
    }

    public AbstractMap.SimpleEntry<File[],List<String>> compileJavaFile(File domainjar, File... files){
        var compilerOptions = Arrays.asList("-classpath", domainjar.getAbsolutePath() + File.pathSeparator + System.getProperty("java.class.path"));
        
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        var compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));

        CompilationTask task = compiler.getTask(null, fileManager, diagnostics, compilerOptions, null, compilationUnits);
        
        boolean success = task.call();
        
        try {
            fileManager.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        List<String> diag = diagnostics.getDiagnostics().stream().map(d ->
            String.format(
                "Error on line %d in %s: %s",
                d.getLineNumber(),
                d.getSource() == null ? "Unknown Source" : d.getSource().getName(),
                d.getMessage(Locale.getDefault())
            )
        ).toList();
        

        if(success){
            var compiled = new HashSet<File>();
            for(var file : files){
                for(var children : file.getParentFile().listFiles()){
                    if(children.isFile() && children.getName().endsWith(".class")){
                        compiled.add(children);
                    }
                }
            }
            return new AbstractMap.SimpleEntry<>(compiled.toArray(new File[compiled.size()]), diag);
        }
        return new AbstractMap.SimpleEntry<>(null, diag);
    }

    public void run(String classname, Function0<Object[]> args, File... files) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException{
        run(classname, null, null, () -> new Object[]{new String[]{}}, files);
    }

    public void run(String classname, Function1<Class<?>, Method> method, Function1<Class<?>, Object> instance, Function0<Object[]> args, File... files) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException{
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { wd.toUri().toURL() });
        Class<?> loadedClass = Class.forName(classname, true, classLoader);

        Method targetMethod = method!=null ? method.apply(loadedClass) : loadedClass.getMethod("main", String[].class);

        Object targetInstance = null;
        if(instance!=null){
            targetInstance = instance.apply(loadedClass);
        }
        
        targetMethod.invoke(targetInstance, args.apply()); // Cas
    }
}
