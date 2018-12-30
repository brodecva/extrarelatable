package eu.odalic.extrarelatable.util;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableMap;

/**
 * Utility class for -- you guessed it -- working with Java Collections lists.
 * 
 * @author Václav Brodec
 *
 */
public final class Lists {
	/**
	 * Converts the list to a map in which the keys are the original list indices
	 * and the values are the elements of the original list accessible through the
	 * associated index.
	 * 
	 * @param list
	 *            list
	 * @return map of list indices to the elements
	 * 
	 * @param <T>
	 *            type of elements
	 */
	public static final <T> Map<Integer, T> toMap(final List<? extends T> list) {
		return IntStream.range(0, list.size()).mapToObj(i -> Integer.valueOf(i))
				.collect(ImmutableMap.toImmutableMap(i -> i, i -> list.get(i)));
	}

	/**
	 * We want to keep this class uninstantiable, so no visible constructor is
	 * available.
	 */
	private Lists() {
	}
}
