package se.liu.ida.sas.pelab.text2vql.ocl;

public class DataException extends RuntimeException{
    public DataException(Object obj){
        super("Unsupported data type of "+obj.getClass());
    }
}
