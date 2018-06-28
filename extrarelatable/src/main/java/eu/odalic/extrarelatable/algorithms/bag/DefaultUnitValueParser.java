package eu.odalic.extrarelatable.algorithms.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.springframework.stereotype.Component;

@Immutable
@Component
public final class DefaultUnitValueParser implements UnitValueParser {

	private static final String NON_DIGIT_CHARACTERS_PATTERN = "[^0-9.,+- ]";

	@Override
	public double parse(final String text, final Locale locale) {
		checkNotNull(text);
		checkNotNull(locale);
		
		final NumberFormat format = NumberFormat.getInstance(locale);
	    final Number number;
		try {
			number = format.parse(text.replaceAll(NON_DIGIT_CHARACTERS_PATTERN, "").trim());
		} catch (final ParseException e) {
			throw new IllegalArgumentException(e);
		}
	    
	    return number.doubleValue();
	}
	
}
