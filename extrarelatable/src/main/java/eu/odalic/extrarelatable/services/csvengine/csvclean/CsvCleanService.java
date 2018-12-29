package eu.odalic.extrarelatable.services.csvengine.csvclean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Cleans the input CSV files (mainly adds missing headers, normalizes number of columns and converts to uniform format).
 * 
 * @author Václav Brodec
 *
 */
public interface CsvCleanService {
	/**
	 * Cleans the CSV file.
	 * 
	 * @param file
	 *            CSV file to clean
	 * @return cleaned input stream containing CSV data
	 * @throws IOException
	 *             whenever I/O exception occurs
	 * @throws IllegalStateException
	 *             when the request to clean the file has been successfully
	 *             executed, but the cleaning itself failed
	 */
	InputStream clean(File file) throws IOException;
}
