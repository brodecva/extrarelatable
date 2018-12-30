package eu.odalic.extrarelatable.algorithms.subcontext;

import java.util.Set;

import eu.odalic.extrarelatable.model.subcontext.Partition;
import eu.odalic.extrarelatable.model.subcontext.Subcontext;

/**
 * Select the best (most discriminating) {@link Subcontext} from candidates for
 * an input {@link Partition}.
 * 
 * @author Václav Brodec
 *
 */
public interface SubcontextMatcher {
	/**
	 * Selects the best (most discriminating) {@link Subcontext} from the candidates
	 * for the input {@link Partition}.
	 * 
	 * @param candidates
	 *            candidate {@link Subcontext}s
	 * @param inputPartition
	 *            input partition
	 * @param minimumPartitionRelativeSize
	 *            minimum partition size relative to the input partition. If a
	 *            candidate {@link Subcontext} has no larger partition, it is
	 *            skipped.
	 * @param maximumPartitionRelativeSize
	 *            maximum partition size relative to the input partition. If a
	 *            candidate {@link Subcontext} has no smaller partition, it is
	 *            skipped.
	 * @param minimumPartitionSize
	 *            minimum absolute partition size. If a candidate {@link Subcontext}
	 *            has no larger partition, it is skipped.
	 * @return the winning sub-context
	 */
	Subcontext match(Set<? extends Subcontext> candidates, Partition inputPartition,
			double minimumPartitionRelativeSize, double maximumPartitionRelativeSize, int minimumPartitionSize);
}
