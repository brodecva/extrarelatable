package eu.odalic.extrarelatable.algorithms.bag;

public interface ValueTypeAnalyzer {
	boolean isNumeric(final String value);
	
	boolean isEmpty(final String value);
}
