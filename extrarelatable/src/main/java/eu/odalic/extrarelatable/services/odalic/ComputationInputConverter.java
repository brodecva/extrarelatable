package eu.odalic.extrarelatable.services.odalic;

import java.util.Random;

import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.services.odalic.values.ComputationInputValue;

/**
 * Converter of the ERT input format to the one recognized by Odalic
 * ({@link ComputationInputValue}).
 * 
 * @author Václav Brodec
 *
 */
public interface ComputationInputConverter {
	/**
	 * Converts the ERT input format to the one recognized by Odalic.
	 * 
	 * @param table
	 *            parsed table
	 * @param rowsLimit
	 *            limit on number of rows sent to Odalic
	 * @param random
	 *            random generator used to sample the table rows when exceeding the
	 *            limit
	 * @return Odalic-compatible context-collecting input
	 * 
	 * @see ComputationInputValue
	 */
	ComputationInputValue convert(ParsedTable table, int rowsLimit, Random random);
}
