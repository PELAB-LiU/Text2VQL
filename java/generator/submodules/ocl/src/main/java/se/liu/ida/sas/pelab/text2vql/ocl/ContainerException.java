package se.liu.ida.sas.pelab.text2vql.ocl;

public class ContainerException extends RuntimeException{
    public ContainerException(Object obj){
        super("Unsupported container type of "+obj.getClass());
    }
}
