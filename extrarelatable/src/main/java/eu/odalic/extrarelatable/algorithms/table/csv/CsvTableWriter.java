package eu.odalic.extrarelatable.algorithms.table.csv;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import eu.odalic.extrarelatable.model.table.ParsedTable;

/**
 * Writes {@link ParsedTable} back as CSV. The specific format is arbitrary,
 * depending on the implementation, but it should be easy to parse by common
 * available parsers.
 * 
 * @author Václav Brodec
 *
 */
public interface CsvTableWriter {
	/**
	 * Writes the table to output file.
	 * 
	 * @param file
	 *            output file
	 * @param table
	 *            the input table
	 * @throws IOException
	 *             whenever I/O exception occurs
	 */
	void write(File file, ParsedTable table) throws IOException;

	/**
	 * Writes the table to output stream.
	 * 
	 * @param stream
	 *            output stream
	 * @param table
	 *            the input table
	 * @throws IOException
	 *             whenever I/O exception occurs
	 */
	void write(OutputStream stream, ParsedTable table) throws IOException;
}
