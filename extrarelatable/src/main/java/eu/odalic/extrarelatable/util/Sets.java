package eu.odalic.extrarelatable.util;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;

public final class Sets {

	public static <T> Comparator<Set<T>> comparator(final  Comparator<? super T> comparator) {
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
	
	public static <T extends Comparable<? super T>> Comparator<Set<T>> comparator() {
		return comparator(Comparator.naturalOrder());
	}
	
	private Sets() {
	}
}
