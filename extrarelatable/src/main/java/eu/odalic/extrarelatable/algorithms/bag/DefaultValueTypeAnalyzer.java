package eu.odalic.extrarelatable.algorithms.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public final class DefaultValueTypeAnalyzer implements ValueTypeAnalyzer {

	private final NumericValueParser numericValueParser;
	private final InstantValueParser dateValueParser;
	
	public DefaultValueTypeAnalyzer(final NumericValueParser numericValueParser, final InstantValueParser dateValueParser) {
		checkNotNull(numericValueParser);
		checkNotNull(dateValueParser);
		
		this.numericValueParser = numericValueParser;
		this.dateValueParser = dateValueParser;
	}
	
	@Override
	public boolean isNumeric(final String value, final Locale locale) {
		checkNotNull(locale);
		if (isEmpty(value)) {
			return false;
		}
		
		try {
			numericValueParser.parse(value, locale);
		} catch (final IllegalArgumentException e) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean isInstant(final String value, final Locale locale) {
		checkNotNull(locale);
		if (isEmpty(value)) {
			return false;
		}
		
		try {
			dateValueParser.parse(value, locale);
		} catch (final IllegalArgumentException e) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}
}
