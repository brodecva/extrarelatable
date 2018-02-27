package eu.odalic.extrarelatable.algorithms.table.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.csv.Format;

public interface CsvTableParser {
	ParsedTable parse(String content, Format format, Metadata metadata) throws IOException;

	ParsedTable parse(Reader reader, Format format, Metadata metadata) throws IOException;

	ParsedTable parse(InputStream stream, Format format, Metadata metadata) throws IOException;
}
