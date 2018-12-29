package eu.odalic.extrarelatable.algorithms.graph.aggregation;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

import com.google.common.collect.SetMultimap;

/**
 * General template of a result aggregator.
 * 
 * @author Václav Brodec
 *
 * @param <U>
 *            the type of aggregated items
 */
public interface ResultAggregator<U> {
	/**
	 * Aggregates according to the inner logging of the comparator, in case of tie
	 * the natural ordering on the aggregation product is used.
	 * 
	 * @param levelAggregates
	 *            map of the aggregation products to the associated aggregated items
	 * @return products of aggregation sorted according to the declared logic of the
	 *         aggregator implementation
	 * 
	 * @param <T>
	 *            type of the product of the aggregation
	 */
	<T extends Comparable<T>> SortedSet<T> aggregate(SetMultimap<T, ? extends U> levelAggregates);

	/**
	 * Aggregates according to the inner logic of the comparator, in case of tie
	 * the provided comparator on the aggregation product is used.
	 * 
	 * @param levelAggregates
	 *            map of the aggregation products to the aggregated items
	 * @param keysComparator
	 *            comparator on the type of the aggregation product
	 * @return products of aggregation sorted according to the declared logic of the
	 *         aggregator implementation
	 * 
	 * @param <T>
	 *            type of the product of the aggregation
	 */
	<T> SortedSet<T> aggregate(SetMultimap<T, ? extends U> levelAggregates, Comparator<? super T> keysComparator);

	/**
	 * Return the comparator from
	 * {@link #aggregatesComparator(Map, Comparator, Comparator)} where the input
	 * comparators are both derived from the natural ordering of the keys and the values.
	 * 
	 * @param map
	 *            reference map
	 * @return the comparator
	 * 
	 * @param <T>
	 *            type of the keys
	 * @param <U>
	 *            type of the values
	 */
	static <T extends Comparable<? super T>, U extends Comparable<? super U>> Comparator<T> aggregatesComparator(
			final Map<T, U> map) {
		return aggregatesComparator(map, Comparator.naturalOrder(), Comparator.naturalOrder());
	}

	/**
	 * Creates a comparator working in the following manner:
	 * <ol>
	 * <li>the values associated with the compared pair in the input map are
	 * compared using the provided values comparator.</li>
	 * <li>In case of tie, the pair is directly compared using the provided keys
	 * comparator.</li>
	 * </ol>
	 * 
	 * @param map
	 *            reference map
	 * @param generalKeysComparator
	 *            keys comparator
	 * @param generalValuesComparator
	 *            values comparator
	 * @return the described comparator
	 * 
	 * @param <T>
	 *            type of the keys
	 * @param <U>
	 *            type of the values
	 */
	static <T, U> Comparator<T> aggregatesComparator(final Map<T, U> map,
			final Comparator<? super T> generalKeysComparator, final Comparator<? super U> generalValuesComparator) {
		return (first, second) -> {
			final int valuesComparison = generalValuesComparator.compare(map.get(first), map.get(second));

			if (valuesComparison == 0) {
				return generalKeysComparator.compare(first, second);
			} else {
				return valuesComparison;
			}
		};
	}
}
