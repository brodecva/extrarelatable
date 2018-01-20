package eu.odalic.extrarelatable.algorithms.graph;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

import com.google.common.collect.SetMultimap;

import eu.odalic.extrarelatable.model.annotation.MeasuredNode;

public interface ResultAggregator {
	<T extends Comparable<T>> SortedSet<T> aggregate(SetMultimap<T, ? extends MeasuredNode> levelAggregates);
	
	<T> SortedSet<T> aggregate(SetMultimap<T, ? extends MeasuredNode> levelAggregates, Comparator<? super T> keysComparator);

	static <T extends Comparable<? super T>, U extends Comparable<? super U>> Comparator<T> aggregatesComparator(final Map<T, U> map) {
		return aggregatesComparator(map, Comparator.naturalOrder(), Comparator.naturalOrder());
	}
	
	static <T, U> Comparator<T> aggregatesComparator(final Map<T, U> map, final Comparator<? super T> generalKeysComparator, final Comparator<? super U> generalValuesComparator) {
		return (first, second) -> {
			final int valuesComparison = generalValuesComparator.compare(map.get(first), map.get(second));
			
			if (valuesComparison == 0) {
				return generalKeysComparator.compare(first,  second);
			} else {
				return valuesComparison;
			}
		};
	}
}
