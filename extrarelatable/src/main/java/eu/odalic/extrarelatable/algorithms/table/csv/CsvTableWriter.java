package eu.odalic.extrarelatable.algorithms.table.csv;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import eu.odalic.extrarelatable.model.table.ParsedTable;

public interface CsvTableWriter {
	void write(File file, ParsedTable table) throws IOException;

	void write(OutputStream stream, ParsedTable table) throws IOException;
}
