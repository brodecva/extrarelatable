package eu.odalic.extrarelatable.services.odalic;

import java.util.Set;

import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.services.odalic.values.ResultValue;

public interface OdalicService {
	ResultValue process(ParsedTable table, Set<? extends String> usedBases, String primaryBase);
}
