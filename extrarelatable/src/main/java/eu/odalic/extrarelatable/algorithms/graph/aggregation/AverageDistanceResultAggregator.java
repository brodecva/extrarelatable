package eu.odalic.extrarelatable.algorithms.graph.aggregation;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

import javax.annotation.concurrent.Immutable;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.SetMultimap;

import eu.odalic.extrarelatable.model.annotation.MeasuredNode;

/**
 * Aggregates {@link MeasuredNode}s according to their average distance. This
 * means that the products of aggregation are ordered in ascending order by the
 * average distance of the associated aggregated measured nodes. In case of tie
 * the natural ordering of products is used.
 * 
 * @author Václav Brodec
 * 
 */
@Immutable
@Component("averageDistance")
public class AverageDistanceResultAggregator implements ResultAggregator<MeasuredNode> {

	@Override
	public <T extends Comparable<T>> SortedSet<T> aggregate(
			final SetMultimap<T, ? extends MeasuredNode> levelAggregates) {
		return aggregate(levelAggregates, Comparator.naturalOrder());
	}

	@Override
	public <T> SortedSet<T> aggregate(final SetMultimap<T, ? extends MeasuredNode> levelAggregates,
			final Comparator<? super T> generalKeysComparator) {
		final Map<T, Double> mappedAggregates = levelAggregates.asMap().entrySet().stream()
				.collect(ImmutableMap.toImmutableMap(e -> e.getKey(),
						e -> e.getValue().stream().reduce(0d, (u, n) -> u + n.getDistance(), (a, b) -> a + b)
								/ e.getValue().size()));

		final SortedSet<T> sortedAggregates = ImmutableSortedSet.copyOf(ResultAggregator.aggregatesComparator(
				mappedAggregates, generalKeysComparator, Comparator.naturalOrder()), mappedAggregates.keySet());

		return sortedAggregates;
	}
}
