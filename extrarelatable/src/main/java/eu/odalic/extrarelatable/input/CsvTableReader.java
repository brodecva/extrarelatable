package eu.odalic.extrarelatable.input;

import java.io.InputStream;

import org.springframework.stereotype.Component;

import eu.odalic.extrarelatable.model.table.NestedListsParsedTable;

@Component
public final class CsvTableReader implements TableReader {

	public CsvTableReader() {
	}
	
	@Override
	public NestedListsParsedTable parse(InputStream inputStream) {
		// TODO Implement.
		return null;
	}

}
