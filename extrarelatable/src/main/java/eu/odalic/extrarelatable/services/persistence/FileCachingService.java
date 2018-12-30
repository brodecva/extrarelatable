package eu.odalic.extrarelatable.services.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * General simple file caching service.
 * 
 * @author Václav Brodec
 *
 */
public interface FileCachingService {

	/**
	 * Caches the content of the input stream as file accessible on the returned
	 * path.
	 * 
	 * @param input
	 *            cached input stream
	 * @return file cache
	 * @throws IOException
	 *             whenever I/O exception occurs
	 */
	Path cache(InputStream input) throws IOException;

	/**
	 * Creates a temporary file and returns its path.
	 * 
	 * @return path to the newly created temporary file
	 * @throws IOException
	 *             whenever I/O exception occurs
	 */
	Path provideTemporaryFile() throws IOException;

}
