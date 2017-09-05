package eu.odalic.extrarelatable.input;

import java.util.Locale;

public interface NumericValueParser {
	double parse(String text, Locale locale);
}
