package eu.odalic.extrarelatable.services.odalic;

import java.util.Random;
import java.util.Set;

import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.services.odalic.values.ResultValue;

/**
 * Context-collecting service. Returns Odalic-compatible result.
 * 
 * @author Václav Brodec
 *
 */
public interface ContextCollectionService {
	/**
	 * Collects the available context for the table from linked data knowledge
	 * bases.
	 * 
	 * @param table
	 *            parsed table
	 * @param usedBases
	 *            recognizable names of used linked data bases
	 * @param primaryBase
	 *            recognizable names of the primary linked data base (takes
	 *            precedence)
	 * @param random
	 *            random generator (used to sample the table rows when over limit)
	 * @return the collected context in Odalic-compatible format
	 */
	ResultValue process(ParsedTable table, Set<? extends String> usedBases, String primaryBase, Random random);
}
