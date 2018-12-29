package eu.odalic.extrarelatable.algorithms.table;

import eu.odalic.extrarelatable.model.table.TypedTable;

/**
 * Provides a score indicating the percentage to which a table column conforms
 * to a particular data type.
 * 
 * @author Václav Brodec
 *
 */
public interface ColumnTypeAnalyzer {
	/**
	 * Provides a score indicating the percentage to which the column conforms to
	 * the {@link eu.odalic.extrarelatable.model.bag.NumericValue} data type.
	 * 
	 * @param columnIndex
	 *            index of the column in question
	 * @param table
	 *            examined typed table
	 * @return the score
	 */
	double isNumeric(int columnIndex, TypedTable table);

	/**
	 * Provides a score indicating the percentage to which the column conforms to
	 * the {@link eu.odalic.extrarelatable.model.bag.TextValue} data type.
	 * 
	 * @param columnIndex
	 *            index of the column in question
	 * @param table
	 *            examined typed table
	 * @return the score
	 */
	double isTextual(int columnIndex, TypedTable table);

	/**
	 * Provides a score indicating the percentage to which the column conforms to
	 * the {@link eu.odalic.extrarelatable.model.bag.InstantValue} data type.
	 * 
	 * @param columnIndex
	 *            index of the column in question
	 * @param table
	 *            examined typed table
	 * @return the score
	 */
	double isInstant(int columnIndex, TypedTable table);

	/**
	 * Provides a score indicating the percentage to which the column conforms to
	 * the {@link eu.odalic.extrarelatable.model.bag.EntityValue} data type.
	 * 
	 * @param columnIndex
	 *            index of the column in question
	 * @param table
	 *            examined typed table
	 * @return the score
	 */
	double isEntity(int columnIndex, TypedTable table);

	/**
	 * Provides a score indicating the percentage to which the column conforms to
	 * the {@link eu.odalic.extrarelatable.model.bag.IdValue} data type.
	 * 
	 * @param columnIndex
	 *            index of the column in question
	 * @param table
	 *            examined typed table
	 * @return the score
	 */
	double isId(int columnIndex, TypedTable table);

	/**
	 * Provides a score indicating the percentage to which the column conforms to
	 * the {@link eu.odalic.extrarelatable.model.bag.UnitValue} data type.
	 * 
	 * @param columnIndex
	 *            index of the column in question
	 * @param table
	 *            examined typed table
	 * @return the score
	 */
	double isUnit(int columnIndex, TypedTable table);
}
