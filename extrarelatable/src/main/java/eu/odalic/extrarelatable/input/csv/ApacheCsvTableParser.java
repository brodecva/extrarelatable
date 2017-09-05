package eu.odalic.extrarelatable.input.csv;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.odalic.extrarelatable.input.TableParser;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.NestedListsParsedTable;
import eu.odalic.extrarelatable.model.table.ParsedTable;

/**
 * <p>
 * An implementation of the {@link TableParser} handling CSV files and employing
 * Apache Commons CSV library.
 * </p>
 * 
 * <p>
 * Adapted from Odalic.
 * </p>
 *
 * @author Jan Váňa
 * @author Josef Janoušek
 */
@Immutable
@Component
public final class ApacheCsvTableParser implements TableParser {

	private final FormatAdapter apacheCsvFormatAdapter;

	@Autowired
	public ApacheCsvTableParser(final FormatAdapter apacheCsvFormatAdapter) {
		checkNotNull(apacheCsvFormatAdapter, "The apacheCsvFormatAdapter cannot be null!");

		this.apacheCsvFormatAdapter = apacheCsvFormatAdapter;
	}

	private void handleHeaders(final NestedListsParsedTable.Builder builder, final CSVParser parser) {
		final Map<String, Integer> headerMap = parser.getHeaderMap();

		for (final Map.Entry<String, Integer> headerEntry : headerMap.entrySet()) {
			builder.insertHeader(headerEntry.getKey(), headerEntry.getValue());
		}
	}

	private void handleInputRow(final NestedListsParsedTable.Builder builder, final CSVRecord row, final int rowIndex)
			throws IOException {
		if (!row.isConsistent()) {
			throw new IOException("CSV file is not consistent: data row with index " + rowIndex
					+ " has different size than header row.");
		}

		int column = 0;
		for (final String value : row) {
			builder.insertCell(value, rowIndex, column);
			column++;
		}
	}

	@Override
	public ParsedTable parse(final InputStream stream, final Format format, Metadata metadata) throws IOException {
		try (final Reader reader = new InputStreamReader(stream, format.getCharset())) {
			return parse(reader, format, metadata);
		}
	}

	@Override
	public ParsedTable parse(final Reader reader, final Format format, Metadata metadata) throws IOException {
		final CSVFormat internalFormat = this.apacheCsvFormatAdapter.convert(format);
		final CSVParser parser = internalFormat.parse(reader);

		final NestedListsParsedTable.Builder builder = NestedListsParsedTable.builder();

		handleHeaders(builder, parser);

		int row = 0;
		for (final CSVRecord record : parser) {
			handleInputRow(builder, record, row);
			row++;
		}

		if (row == 0) {
			throw new IOException("There are no data rows in the CSV file.");
		}

		return builder.build();
	}

	@Override
	public ParsedTable parse(final String content, final Format format, Metadata metadata) throws IOException {
		try (Reader reader = new StringReader(content)) {
			return parse(reader, format, metadata);
		}
	}
}
