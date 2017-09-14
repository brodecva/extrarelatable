package eu.odalic.extrarelatable.algorithms.distance;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.springframework.stereotype.Component;

@Immutable
@Component
public final class KolmogorovSmirnovDistance implements Distance {

	private static final KolmogorovSmirnovTest STATISTIC_CALCULATOR = new KolmogorovSmirnovTest();
	private static final int MINIMUM_DATA_SIZE = 2;
	
	@Override
	public double compute(final double[] first, final double[] second) {
		checkNotNull(first);
		checkNotNull(second);
		checkArgument(first.length >= MINIMUM_DATA_SIZE);
		checkArgument(second.length >= MINIMUM_DATA_SIZE);
		
		return STATISTIC_CALCULATOR.kolmogorovSmirnovStatistic(first, second);
	}

}
