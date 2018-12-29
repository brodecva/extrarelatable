package eu.odalic.extrarelatable.algorithms.table;

import java.util.Map;

import eu.odalic.extrarelatable.model.bag.Type;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;

/**
 * Divides each input typed table ({@link TypedTable}) and presents the result
 * as {@link SlicedTable}.
 * 
 * @author Václav Brodec
 *
 */
public interface TableSlicer {
	/**
	 * Determines the predominant type in each column based on the threshold and
	 * sorts that column according to the type as either numeric column or a
	 * column providing row context. This information is added to the original
	 * typed table and presented as {@link SlicedTable}.
	 * 
	 * @param threshold
	 *            number between 0 and 1 (both including) that serves as the
	 *            relative threshold from the total number of cells in a column that
	 *            must be passed in order to consider one data type of cells to be
	 *            predominant in the column
	 * @param table
	 *            input table
	 * @return the sliced table
	 */
	SlicedTable slice(double threshold, TypedTable table);

	/**
	 * Determines the predominant type in each column based on the default
	 * threshold and sorts that column according to the type as either
	 * numeric column or a column providing row context. This information
	 * is added to the original typed table and presented as
	 * {@link SlicedTable}.
	 * 
	 * @param table
	 *            input table
	 * @return the sliced table
	 */
	SlicedTable slice(TypedTable table);

	/**
	 * Determines the predominant type in each column based on the threshold and
	 * sorts that column according to the type as either numeric column or a
	 * column providing row context. This information is added to the original
	 * typed table and presented as {@link SlicedTable}.
	 * 
	 * @param threshold
	 *            number between 0 and 1 (both including) that serves as the
	 *            relative threshold from the total number of cells in a column that
	 *            must be passed in order to consider one data type of cells to be
	 *            predominant in the column
	 * @param table
	 *            input table
	 * @param columnTypeHints
	 *            overriding hints for the column types
	 * @return the sliced table
	 */
	SlicedTable slice(double threshold, TypedTable table, Map<? extends Integer, ? extends Type> columnTypeHints);

	/**
	 * Determines the predominant type in each column based on the default
	 * threshold and sorts that column according to the type as either
	 * numeric column or a column providing row context. This information
	 * is added to the original typed table and presented as
	 * {@link SlicedTable}.
	 * 
	 * @param table
	 *            input table
	 * @param columnTypeHints
	 *            overriding hints for the column types
	 * @return the sliced table
	 */
	SlicedTable slice(TypedTable table, Map<? extends Integer, ? extends Type> columnTypeHints);
}
