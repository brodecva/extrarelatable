package eu.odalic.extrarelatable.algorithms.subcontexts;

import java.util.Set;

import eu.odalic.extrarelatable.model.histogram.Partition;
import eu.odalic.extrarelatable.model.histogram.Subcontext;

public interface SubcontextMatcher {
	Subcontext match(Set<? extends Subcontext> candidates, Partition inputPartition);
}
