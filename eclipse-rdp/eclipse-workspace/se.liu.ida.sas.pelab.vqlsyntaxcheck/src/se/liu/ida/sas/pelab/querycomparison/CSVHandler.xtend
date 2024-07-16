package se.liu.ida.sas.pelab.querycomparison

import java.io.FileReader
import java.io.FileWriter
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.apache.commons.csv.CSVPrinter

class CSVHandler {
	val record = newHashMap
	val CSVPrinter output;
	public static val OUT_ID = "id"
	public static val OUT_CORRECT = "has_correct"
	public static val OUT_BASEMATCHCOUNT = "#truth"
	public static val OUT_SYNT = "syntax_ok"
	public static val OUT_MATCH = "match_ok"
	
	static def Iterable<CSVRecord> parse(String file){
		val Iterable<CSVRecord> records = CSVFormat.RFC4180
			.withDelimiter(',')
			.withFirstRecordAsHeader()
			.parse(new FileReader(file));

		return records
	}
	
	new (String file){
		val format = CSVFormat.RFC4180
			.withDelimiter(',')
		if(file===null){
			output = format.print(System.out)
		} else {
			output = format.print(new FileWriter(file))
		}
		
		output.printRecord(
			OUT_ID,
			OUT_CORRECT,
			OUT_BASEMATCHCOUNT,
			OUT_SYNT,
			OUT_MATCH
		)
		output.flush
			
	}
	def put(String column, Object value){
		record.put(column, value.toString)
	}
	def commit(){
		output.printRecord(
			record.get(OUT_ID),
			record.get(OUT_CORRECT),
			record.get(OUT_BASEMATCHCOUNT),
			record.get(OUT_SYNT),
			record.get(OUT_MATCH)
		)
		record.clear
		output.flush
	}
	def close(){
		output.close(true)
	}
	
}