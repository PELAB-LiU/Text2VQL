package se.liu.ida.sas.pelab.text2vql.utilities.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class CSVHandler {
    private final CSVPrinter output;
    private final String[] columns;
    private final Map<String, Object> row = new HashMap<>();
    public CSVHandler(File file, String... columns) throws IOException {
        this.columns = columns;
        var format = CSVFormat.RFC4180.withDelimiter(',');
        if(file==null){
            output = format.print(System.out);
        } else {
            output = format.print(new FileWriter(file));
        }
        output.printRecord((Object[]) columns);
        output.flush();
    }
    public Object put(String key, Object object){
        return row.put(key,object);
    }
    public void commit() {
        String[] record = new String[columns.length];
        for(int i=0; i< record.length; i++){
            record[i] = Objects.toString(row.get(columns[i]));
        }
        try {
            output.printRecord((Object[]) record);
            output.flush();
            row.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Iterable<CSVRecord> loadCSV(File path) throws IOException {
        return new Iterable<CSVRecord>(){
            @Override
            public Iterator<CSVRecord> iterator() {
                return new FlatteningIterator(path);
            }
        };
    }
    static class FlatteningIterator implements Iterator<CSVRecord>{
        Iterator<CSVRecord> iterator;
        Queue<File> csvs;
        FlatteningIterator(File file){
            csvs = new LinkedList<File>();
            if(file.isDirectory()){
                csvs.addAll(Arrays.asList(file.listFiles(csv -> csv.getName().endsWith(".csv"))));
            } else {
                csvs.add(file);
            }
        }
        @Override
        public boolean hasNext() {
            try {
                while(load()){
                    if(iterator.hasNext()){
                        return true;
                    }
                }
                return false;
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        @Override
        public CSVRecord next() {
            try {
                while(load()){
                    if(iterator.hasNext()){
                        return iterator.next();
                    }
                }
                return null;
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        private boolean load() throws IOException {
            if(iterator==null || !iterator.hasNext()){
                var csv = csvs.poll();
                if(csv==null){
                    return false;
                }
                iterator = CSVFormat.RFC4180
                        .withDelimiter(',')
                        .withFirstRecordAsHeader()
                        .parse(new FileReader(csv)).iterator();
            }
            return true;
        }
    }
}
