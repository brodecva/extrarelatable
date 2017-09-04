package eu.odalic.extrarelatable.model.table;

import java.util.List;

public interface ParsedTable {

	List<String> getHeaders();

	List<List<String>> getRows();

	List<List<String>> getColumns();

	Metadata getMetadata();

	int getWidth();

	int getHeight();

	List<String> getRow(int index);

	List<String> getColumn(int index);

}