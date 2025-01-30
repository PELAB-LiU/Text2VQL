package se.liu.ida.sas.pelab.text2vql.utilities.evaluation;

public class AbortFlag {
    private boolean abort = false;
    public void abort(){abort=true;}
    public void reset(){abort=false;}
    public boolean shouldContinue(){return !abort;}
    public boolean shouldAbort(){return abort;}

    public static AbortFlag[] of(int count){
        AbortFlag[] flags = new AbortFlag[count];
        for (int i=0; i<count; i++){
            flags[i] = new AbortFlag();
        }
        return flags;
    }
}
