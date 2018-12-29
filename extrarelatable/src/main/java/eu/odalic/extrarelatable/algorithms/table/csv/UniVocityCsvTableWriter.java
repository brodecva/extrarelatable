package eu.odalic.extrarelatable.algorithms.table.csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import eu.odalic.extrarelatable.model.table.ParsedTable;

/**
 * An implementation of {@link CsvTableWriter} using uniVocity library, its
 * {@link CsvWriter} class in particular.
 * 
 * @author Václav Brodec
 *
 */
@Component
public final class UniVocityCsvTableWriter implements CsvTableWriter {

	@Override
	public void write(File file, ParsedTable table) throws IOException {
		try (final FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			write(fileOutputStream, table);
		}
	}

	@Override
	public void write(OutputStream outputStream, ParsedTable table) throws IOException {
		final CsvWriterSettings csvWriterSettings = new com.univocity.parsers.csv.CsvWriterSettings();
		final CsvWriter csvWriter = new CsvWriter(outputStream, StandardCharsets.UTF_8, csvWriterSettings);

		csvWriter.writeHeaders(table.getHeaders());
		table.getRows().forEach(row -> csvWriter.writeRow(row.toArray(new String[row.size()])));

		csvWriter.flush();
	}
}
