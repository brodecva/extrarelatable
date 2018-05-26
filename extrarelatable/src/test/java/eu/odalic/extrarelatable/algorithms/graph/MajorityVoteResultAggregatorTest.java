/**
 * 
 */
package eu.odalic.extrarelatable.algorithms.graph;

import static org.junit.Assert.*;

import java.util.SortedSet;

import org.eclipse.collections.impl.block.factory.Comparators;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSetMultimap;

/**
 * @author brodecva
 *
 */
public class MajorityVoteResultAggregatorTest {

	/**
	 * Test method for {@link eu.odalic.extrarelatable.algorithms.graph.MajorityVoteResultAggregator#aggregate(com.google.common.collect.SetMultimap, java.util.Comparator)}.
	 */
	@Test
	public final void testAggregateSetMultimapOfTQextendsUComparatorOfQsuperT() {
		final MajorityVoteResultAggregator<String> aggregator = new MajorityVoteResultAggregator<>();
		
		final SortedSet<String> result = aggregator.aggregate(
			ImmutableSetMultimap.<String, String>builder()
				.put("Abe", "Lucinda")
				.put("Abe", "Kelly")
				.put("Abe", "Danna")
				.put("Barry", "Cindy")
				.put("Barry", "Monika")
				.put("Barry", "Suzanne")
				.put("Barry", "Maggie")
				.put("Carl", "Debie")
				.put("Carl", "Debie")
				.put("Carl", "Debie")
				.put("Carl", "Debie")
				.put("Carl", "Debie")
				.put("Danny", "Ursula")
				.put("Danny", "Sandra")
			.build(),
			Comparators.naturalOrder()
		);
		
		assertEquals(ImmutableList.of("Barry", "Abe", "Danny", "Carl"), ImmutableList.copyOf(result));
	}

}
