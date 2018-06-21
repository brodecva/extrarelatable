package eu.odalic.extrarelatable.algorithms.graph.aggregation;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

import javax.annotation.concurrent.Immutable;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.SetMultimap;

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
		
		final SortedSet<T> sortedAggregates = ImmutableSortedSet.copyOf(
				ResultAggregator.aggregatesComparator(mappedAggregates, generalKeysComparator, Comparator.reverseOrder()),
				mappedAggregates.keySet());

		return sortedAggregates;
	}	
}
