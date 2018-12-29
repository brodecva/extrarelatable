package eu.odalic.extrarelatable.algorithms.bag;

import java.util.Locale;

/**
 * Analyzes the types of values in context of the provided {@link Locale}s.
 * 
 * @author Václav Brodec
 *
 */
public interface ValueTypeAnalyzer {
	/**
	 * Tests whether the value is a number in the provided {@link Locale}.
	 * 
	 * @param value
	 *            text value
	 * @param locale
	 *            a {@link Locale}
	 * @return whether the value can be understood as a number
	 */
	boolean isNumeric(String value, Locale locale);

	/**
	 * Tests whether the value is empty.
	 * 
	 * @param value
	 *            text value
	 * @return whether the value is empty
	 */
	boolean isEmpty(final String value);

	/**
	 * Tests whether the value is a representation of {@link java.time.Instant} in
	 * time in the provided {@link Locale}.
	 * 
	 * @param value
	 *            text value
	 * @param locale
	 *            a {@link Locale}
	 * @return whether the value can be understood as a representation of
	 *         {@link java.time.Instant} in time.
	 */
	boolean isInstant(String value, Locale locale);

	/**
	 * Tests whether the value is a unit value (centimeters, kilograms, ...) in the
	 * provided {@link Locale}.
	 * 
	 * @param value
	 *            text value
	 * @param locale
	 *            a {@link Locale}
	 * @return whether the value can be understood as a number
	 */
	boolean isUnit(String value, Locale locale);
}
