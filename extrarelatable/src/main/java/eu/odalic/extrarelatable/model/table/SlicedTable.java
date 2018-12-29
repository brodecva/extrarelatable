package eu.odalic.extrarelatable.model.table;

import java.util.List;
import java.util.Map;

import eu.odalic.extrarelatable.model.bag.Value;

/**
 * Extends the {@link TypedTable} by providing information on which columns
 * contain number-like data and which columns will serve as the context columns
 * for that data.
 * 
 * @author Václav Brodec
 *
 */
public interface SlicedTable extends TypedTable {
	/**
	 * Indicates which columns are marked to contain numric data and allows their instant retrieval.
	 * 
	 * @return map of column indices to list of cells in the indexed numeric column
	 */
	Map<Integer, List<Value>> getDataColumns();

	/**
	 * Indicates which columns are marked to contain context data and allows their instant retrieval.
	 * 
	 * @return map of column indices to list of cells in the indexed context column
	 */
	Map<Integer, List<Value>> getContextColumns();
}