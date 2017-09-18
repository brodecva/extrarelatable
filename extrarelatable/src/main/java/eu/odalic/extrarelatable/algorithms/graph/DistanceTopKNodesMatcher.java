package eu.odalic.extrarelatable.algorithms.graph;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.SortedSet;
import javax.annotation.concurrent.Immutable;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSortedSet;

import eu.odalic.extrarelatable.algorithms.distance.Distance;
import eu.odalic.extrarelatable.model.annotation.MeasuredNode;
import eu.odalic.extrarelatable.model.bag.NumericValue;
import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.graph.Property;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.graph.PropertyTree.Node;

@Immutable
@Component
public final class DistanceTopKNodesMatcher implements TopKNodesMatcher {
	
	private final Distance distance;
	
	public DistanceTopKNodesMatcher(final Distance distance) {
		checkNotNull(distance);
		
		this.distance = distance;
	}

	@Override
	public SortedSet<MeasuredNode> match(final BackgroundKnowledgeGraph graph, final Collection<? extends NumericValue> values, final int k) {
		checkNotNull(graph);
		checkNotNull(values);
		checkArgument(k >= 1);

		final double[] inputValues = values.stream().mapToDouble(e -> e.getFigure()).toArray();

		final PriorityQueue<MeasuredNode> winners = new PriorityQueue<>(k, Comparator.reverseOrder());
		
		for (final Property property : graph) {
			for (final PropertyTree instance : property) {
				for (final Node node : instance) {
					final double[] candidateValues = node.getValues().stream().mapToDouble(e -> e.getFigure()).toArray();
					
					final double computedDistance = distance.compute(inputValues, candidateValues);
					final MeasuredNode candidateNode = new MeasuredNode(node, computedDistance);
					
					if (winners.size() < k) {
						winners.add(candidateNode);
					} else {
						final MeasuredNode farthestNode = winners.peek();
						
						if (farthestNode.compareTo(candidateNode) > 0) {
							winners.poll();
							winners.add(candidateNode);
						}
					}
				}
			}
		}

		return ImmutableSortedSet.copyOf(winners);
	}
	
	
}
