package eu.odalic.extrarelatable.services.dwtc;

import java.io.IOException;
import java.nio.file.Path;

public interface DwtcToCsvService {
	void convert(final Path input, final Path output) throws IOException;
}
