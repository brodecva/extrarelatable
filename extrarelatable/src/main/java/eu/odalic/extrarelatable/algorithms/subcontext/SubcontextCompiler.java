package eu.odalic.extrarelatable.algorithms.subcontext;

import java.util.Set;

import eu.odalic.extrarelatable.model.subcontext.Partition;
import eu.odalic.extrarelatable.model.subcontext.Subcontext;
import eu.odalic.extrarelatable.model.table.TypedTable;

/**
 * Creates candidate {@link Subcontext}s from input {@link Partition}s.
 * 
 * @author Václav Brodec
 *
 */
public interface SubcontextCompiler {
	/**
	 * Creates a set of candidate {@link Subcontext}s from the input {@link Partition}.
	 * 
	 * @param partition input partition
	 * @param availableContextColumnIndices indices of remaining columns able to provide row context
	 * @param table the source table
	 * @param minimumPartitionRelativeSize minimum partition size relative to the input partition. If a candidate {@link Subcontext} has no larger partition, it is skipped.
	 * @param maximumPartitionRelativeSize maximum partition size relative to the input partition. If a candidate {@link Subcontext} has no smaller partition, it is skipped.
	 * @param minimumPartitionSize minimum absolute partition size. If a candidate {@link Subcontext} has no larger partition, it is skipped.
	 * @return the set of candidate subcontexts created from the input partition
	 */
	Set<Subcontext> compile(Partition partition, Set<Integer> availableContextColumnIndices, TypedTable table,
			double minimumPartitionRelativeSize, double maximumPartitionRelativeSize, int minimumPartitionSize);
}
