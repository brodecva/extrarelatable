package eu.odalic.extrarelatable.model.table;

import java.util.List;

/**
 * A table which has already been parsed into logical cells containing the original text, grouped into table rows and columns. It can be accompanied by meta-data.
 * 
 * @author Václav Brodec
 *
 */
public interface ParsedTable {
	/**
	 * Provides the table headers. The list of headers is always present (as we operate on tables with at least one row) and it is always the first row.
	 * 
	 * @return the list of cells in the column headers
	 */
	List<String> getHeaders();

	/**
	 * @return the list of rows from top to bottom of the table in normal orientation
	 */
	List<List<String>> getRows();

	/**
	 * @return the list of columns from left to right of the table in normal orientation
	 */
	List<List<String>> getColumns();

	/**
	 * @return the table meta-data
	 */
	Metadata getMetadata();

	/**
	 * @return number of columns
	 */
	int getWidth();

	/**
	 * @return number of rows (including header)
	 */
	int getHeight();

	/**
	 * @param index zero-based index of the row
	 * @return the indexed row
	 */
	List<String> getRow(int index);

	/**
	 * @param index zero-based index of the column
	 * @return the indexed column
	 */
	List<String> getColumn(int index);
}