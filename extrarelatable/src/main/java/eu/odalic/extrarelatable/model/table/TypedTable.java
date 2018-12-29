package eu.odalic.extrarelatable.model.table;

import java.util.List;

import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.Value;

/**
 * The {@link ParsedTable} where each cell has been converted to a typed value ({@link Value})in best-effort manner. 
 * 
 * @author Václav Brodec
 *
 */
public interface TypedTable {
	/**
	 * @return the typed headers
	 */
	List<Label> getHeaders();

	/**
	 * @return the typed list of rows
	 */
	List<List<Value>> getRows();

	/**
	 * @return the typed list of columns
	 */
	List<List<Value>> getColumns();

	/**
	 * @return the table meta-data
	 */
	Metadata getMetadata();

	/**
	 * @return the number of columns
	 */
	int getWidth();

	/**
	 * @return the number of rows
	 */
	int getHeight();

	/**
	 * @param index zero-based index of the row
	 * @return the indexed typed row
	 */
	List<Value> getRow(int index);

	/**
	 * @param index zero-based index of the column
	 * @return the indexed typed column
	 */
	List<Value> getColumn(int index);
}