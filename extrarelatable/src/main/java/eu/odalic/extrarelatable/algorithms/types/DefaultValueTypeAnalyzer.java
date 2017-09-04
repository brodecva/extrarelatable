package eu.odalic.extrarelatable.algorithms.types;

import java.util.regex.Pattern;

public final class DefaultValueTypeAnalyzer implements ValueTypeAnalyzer {

	private static final Pattern NUMBERS_CONTAINED_PATTERN = Pattern.compile("[0-9]+");;

	public boolean isNumeric(String value) {
		if (isEmpty(value)) {
			return false;
		}
		
		return NUMBERS_CONTAINED_PATTERN.matcher(value).matches();
	}

	@Override
	public boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

}
