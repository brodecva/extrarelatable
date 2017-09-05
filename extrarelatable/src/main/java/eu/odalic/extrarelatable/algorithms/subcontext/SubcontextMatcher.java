package eu.odalic.extrarelatable.algorithms.subcontext;

import java.util.Set;

import eu.odalic.extrarelatable.model.subcontext.Partition;
import eu.odalic.extrarelatable.model.subcontext.Subcontext;

public interface SubcontextMatcher {
	Subcontext match(Set<? extends Subcontext> candidates, Partition inputPartition);
}
