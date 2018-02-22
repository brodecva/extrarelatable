package eu.odalic.extrarelatable.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface FileCachingService {

	Path cache(InputStream input) throws IOException;

}
