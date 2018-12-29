package eu.odalic.extrarelatable.algorithms.table.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.csv.Format;

/**
 * A CSV parser which reads the input into a {@link ParsedTable}.
 * 
 * @author Václav Brodec
 *
 */
public interface CsvTableParser {
	/**
	 * Parses the input string into a {@link ParsedTable}.
	 * 
	 * @param content CSV string
	 * @param format CSV format
	 * @param metadata CSV meta-data
	 * @return the parsed table
	 * @throws IOException whenever I/O exception occurs
	 */
	ParsedTable parse(String content, Format format, Metadata metadata) throws IOException;

	/**
	 * Reads into a {@link ParsedTable}.
	 * 
	 * @param reader character reader of CSV input
	 * @param format CSV format
	 * @param metadata CSV meta-data
	 * @return the parsed table
	 * @throws IOException whenever I/O exception occurs
	 */
	ParsedTable parse(Reader reader, Format format, Metadata metadata) throws IOException;

	/**
	 * Parses the input stream into a {@link ParsedTable}.
	 * 
	 * @param stream CSV input stream
	 * @param format CSV format
	 * @param metadata CSV meta-data
	 * @return the parsed table
	 * @throws IOException whenever I/O exception occurs
	 */
	ParsedTable parse(InputStream stream, Format format, Metadata metadata) throws IOException;
}
