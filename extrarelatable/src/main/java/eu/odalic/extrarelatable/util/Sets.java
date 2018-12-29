package eu.odalic.extrarelatable.util;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Utility class for -- you guessed it -- working with Java Collections sets.
 * 
 * @author Václav Brodec
 *
 */
public final class Sets {

	/**
	 * Derives set comparator based on the comparator of the elements. The
	 * comparators cuts short in case the sizes of the sets are not the same.
	 * 
	 * @param comparator
	 *            comparator of element of type <T>
	 * @return comparator of two sets based on the comparator of the elements
	 * 
	 * @param <T> type of elements
	 */
	public static <T> Comparator<Set<T>> comparator(final Comparator<? super T> comparator) {
		return (first, second) -> {
			final SortedSet<T> firstSet = ImmutableSortedSet.copyOf(first);
			final SortedSet<T> secondSet = ImmutableSortedSet.copyOf(second);

			final int sizesComparison = Integer.compare(firstSet.size(), secondSet.size());
			if (sizesComparison != 0) {
				return sizesComparison;
			}

			for (final T firstSetElement : firstSet) {
				for (final T secondSetElement : secondSet) {
					final int elementsComparison = comparator.compare(firstSetElement, secondSetElement);
					if (elementsComparison != 0) {
						return elementsComparison;
					}
				}
			}

			return 0;
		};
	}

	/**
	 * Derive set comparator based on the natural order of the elements. The
	 * comparator cuts short in case the sizes of the sets are not the same.
	 * 
	 * @return natural comparator of the sets
	 * 
	 * @param <T> type of elements
	 */
	public static <T extends Comparable<? super T>> Comparator<Set<T>> comparator() {
		return comparator(Comparator.naturalOrder());
	}

	/**
	 * We want to keep this class uninstantiable, so no visible constructor is
	 * available.
	 */
	private Sets() {
	}
}
