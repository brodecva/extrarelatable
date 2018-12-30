package eu.odalic.extrarelatable.services.dwtc;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Converts table kept int the DWTC JSON-based format to CSV.
 * 
 * @author Václav Brodec
 *
 */
public interface DwtcToCsvService {
	/**
	 * Converts between the table formats and stores the result to a file.
	 * 
	 * @param input
	 *            file path to the table in DWTC JSON-based format
	 * @param output
	 *            out file path where the converted table (to CSV) is to be stored
	 * @throws IOException
	 *             whenever I/O exception occurs
	 */
	void convert(final Path input, final Path output) throws IOException;
}
