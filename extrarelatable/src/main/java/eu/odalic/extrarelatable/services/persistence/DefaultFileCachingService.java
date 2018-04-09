package eu.odalic.extrarelatable.services.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

@Service
public class DefaultFileCachingService implements FileCachingService {

	private static final String PREFIX = "extrarelatable";

	@Override
	public Path cache(final InputStream input) throws IOException {
		final Path temporaryFilePath = Files.createTempFile(PREFIX, null);
		
		Files.copy(input, temporaryFilePath);
		
		temporaryFilePath.toFile().deleteOnExit();
		
		return temporaryFilePath;
	}
	
	@Override
	public Path provideTemporaryFile() throws IOException {
		final Path temporaryFilePath = Files.createTempFile(PREFIX, null);
		temporaryFilePath.toFile().deleteOnExit();
				
		return temporaryFilePath;
	}
}