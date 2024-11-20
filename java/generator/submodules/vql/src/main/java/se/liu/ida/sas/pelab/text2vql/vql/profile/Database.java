package se.liu.ida.sas.pelab.text2vql.vql.profile;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private Map<ProfileEntry, Integer> data = new HashMap<>();
    public void increment(ProfileEntry key){
        data.putIfAbsent(key,0);
        data.put(key,data.get(key)+1);
    }
    public void decrement(ProfileEntry key){
        data.putIfAbsent(key,0);
        data.put(key,data.get(key)-1);
    }
    public int get(ProfileEntry key){
        return data.getOrDefault(key,0);
    }
    public int get(DerivedProfileEntry key){
        return key.operation.get(this);
    }
}
