package eu.odalic.extrarelatable.algorithms.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Instant;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.pojava.datetime.DateTime;
import org.pojava.datetime.DateTimeConfig;
import org.pojava.datetime.DateTimeConfigBuilder;
import org.pojava.datetime.IDateTimeConfig;
import org.springframework.stereotype.Component;

@Immutable
@Component
public final class DefaultInstantValueParser implements InstantValueParser {

	@Override
	public Instant parse(final String text, final Locale locale) {
		checkNotNull(text);
		
		final DateTimeConfigBuilder builder = DateTimeConfigBuilder.newInstance();
		builder.setLocale(locale);
		final IDateTimeConfig config =  DateTimeConfig.fromBuilder(builder);
		
		final DateTime dateTime;
		try {
			dateTime = DateTime.parse(text, config);
		} catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}
		
		return dateTime.toDate().toInstant();
	}
	
}
