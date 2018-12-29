package eu.odalic.extrarelatable.services.dwtc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import eu.odalic.extrarelatable.util.Matrix;
import webreduce.data.Dataset;

/**
 * Default implementation of {@link DwtcToCsvService}, which uses the
 * referential DWTC parsing library and uniVocity parsers to write the CSVs.
 * 
 * @author Václav Brodec
 *
 */
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

		final List<List<String>> listBackedRelation = Arrays.stream(dataset.getRelation())
				.map(row -> ImmutableList.copyOf(row)).collect(ImmutableList.toImmutableList());
		final List<List<String>> transposedRelation = Matrix.transpose(listBackedRelation); // DWTC keeps the tables transposed.

		final CsvWriter writer = new CsvWriter(output.toFile(), new CsvWriterSettings());

		writer.writeRowsAndClose(transposedRelation.stream().map(row -> row.toArray(new String[row.size()]))
				.collect(ImmutableList.toImmutableList()));
	}

}
