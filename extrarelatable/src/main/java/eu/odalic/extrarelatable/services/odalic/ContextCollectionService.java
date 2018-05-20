package eu.odalic.extrarelatable.services.odalic;

import java.util.Random;
import java.util.Set;

import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.services.odalic.values.ResultValue;

public interface ContextCollectionService {
	ResultValue process(ParsedTable table, Set<? extends String> usedBases, String primaryBase, Random random);
}
