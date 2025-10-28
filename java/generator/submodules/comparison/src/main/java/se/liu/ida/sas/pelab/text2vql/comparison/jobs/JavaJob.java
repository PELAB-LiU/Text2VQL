package se.liu.ida.sas.pelab.text2vql.comparison.jobs;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;
import se.liu.ida.sas.pelab.text2vql.java.ConvertingCopier;
import se.liu.ida.sas.pelab.text2vql.java.StatelessSyntaxCheckJava;
import se.liu.ida.sas.pelab.text2vql.java.compiler.CompileJava;
import se.liu.ida.sas.pelab.text2vql.java.compiler.CompileJava.CompilerOutput;

public class JavaJob implements Job {
    private Query query;
    private URL domainJar;
    private CompilerOutput compiled;
    private Syntax syntax;
    private URLClassLoader classLoader;
    private Class<?> mainclass;
    private Method method;
    private boolean isTuple;
    private Field[] fields;
    private Resource resource;
    private ConvertingCopier trace2;

    public JavaJob(Resource metamodel, Query query, File domainJar) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        this.query = query;
        StatelessSyntaxCheckJava checker = new StatelessSyntaxCheckJava(){};
        this.compiled = checker.parse(query.query(), metamodel, domainJar);
        if(!syntaxOk()){
            this.syntax = new Syntax(false, compiled.diagnostics().toArray(String[]::new));
            return;
        }
        this.syntax = new Syntax(true, compiled.diagnostics().toArray(String[]::new));

        this.domainJar = domainJar!=null ? domainJar.toURI().toURL() : null;
        this.resource = new ResourceImpl();

        classLoader = domainJar!=null ?
            URLClassLoader.newInstance(new URL[] { compiled.wd().toURI().toURL(), this.domainJar }):
            URLClassLoader.newInstance(new URL[] { compiled.wd().toURI().toURL() });

        trace2 = new ConvertingCopier(classLoader, metamodel);

        String mainclassname = CompileJava.extractClassName(query.query());
        mainclass = Class.forName(mainclassname, true, classLoader);
        method = mainclass.getMethod(query.entry(), Resource.class);

        Class<?> returnType = method.getReturnType();
        if(Collection.class.isAssignableFrom(returnType)){
            Type genericReturnType = method.getGenericReturnType();
            if(genericReturnType instanceof ParameterizedType pt){
                Type[] args = pt.getActualTypeArguments();

                Type dataType = args[0];
                isTuple = isTupleClass(dataType);
                
                fields = ((Class<?>) dataType).getDeclaredFields();
                Arrays.sort(fields, (f1, f2) -> f1.getName().compareTo(f2.getName()));

            }
        } else {
            isTuple = false; 
            // For now, I'm lazy and will assume that the query does not return a tuple directly. 
        }
    }
    public boolean syntaxOk(){
        return !(this.compiled==null || this.compiled.classFiles()==null || this.compiled.classFiles().length==0);
    }
    
    //private Copier trace = new Copier();
    private Map<EObject, EObject> reverse = new HashMap<>();
    public void configureInstanceModel(EObject model){
       if(!syntaxOk()){
            return;
        }

        trace2.clear();
        reverse.clear();
        EObject instanceModel = trace2.copy(model);
        trace2.copyReferences();
        trace2.entrySet().forEach(it -> reverse.put(it.getValue(), it.getKey()));
        resource.getContents().clear();
        resource.getContents().add(instanceModel);
    }
    @Override
    public List<String> call() throws Exception { 
        if(!syntaxOk()){
            return null;
        }


        Object instance = mainclass.getDeclaredConstructor().newInstance();
        Object result = method.invoke(instance, resource);
        List<String> matches =  processResult(result);
        Collections.sort(matches);
        return matches;
    }

    private List<String> processResult(Object object){
        List<String> matches = new LinkedList<>();
        if(object instanceof Collection<?> collection){
            collection.forEach(entry -> {            
                matches.add(processSingleResult(entry));
            }); 
        } /*else if (object.getClass().isArray()){
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++) {
                matches.add(processSingleResult(Array.get(object, i)));
            }
        } */else {
            //System.out.println("It is... it is... It is green.");
            matches.add(processSingleResult(object));
        }
        return matches;
    }
    private String processSingleResult(Object match){
        if(isTuple){
            try {
                StringBuilder builder = new StringBuilder();
                builder.append("M");
            
                for(Field field : fields) {
                    field.setAccessible(true); // Would be nicer to use getters, but also more failure points are expected.
                    Object fieldValue = field.get(match);
                    builder.append('_').append(processValue(fieldValue));
                }
                return builder.toString();
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                return "E_"+e.getMessage();
            }
        } else {
            // There can be a problem if the condition is a global true/false.
            // Viatra might use a query like pattern foo(), wich would be mepped to M
            // Java might map the same to either M_ or M_true/M_false
            // Possibly fix at java
            return "M_"+processValue(match);
        }
    }
    private String processValue(Object value){
        if(value instanceof EObject eobj){
            return "@"+reverse.get(eobj).hashCode();
        } else {
            return Objects.toString(value);
        }
    }
    // A type is tuple class if it was loaded by the local classloader and not a class from the domain jar
    private boolean isTupleClass(Type type){
        if(type instanceof Class<?> clazz){
            ClassLoader valueClassCloader = clazz.getClassLoader();
            if(valueClassCloader==null || this.classLoader != valueClassCloader){
                return false; // Not loaded with the local classloader (Tuple is defined in the query class and loaded later)
            }

            CodeSource source = clazz.getProtectionDomain().getCodeSource();
            if(source==null || source.getLocation()==null){
                return false; // Built-in class or generated (?)
            } else {
                URL location = source.getLocation();
                return !location.equals(domainJar); // Class loading happens with the generated Query class files and a domain jar.
            }
        } else {
            throw new RuntimeException("Type is expected to be a Class. Not "+type.getTypeName());
        }
    }
    public void dispose(){
        /*if(engine!=null && !engine.isDisposed()){
            engine.dispose();
            engine = null;
        }*/
    }
    @Override
    public boolean hasSyntaxError() {
        return !syntaxOk();
    }
    @Override
    public Query getQuery() {
        return this.query;
    }

    @Override
    public Syntax getSyntaxResult() {
        return this.syntax;
    }
}
