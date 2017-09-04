package eu.odalic.extrarelatable.algorithms.subcontexts;

import java.util.Set;

import eu.odalic.extrarelatable.model.histogram.Partition;
import eu.odalic.extrarelatable.model.histogram.Subcontext;
import eu.odalic.extrarelatable.model.table.TypedTable;

public interface SubcontextCompiler {
	Set<Subcontext> compile(Partition partition, Set<Integer> availableContextColumnIndices, TypedTable table,
			double minimumPartitionRelativeSize, double maximumPartitionRelativeSize);
}
