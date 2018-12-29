package eu.odalic.extrarelatable.algorithms.graph.aggregation;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

import javax.annotation.concurrent.Immutable;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.SetMultimap;

/**
 * Aggregates according to the majority vote. This means that the products of
 * aggregation are ordered in descending order by the number of associated
 * aggregated items. In case of tie the natural ordering of products is used.
 * 
 * @author Václav Brodec
 *
 * @param <U>
 *            the type of aggregated items
 */
@Immutable
@Component("majorityVote")
public class MajorityVoteResultAggregator<U> implements ResultAggregator<U> {

	@Override
	public <T extends Comparable<T>> SortedSet<T> aggregate(final SetMultimap<T, ? extends U> levelAggregates) {
		return aggregate(levelAggregates, Comparator.naturalOrder());
	}

	@Override
	public <T> SortedSet<T> aggregate(final SetMultimap<T, ? extends U> levelAggregates,
			final Comparator<? super T> generalKeysComparator) {
		final Map<T, Integer> mappedAggregates = levelAggregates.asMap().entrySet().stream()
				.collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> e.getValue().size()));

		final SortedSet<T> sortedAggregates = ImmutableSortedSet.copyOf(ResultAggregator.aggregatesComparator(
				mappedAggregates, generalKeysComparator, Comparator.reverseOrder()), mappedAggregates.keySet());

		return sortedAggregates;
	}
}
