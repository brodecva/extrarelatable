package eu.odalic.extrarelatable.algorithms.distance;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.springframework.stereotype.Component;

/**
 * Distance measure based on Kolmogorov-Smirnov two sample test. It requires the
 * bags of values to be at least size of {@value #MINIMUM_DATA_SIZE}.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
@Component
public final class KolmogorovSmirnovDistance implements Distance {

	/**
	 * Minimum required size of the input bags of values.
	 */
	public static final int MINIMUM_DATA_SIZE = 2;

	private static final KolmogorovSmirnovTest STATISTIC_CALCULATOR = new KolmogorovSmirnovTest();

	@Override
	public double compute(final double[] first, final double[] second) {
		checkNotNull(first);
		checkNotNull(second);
		checkArgument(first.length >= MINIMUM_DATA_SIZE);
		checkArgument(second.length >= MINIMUM_DATA_SIZE);

		return STATISTIC_CALCULATOR.kolmogorovSmirnovStatistic(first, second);
	}

}
