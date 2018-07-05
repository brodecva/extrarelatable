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

@Immutable
@Component
public final class DefaultNumericValueParser implements NumericValueParser {

	private final int limit;
	
	@Autowired
	public DefaultNumericValueParser(final @Value("${eu.odalic.extrarelatable.parseLimit:50}") int limit) {
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
			number = format.parse(cutText);
		} catch (final ParseException e) {
			throw new IllegalArgumentException(e);
		}
	    
	    return number.doubleValue();
	}
	
}
