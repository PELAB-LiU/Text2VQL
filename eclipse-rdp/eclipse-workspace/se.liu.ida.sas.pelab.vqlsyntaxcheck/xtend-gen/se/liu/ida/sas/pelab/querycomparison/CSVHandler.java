package se.liu.ida.sas.pelab.querycomparison;

import java.util.HashMap;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@SuppressWarnings("all")
public class CSVHandler {
  private final HashMap<Object, Object> record = CollectionLiterals.<Object, Object>newHashMap();

  private final /* CSVPrinter */Object output;

  public static final String OUT_ID = "id";

  public static final String OUT_CORRECT = "has_correct";

  public static final String OUT_BASEMATCHCOUNT = "#truth";

  public static final String OUT_SYNT = "syntax_ok";

  public static final String OUT_MATCH = "match_ok";

  public static /* Iterable<CSVRecord> */Object parse(final String file) {
    throw new Error("Unresolved compilation problems:"
      + "\nCSVRecord cannot be resolved to a type."
      + "\nThe method or field CSVFormat is undefined"
      + "\nRFC4180 cannot be resolved"
      + "\nwithDelimiter cannot be resolved"
      + "\nwithFirstRecordAsHeader cannot be resolved"
      + "\nparse cannot be resolved");
  }

  public CSVHandler(final String file) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field CSVFormat is undefined"
      + "\nThe field CSVHandler.output refers to the missing type CSVPrinter"
      + "\nThe field CSVHandler.output refers to the missing type CSVPrinter"
      + "\nThe field CSVHandler.output refers to the missing type CSVPrinter"
      + "\nThe field CSVHandler.output refers to the missing type CSVPrinter"
      + "\nRFC4180 cannot be resolved"
      + "\nwithDelimiter cannot be resolved"
      + "\nprint cannot be resolved"
      + "\nprint cannot be resolved"
      + "\nprintRecord cannot be resolved"
      + "\nflush cannot be resolved");
  }

  public Object put(final String column, final Object value) {
    return this.record.put(column, value.toString());
  }

  public Object commit() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe field CSVHandler.output refers to the missing type CSVPrinter"
      + "\nThe field CSVHandler.output refers to the missing type CSVPrinter"
      + "\nprintRecord cannot be resolved"
      + "\nflush cannot be resolved");
  }

  public Object close() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe field CSVHandler.output refers to the missing type CSVPrinter"
      + "\nclose cannot be resolved");
  }
}
