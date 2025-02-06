package se.liu.ida.sas.pelab.text2vql.utilities.evaluation;

import java.util.Arrays;

public class ComparisonResult {
    private final String query;
    private final Boolean[] parse;

    private final Boolean[] compare;

    private final String[] message;
    private int count;
    public ComparisonResult(String query, int count){
        this.query = query;
        this.parse = new Boolean[count];
        this.compare = new Boolean[count];
        this.message = new String[count];
        for(int i=0; i<count; i++){
            parse[i] = false;
            compare[i] = true;
        }
        this.count = 0;
    }
    public void parsed(int index){
        parse[index] = true;
    }
    public void different(int index){
        compare[index] = false;
    }
    public void increment(int count){
        this.count += count;
    }
    public int getCount(){
        return count;
    }
    public String getParse(){
        return Arrays.toString(parse);
    }
    public String getCompare(){
        return Arrays.toString(compare);
    }
    public boolean getPass(){
        for(int i=0; i<parse.length; i++){
            if(parse[i] && compare[i]){
                return true;
            }
        }
        return false;
    }
}
