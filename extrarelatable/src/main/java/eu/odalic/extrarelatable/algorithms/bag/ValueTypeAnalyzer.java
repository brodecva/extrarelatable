package eu.odalic.extrarelatable.algorithms.bag;

import java.util.Locale;

public interface ValueTypeAnalyzer {
	boolean isNumeric(String value, Locale locale);

	boolean isEmpty(final String value);

	boolean isInstant(String value, Locale locale);
}
