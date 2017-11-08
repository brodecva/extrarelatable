package eu.odalic.extrarelatable.services.csvengine.csvclean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface CsvCleanService {
	/**
	 * Cleans the the CSV file.
	 * 
	 * @param file file
	 * @return cleaned input stream
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	InputStream clean(File file) throws IOException;
}
