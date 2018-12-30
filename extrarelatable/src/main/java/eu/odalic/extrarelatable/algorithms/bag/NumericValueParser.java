package eu.odalic.extrarelatable.algorithms.bag;

import java.util.Locale;

/**
 * Parser of numbers.
 * 
 * @author Václav Brodec
 *
 */
public interface NumericValueParser {
	/**
	 * Parses the text into a double according to a {@link Locale}.
	 * 
	 * @param text
	 *            text value
	 * @param locale
	 *            the {@link Locale} instance used during the parsing
	 * @return primitive {@link java.lang.Double} representation of the number in
	 *         the text
	 */
	double parse(String text, Locale locale);
}
