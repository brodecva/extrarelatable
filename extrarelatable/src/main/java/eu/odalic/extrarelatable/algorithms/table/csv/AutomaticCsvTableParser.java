package eu.odalic.extrarelatable.algorithms.table.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import javax.annotation.concurrent.Immutable;

import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.NestedListsParsedTable;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.csv.Format;

/**
 * <p>
 * An implementation of the {@link CsvTableParser} using
 * <a href="https://github.com/uniVocity/univocity-parsers"> UniVocity
 * parsers</a> and <a href="http://tika.apache.org/"> to automate format
 * detection.
 * </p>
 * 
 * <p>
 * Requires the input streams to support mark and reset, this usually means that
 * the stream should be buffered.
 * </p>
 *
 *
 * @author Václav Brodec
 */
@Immutable
@Component("automatic")
public final class AutomaticCsvTableParser implements CsvTableParser {

	public AutomaticCsvTableParser() {
	}

	@Override
	public ParsedTable parse(final InputStream stream, final Format format, Metadata metadata) throws IOException {
		if (format == null) {
			final CharsetDetector charsetDetector = new CharsetDetector();
			charsetDetector.setText(stream);
			final CharsetMatch[] charsetMatches = charsetDetector.detectAll();
			
			for (final CharsetMatch match : charsetMatches) {
				try (final Reader reader = new InputStreamReader(stream, match.getName())) {
					return parse(reader, format, metadata);
				} catch (final UnsupportedEncodingException e) {
				}
			}
			
			try (final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
				return parse(reader, format, metadata);
			}
		} else {
			try (final Reader reader = new InputStreamReader(stream, format.getCharset())) {
				return parse(reader, format, metadata);
			}
		}
	}

	@Override
	public ParsedTable parse(final Reader reader, final Format format, Metadata metadata) throws IOException {
		final CsvParserSettings parserSettings = new CsvParserSettings();

		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.setDelimiterDetectionEnabled(true);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setEmptyValue("");
		parserSettings.setNullValue("");
		parserSettings.setMaxCharsPerColumn(-1);

		final RowListProcessor rowProcessor = new RowListProcessor();
		parserSettings.setProcessor(rowProcessor);

		final CsvParser parser = new CsvParser(parserSettings);
		parser.parse(reader);

		final String[] headers = rowProcessor.getHeaders();
		final List<String[]> rows = rowProcessor.getRows();
		
		final int maximumCellsPerRow = rows.stream().reduce(0, (u, r) -> Math.max(u, r.length), (u, v) -> Math.max(u, v));
		final int width = Math.max(headers.length, maximumCellsPerRow);
		
		final List<String> headersList = toRowList(headers, width);
		
		return NestedListsParsedTable.fromRows(
			headersList,
			rows.stream().map(row -> toRowList(row, width)).collect(ImmutableList.toImmutableList()),
			metadata
		);
	}

	private static List<String> toRowList(final String[] rowArray, final int tableWidth) {
		final ImmutableList.Builder<String> builder = ImmutableList.builder();
		builder.add(rowArray);
		builder.addAll(Collections.nCopies(tableWidth - rowArray.length, ""));
		return builder.build();
	}

	@Override
	public ParsedTable parse(final String content, final Format format, Metadata metadata) throws IOException {
		try (Reader reader = new StringReader(content)) {
			return parse(reader, format, metadata);
		}
	}
}
