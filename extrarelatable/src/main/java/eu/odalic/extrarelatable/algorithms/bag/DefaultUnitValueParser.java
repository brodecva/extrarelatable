package eu.odalic.extrarelatable.algorithms.bag;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A {@link UnitValueParser} implementation that first strips away any non-digit
 * characters from the parsed text. This has its drawbacks (e.g. losing
 * information about the position of a possible decimal separator),
 * which are mostly acceptable for the purpose.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
@Component
public final class DefaultUnitValueParser implements UnitValueParser {

	private static final String NON_DIGIT_CHARACTERS_PATTERN = "[^0-9.,+- ]";

	private final int limit;

	/**
	 * Instantiates the parser.
	 * 
	 * @param limit maximum number of characters that are parsed
	 */
	@Autowired
	public DefaultUnitValueParser(final @Value("${eu.odalic.extrarelatable.parseLimit:50}") int limit) {
		checkArgument(limit > 0, "The limit must be a positive integer!");

		this.limit = limit;
	}

	@Override
	public double parse(final String text, final Locale locale) {
		checkNotNull(text);
		checkNotNull(locale);

		final String cutText = text.substring(0, Math.min(text.length(), this.limit));

		final NumberFormat format = NumberFormat.getInstance(locale);
		final Number number;
		try {
			number = format.parse(cutText.replaceAll(NON_DIGIT_CHARACTERS_PATTERN, "").trim());
		} catch (final ParseException e) {
			throw new IllegalArgumentException(e);
		}

		return number.doubleValue();
	}

}
