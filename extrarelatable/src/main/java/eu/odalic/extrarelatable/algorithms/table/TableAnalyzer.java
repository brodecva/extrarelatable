package eu.odalic.extrarelatable.algorithms.table;

import java.util.Locale;
import java.util.Map;

import eu.odalic.extrarelatable.model.bag.Type;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;

/**
 * Infer data types of the cells in the table, producing {@link TypedTable}.
 * 
 * @author Václav Brodec
 *
 */
public interface TableAnalyzer {
	/**
	 * Infers cell data types and converts the text values, producing
	 * {@link TypedTable}.
	 * 
	 * @param table
	 *            table parsed into cells
	 * @param locale
	 *            the {@link Locale} used to determine the data types
	 * @return the typed table
	 */
	TypedTable infer(ParsedTable table, Locale locale);

	/**
	 * Infers cell data types and converts the text values, producing
	 * {@link TypedTable}.
	 * 
	 * @param table
	 *            table parsed into cells
	 * @param locale
	 *            the {@link Locale} used to determine the data types
	 * @param columnTypeHints
	 *            overriding type hints for some of the columns (all cells in the
	 *            column are converted to that type)
	 * @return the typed table
	 */
	TypedTable infer(ParsedTable table, Locale locale, Map<? extends Integer, ? extends Type> columnTypeHints);
}
