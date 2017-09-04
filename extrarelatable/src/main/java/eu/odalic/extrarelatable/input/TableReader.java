package eu.odalic.extrarelatable.input;

import java.io.InputStream;

import eu.odalic.extrarelatable.model.table.NestedListsParsedTable;

public interface TableReader {
	NestedListsParsedTable parse(final InputStream inputStream);
}
