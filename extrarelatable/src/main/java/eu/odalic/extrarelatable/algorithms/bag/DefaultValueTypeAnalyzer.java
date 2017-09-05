package eu.odalic.extrarelatable.algorithms.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Locale;
import javax.annotation.concurrent.Immutable;

import org.springframework.stereotype.Component;

@Immutable
@Component
public final class DefaultValueTypeAnalyzer implements ValueTypeAnalyzer {

	private final NumericValueParser numericValueParser;
	
	public DefaultValueTypeAnalyzer(final NumericValueParser numericValueParser) {
		checkNotNull(numericValueParser);
		
		this.numericValueParser = numericValueParser;
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
	public boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}
}
