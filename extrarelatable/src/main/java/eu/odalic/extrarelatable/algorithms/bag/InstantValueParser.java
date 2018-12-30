package eu.odalic.extrarelatable.algorithms.bag;

import java.time.Instant;
import java.util.Locale;

/**
 * Parser of instants in time.
 * 
 * @author Václav Brodec
 *
 */
public interface InstantValueParser {
	/**
	 * Parses the unit value into an {@link Instant} according to a {@link Locale}.
	 * 
	 * @param text
	 *            text value
	 * @param locale
	 *            the {@link Locale} instance used during the parsing
	 * @return {@link Instant} equivalent of the text value
	 */
	Instant parse(String text, Locale locale);
}
