package eu.odalic.extrarelatable.services.csvengine.csvprofiler;

import java.io.File;
import java.io.IOException;

/**
 * Service that obtains profiles (including data types of the columns) of input
 * CSV files.
 * 
 * @author Václav Brodec
 *
 */
public interface CsvProfilerService {
	/**
	 * Profile the CSV file.
	 * 
	 * @param file
	 *            profiled CSV file
	 * @return profile
	 * @throws IOException
	 *             whenever I/O exception occurs
	 * @throws IllegalStateException
	 *             when the request has been successfully made, but the profiling
	 *             itself failed
	 */
	CsvProfile profile(File file) throws IOException;
}
