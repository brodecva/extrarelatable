package eu.odalic.extrarelatable.algorithms.bag;

import java.time.Instant;
import java.util.Locale;

public interface InstantValueParser {
	Instant parse(String text, Locale locale);
}
