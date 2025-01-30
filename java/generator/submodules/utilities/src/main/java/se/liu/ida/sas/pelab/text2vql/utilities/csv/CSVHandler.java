package se.liu.ida.sas.pelab.text2vql.utilities.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class CSVHandler {
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
