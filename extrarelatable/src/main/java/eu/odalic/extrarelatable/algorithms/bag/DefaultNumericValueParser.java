package eu.odalic.extrarelatable.algorithms.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.springframework.stereotype.Component;

@Immutable
@Component
public final class DefaultNumericValueParser implements NumericValueParser {

	@Override
	public double parse(final String text, final Locale locale) {
		checkNotNull(text);
		
		final NumberFormat format = NumberFormat.getInstance(locale);
	    final Number number;
		try {
			number = format.parse(text);
		} catch (final ParseException e) {
			throw new IllegalArgumentException(e);
		}
	    
	    return number.doubleValue();
	}
	
}
