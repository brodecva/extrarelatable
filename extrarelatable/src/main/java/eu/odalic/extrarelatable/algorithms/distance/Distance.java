package eu.odalic.extrarelatable.algorithms.distance;

/**
 * Measures distance (based on the implementation) between two bags of numeric
 * values.
 * 
 * @author Václav Brodec
 *
 */
public interface Distance {
	/**
	 * Measures the distance. The implementations may further restrict the valid inputs.
	 * 
	 * @param first
	 *            first bag of values
	 * @param second
	 *            second bag of values
	 * @return measured distance (no guarantee regarding the range of values, depends
	 *         on implementation)
	 */
	double compute(double[] first, double[] second);
}
