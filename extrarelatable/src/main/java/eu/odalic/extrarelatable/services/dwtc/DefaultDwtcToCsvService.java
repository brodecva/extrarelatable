package eu.odalic.extrarelatable.services.dwtc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import webreduce.data.Dataset;

@Service
public class DefaultDwtcToCsvService implements DwtcToCsvService {

	@Override
	public void convert(Path input, Path output) throws IOException {
		final Dataset dataset;
		try (final InputStream datasetInputStream = Files.newInputStream(input, StandardOpenOption.READ)) {
			dataset = webreduce.data.Dataset.fromJson(datasetInputStream);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to read " + input + "!", e);
		}
		
		Files.write(output, Arrays.stream(dataset.getRelation()).map(row -> Joiner.on(",").join(row)).collect(ImmutableList.toImmutableList()), StandardCharsets.UTF_8, StandardOpenOption.WRITE);
	}

}
