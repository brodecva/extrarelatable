package eu.odalic.extrarelatable.algorithms.bag;

import java.util.Locale;

import javax.annotation.Nullable;

public interface NumericValueParser {
	double parse(String text, Locale locale);
}
