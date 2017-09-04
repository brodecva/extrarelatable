package eu.odalic.extrarelatable.algorithms.subcontexts;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import eu.odalic.extrarelatable.model.histogram.Partition;
import eu.odalic.extrarelatable.model.histogram.Subcontext;

public final class MaxKSDistanceSubcontextMatcher implements SubcontextMatcher {

	private static final KolmogorovSmirnovTest STATISTIC_CALCULATOR = new KolmogorovSmirnovTest();
	private static final int MINIMUM_DATA_SIZE = 2;
	
	@Override
	public Subcontext match(final Set<? extends Subcontext> candidates, final Partition inputPartition) {
		checkNotNull(candidates);
		checkNotNull(inputPartition);
		checkArgument(!candidates.isEmpty());
		
		final double[] inputValues = inputPartition.getDoubleValuesArray();
		checkArgument(inputValues.length >= MINIMUM_DATA_SIZE);
		
		Double maximumDistance = null;
		Subcontext winner = null;
		for (final Subcontext candidate : candidates) {
			for (final Partition candidatePartition : candidate.getPartitions().values()) {
				final double[] candidateValues = candidatePartition.getDoubleValuesArray();
				checkArgument(candidateValues.length >= MINIMUM_DATA_SIZE);
				
				final double distance = STATISTIC_CALCULATOR.kolmogorovSmirnovStatistic(inputValues, candidateValues);
				if (distance > maximumDistance) {
					maximumDistance = distance;
					winner = candidate;
				}
			}
		}
		
		return winner;
	}

}
