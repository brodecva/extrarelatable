package eu.odalic.extrarelatable.algorithms.bag;

import java.util.Locale;

/**
 * Parser of unit value.
 * 
 * @author Václav Brodec
 *
 */
public interface UnitValueParser {
	/**
	 * Parses the unit value into a double according to a {@link Locale}.
	 * 
	 * @param text
	 *            text value
	 * @param locale
	 *            the {@link Locale} instance used during the parsing
	 * @return primitive {@link java.lang.Double} representation of the unit value
	 *         in the text
	 */
	double parse(String text, Locale locale);
}
