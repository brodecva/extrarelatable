package eu.odalic.extrarelatable.services.odalic;

import java.util.Random;

import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.services.odalic.values.ComputationInputValue;

public interface ComputationInputConverter {
	ComputationInputValue convert(ParsedTable table, int rowsLimit, Random random);
}
