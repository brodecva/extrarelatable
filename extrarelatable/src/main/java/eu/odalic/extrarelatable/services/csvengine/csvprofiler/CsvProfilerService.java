package eu.odalic.extrarelatable.services.csvengine.csvprofiler;

import java.io.File;
import java.io.IOException;

public interface CsvProfilerService {
	/**
	 * Profile the CSV file.
	 * 
	 * @param file profiled file
	 * @return profile
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	CsvProfile profile(File file) throws IOException;
}
