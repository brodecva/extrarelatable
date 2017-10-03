package eu.odalic.extrarelatable.algorithms.graph;

import java.util.Map;
import java.util.SortedSet;

import javax.annotation.concurrent.Immutable;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.SetMultimap;

import eu.odalic.extrarelatable.model.annotation.MeasuredNode;

@Immutable
@Component("majorityVote")
public class MajorityVoteResultAggregator implements ResultAggregator {

	@Override
	public <T> SortedSet<T> aggregate(final SetMultimap<T, ? extends MeasuredNode> levelAggregates) {
		final Map<T, Integer> mappedAggregates = levelAggregates.asMap().entrySet().stream()
				.collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> e.getValue().size()));
		final SortedSet<T> sortedAggregates = ImmutableSortedSet.copyOf(
				(first, second) -> mappedAggregates.get(second).compareTo(mappedAggregates.get(first)),
				mappedAggregates.keySet());

		return sortedAggregates;
	}

}
