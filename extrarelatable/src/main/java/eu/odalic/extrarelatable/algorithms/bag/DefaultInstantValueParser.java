package eu.odalic.extrarelatable.algorithms.bag;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Instant;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.pojava.datetime.DateTime;
import org.pojava.datetime.DateTimeConfig;
import org.pojava.datetime.DateTimeConfigBuilder;
import org.pojava.datetime.IDateTimeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link InstantValueParser}.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
@Component
public final class DefaultInstantValueParser implements InstantValueParser {

	private final int limit;
	
	/**
	 * Instantiates the parser.
	 * 
	 * @param limit maximum number of characters that are parsed
	 */
	@Autowired
	public DefaultInstantValueParser(final @Value("${eu.odalic.extrarelatable.parseLimit:50}") int limit) {
		checkArgument(limit > 0, "The limit must be a positive integer!");
		
		this.limit = limit;
	}
	
	@Override
	public Instant parse(final String text, final Locale locale) {
		checkNotNull(text);
		
		final DateTimeConfigBuilder builder = DateTimeConfigBuilder.newInstance();
		builder.setLocale(locale);
		final IDateTimeConfig config =  DateTimeConfig.fromBuilder(builder);
		
		final String cutText = text.substring(0, Math.min(text.length(), this.limit));
		
		final DateTime dateTime;
		try {
			dateTime = DateTime.parse(cutText, config);
		} catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}
		
		return dateTime.toDate().toInstant();
	}
	
}
