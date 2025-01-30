package se.liu.ida.sas.pelab.text2vql.utilities.evaluation;

public class ComparisonResult {
    private final String query;
    private final Boolean[] parse;

    private final Boolean[] compare;

    private final String[] message;
    public ComparisonResult(String query, int count){
        this.query = query;
        this.parse = new Boolean[count];
        this.compare = new Boolean[count];
        this.message = new String[count];
        for(int i=0; i<count; i++){
            parse[i] = false;
            compare[i] = true;
        }
    }
    public void parsed(int index){
        parse[index] = true;
    }
    public void matches(int index){
        compare[index] = true;
    }
}
