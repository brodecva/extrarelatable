package eu.odalic.extrarelatable.algorithms.subcontext;

import java.util.Set;

import eu.odalic.extrarelatable.model.subcontext.Partition;
import eu.odalic.extrarelatable.model.subcontext.Subcontext;
import eu.odalic.extrarelatable.model.table.TypedTable;

public interface SubcontextCompiler {
	Set<Subcontext> compile(Partition partition, Set<Integer> availableContextColumnIndices, TypedTable table,
			double minimumPartitionRelativeSize, double maximumPartitionRelativeSize, int minimumPartitionSize);
}
