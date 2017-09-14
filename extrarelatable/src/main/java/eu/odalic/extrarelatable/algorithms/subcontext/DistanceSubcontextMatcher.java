package eu.odalic.extrarelatable.algorithms.subcontext;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.odalic.extrarelatable.algorithms.distance.Distance;
import eu.odalic.extrarelatable.model.subcontext.Partition;
import eu.odalic.extrarelatable.model.subcontext.Subcontext;

@Immutable
@Component
public final class DistanceSubcontextMatcher implements SubcontextMatcher {

	private final Distance distance;
	
	@Autowired
	public DistanceSubcontextMatcher(final Distance distance) {
		checkNotNull(distance);
		
		this.distance = distance;
	}

	@Override
	public Subcontext match(final Set<? extends Subcontext> candidates, final Partition inputPartition) {
		checkNotNull(candidates);
		checkNotNull(inputPartition);
		checkArgument(!candidates.isEmpty());

		final double[] inputValues = inputPartition.getDoubleValuesArray();

		boolean found = false;
		Double maximumDistance = null;
		Subcontext winner = null;
		for (final Subcontext candidate : candidates) {
			for (final Partition candidatePartition : candidate.getPartitions().values()) {
				final double[] candidateValues = candidatePartition.getDoubleValuesArray();

				final double computedDistance = distance.compute(inputValues, candidateValues);
				if ((!found) || (computedDistance > maximumDistance)) {
					maximumDistance = computedDistance;
					winner = candidate;
					found = true;
				}
			}
		}

		return winner;
	}

}
