package se.liu.ida.sas.pelab.querycomparison;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;

@SuppressWarnings("all")
public class CSVHandler {
  private final HashMap<Object, Object> record = CollectionLiterals.<Object, Object>newHashMap();

  private final CSVPrinter output;

  public static final String OUT_ID = "id";

  public static final String OUT_CORRECT = "has_correct";

  public static final String OUT_BASEMATCHCOUNT = "#truth";

  public static final String OUT_SYNT = "syntax_ok";

  public static final String OUT_MATCH = "match_ok";

  public static Iterable<CSVRecord> parse(final String file) {
    try {
      CSVFormat _withFirstRecordAsHeader = CSVFormat.RFC4180.withDelimiter(',').withFirstRecordAsHeader();
      FileReader _fileReader = new FileReader(file);
      final Iterable<CSVRecord> records = _withFirstRecordAsHeader.parse(_fileReader);
      return records;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  public CSVHandler(final String file) {
    try {
      final CSVFormat format = CSVFormat.RFC4180.withDelimiter(',');
      if ((file == null)) {
        this.output = format.print(System.out);
      } else {
        FileWriter _fileWriter = new FileWriter(file);
        this.output = format.print(_fileWriter);
      }
      this.output.printRecord(
        CSVHandler.OUT_ID, 
        CSVHandler.OUT_CORRECT, 
        CSVHandler.OUT_BASEMATCHCOUNT, 
        CSVHandler.OUT_SYNT, 
        CSVHandler.OUT_MATCH);
      this.output.flush();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  public Object put(final String column, final Object value) {
    return this.record.put(column, value.toString());
  }

  public void commit() {
    try {
      this.output.printRecord(
        this.record.get(CSVHandler.OUT_ID), 
        this.record.get(CSVHandler.OUT_CORRECT), 
        this.record.get(CSVHandler.OUT_BASEMATCHCOUNT), 
        this.record.get(CSVHandler.OUT_SYNT), 
        this.record.get(CSVHandler.OUT_MATCH));
      this.record.clear();
      this.output.flush();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  public void close() {
    try {
      this.output.close(true);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
