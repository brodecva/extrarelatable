package eu.odalic.extrarelatable.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import eu.odalic.extrarelatable.input.csv.Format;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.ParsedTable;

public interface TableParser {
	ParsedTable parse(final String content, final Format format, Metadata metadata) throws IOException;

	ParsedTable parse(final Reader reader, final Format format, Metadata metadata) throws IOException;

	ParsedTable parse(final InputStream stream, final Format format, Metadata metadata) throws IOException;
}
