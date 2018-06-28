package eu.odalic.extrarelatable.algorithms.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Locale;

import javax.annotation.Nullable;

import org.springframework.stereotype.Component;

@Component
public final class DefaultValueTypeAnalyzer implements ValueTypeAnalyzer {

	private final NumericValueParser numericValueParser;
	private final InstantValueParser dateValueParser;
	private final UnitValueParser unitValueParser;
	
	public DefaultValueTypeAnalyzer(final NumericValueParser numericValueParser, final InstantValueParser dateValueParser, final UnitValueParser unitValueParser) {
		checkNotNull(numericValueParser);
		checkNotNull(dateValueParser);
		checkNotNull(unitValueParser);
		
		this.numericValueParser = numericValueParser;
		this.dateValueParser = dateValueParser;
		this.unitValueParser = unitValueParser;
	}
	
	@Override
	public boolean isNumeric(final String value, @Nullable final Locale locale) {
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
	public boolean isInstant(final String value, @Nullable final Locale locale) {
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
	public boolean isUnit(final String value, @Nullable final Locale locale) {
		checkNotNull(locale);
		if (isEmpty(value)) {
			return false;
		}
		
		try {
			unitValueParser.parse(value, locale);
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
