package eu.odalic.extrarelatable.algorithms.graph.matching;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.concurrent.Immutable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import eu.odalic.extrarelatable.algorithms.distance.Distance;
import eu.odalic.extrarelatable.model.annotation.MeasuredNode;
import eu.odalic.extrarelatable.model.annotation.MeasuredNodeFactory;
import eu.odalic.extrarelatable.model.bag.NumberLikeValue;
import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.graph.Property;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.graph.PropertyTree.Node;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;

/**
 * Implementation of {@link TopKNodesMatcher} that takes the the available additional context into account, along the distance of numeric values.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
@Component
public final class ContextAwareDistanceTopKNodesMatcher implements TopKNodesMatcher {

	/**
	 * Default value of K for construction.
	 */
	public static final int INITIAL_DEFAULT_K = 50;
	/**
	 * Default weight of contribution of the values distances for construction.
	 */
	public static final double INITIAL_DEFAULT_VALUES_WEIGHT = 0.5d;
	/**
	 * Default weight of contribution of the property context distances for construction.
	 */
	public static final double INITIAL_DEFAULT_PROPERTIES_WEIGHT = 0.25d;
	/**
	 * Default weight of contribution of the class context distances for construction.
	 */
	public static final double INITIAL_DEFAULT_CLASSES_WEIGHT = 0.25d;

	private final Distance distance;
	private final MeasuredNodeFactory measuredNodeFactory;
	private final double defaultValuesWeight;
	private final double defaultPropertiesWeight;
	private final double defaultClassesWeight;
	private final int defaultK;

	/**
	 * Constructs the matcher.
	 * 
	 * @param distance user distance measure
	 * @param measuredNodeFactory factory for nodes associated with their distance
	 * @param defaultValuesWeight default weight of the contribution of the distance of numeric values to the overall distance
	 * @param defaultPropertiesWeight default weight of the contribution of the property context distance to the overall distance
	 * @param defaultClassesWeight default weight of the contribution of the class context distance to the overall distance
	 * @param defaultK default value of K (the maximum number of the best matching nodes)
	 */
	@Autowired
	ContextAwareDistanceTopKNodesMatcher(final Distance distance, final MeasuredNodeFactory measuredNodeFactory,
			@Value("${eu.odalic.extrarelatable.valuesWeight:0.5}") final double defaultValuesWeight,
			@Value("${eu.odalic.extrarelatable.propertiesWeight:0.25}") final double defaultPropertiesWeight,
			@Value("${eu.odalic.extrarelatable.classesWeight:0.25}") final double defaultClassesWeight,
			@Value("${eu.odalic.extrarelatable.topKNeighbours?:50}") final int defaultK) {
		checkNotNull(distance);
		checkNotNull(measuredNodeFactory);
		checkArgument(defaultValuesWeight >= 0, "The default values weight must be at least zero!");
		checkArgument(defaultPropertiesWeight >= 0, "The default properties weight must be at least zero!");
		checkArgument(defaultClassesWeight >= 0, "The default classes weight must be at least zero!");
		checkArgument(defaultK >= 1, "The k must be at least one!");

		this.distance = distance;
		this.measuredNodeFactory = measuredNodeFactory;
		this.defaultValuesWeight = defaultValuesWeight;
		this.defaultPropertiesWeight = defaultPropertiesWeight;
		this.defaultClassesWeight = defaultClassesWeight;
		this.defaultK = defaultK;
	}

	/**
	 * Constructs the matcher using the defaults.
	 * 
	 * @param distance used distance measure
	 * @param measuredNodeFactory factory for nodes associated with their distance
	 */
	public ContextAwareDistanceTopKNodesMatcher(final Distance distance, final MeasuredNodeFactory measuredNodeFactory) {
		this(distance, measuredNodeFactory, INITIAL_DEFAULT_VALUES_WEIGHT, INITIAL_DEFAULT_PROPERTIES_WEIGHT, INITIAL_DEFAULT_CLASSES_WEIGHT, INITIAL_DEFAULT_K);
	}

	@Override
	public SortedSet<MeasuredNode> match(final BackgroundKnowledgeGraph graph, final Node matchedNode,
			final double valuesWeight, final double propertiesWeight, final double classesWeight, final int k) {
		return match(graph, matchedNode.getValues(),
				matchedNode.getPropertyTree().getContext().getDeclaredContextColumnProperties().values(),
				matchedNode.getPropertyTree().getContext().getDeclaredContextColumnClasses().values(), valuesWeight,
				propertiesWeight, classesWeight, k);
	}

	@Override
	public SortedSet<MeasuredNode> match(BackgroundKnowledgeGraph graph, Collection<? extends NumberLikeValue> values,
			int k) {
		return match(graph, values, ImmutableList.of(), ImmutableList.of(), 1, 0, 0, k);
	}

	private SortedSet<MeasuredNode> match(final BackgroundKnowledgeGraph graph,
			final Collection<? extends NumberLikeValue> matchedValues,
			final Collection<? extends DeclaredEntity> matchedContextProperties,
			final Collection<? extends DeclaredEntity> matchedContextClasses, final double valuesWeight,
			final double propertiesWeight, final double classesWeight, final int k) {
		checkNotNull(graph);
		checkNotNull(matchedValues);
		checkNotNull(matchedContextProperties);
		checkNotNull(matchedContextClasses);
		checkArgument(k >= 1);
		checkArgument(valuesWeight >= 0);
		checkArgument(propertiesWeight >= 0);
		checkArgument(classesWeight >= 0);

		final Set<URI> matchedUniqueContextProperties = ImmutableSet.copyOf(
				matchedContextProperties.stream().map(p -> p.getUri()).collect(ImmutableList.toImmutableList()));
		final Set<URI> matchedUniqueContextClasses = ImmutableSet
				.copyOf(matchedContextClasses.stream().map(p -> p.getUri()).collect(ImmutableList.toImmutableList()));

		final double[] inputValues = matchedValues.stream().mapToDouble(e -> e.getFigure()).toArray();

		final PriorityQueue<MeasuredNode> winners = new PriorityQueue<>(k, Comparator.reverseOrder());

		for (final Property property : graph) {
			for (final PropertyTree instance : property) {
				for (final Node node : instance) {
					final Collection<URI> candidateContextProperties = node.getPropertyTree().getContext()
							.getDeclaredContextColumnProperties().values().stream().map(p -> p.getUri())
							.collect(ImmutableList.toImmutableList());
					final Set<URI> candidateContextUniqueProperties = ImmutableSet.copyOf(candidateContextProperties);

					final double propertiesNormalizedjaccardDissimilarity = getNormalizedJaccardDissimilarity(
							matchedUniqueContextProperties, candidateContextUniqueProperties);

					final Collection<URI> candidateContextClasses = node.getPropertyTree().getContext()
							.getDeclaredContextColumnClasses().values().stream().map(p -> p.getUri())
							.collect(ImmutableList.toImmutableList());
					final Set<URI> candidateContextUniqueClasses = ImmutableSet.copyOf(candidateContextClasses);

					final double classesNormalizedjaccardDissimilarity = getNormalizedJaccardDissimilarity(
							matchedUniqueContextClasses, candidateContextUniqueClasses);

					final double[] candidateValues = node.getValues().stream().mapToDouble(e -> e.getFigure())
							.toArray();

					final double computedDistance = distance.compute(inputValues, candidateValues);

					final double summedWeights = valuesWeight + propertiesWeight + classesWeight;
					final double normalizedValuesWeight = valuesWeight / summedWeights;
					final double normalizedPropertiesWeight = propertiesWeight / summedWeights;
					final double normalizedClassesWeight = classesWeight / summedWeights;

					final double measuredDistance = normalizedValuesWeight * computedDistance
							+ normalizedPropertiesWeight * propertiesNormalizedjaccardDissimilarity
							+ normalizedClassesWeight * classesNormalizedjaccardDissimilarity;

					final MeasuredNode candidateNode = this.measuredNodeFactory.create(node, measuredDistance);

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

	private static <T> double getNormalizedJaccardDissimilarity(final Set<? extends T> first,
			final Set<? extends T> second) {
		final SetView<? extends T> contextPropertiesIntersection = Sets.intersection(first, second);
		final int intersectionSize = contextPropertiesIntersection.size();

		final double jaccardSimilarity = ((double) intersectionSize)
				/ (first.size() + second.size() - intersectionSize);
		final double jaccardDissimilarity = 1 - jaccardSimilarity;

		final double normalizedjaccardDissimilarity = Double.isNaN(jaccardDissimilarity) ? 1 : jaccardDissimilarity;
		return normalizedjaccardDissimilarity;
	}

	@Override
	public SortedSet<MeasuredNode> match(BackgroundKnowledgeGraph graph, Node matchedNode) {
		return match(graph, matchedNode, defaultValuesWeight, defaultPropertiesWeight, defaultClassesWeight, defaultK);
	}

	@Override
	public SortedSet<MeasuredNode> match(BackgroundKnowledgeGraph graph, Collection<? extends NumberLikeValue> values) {
		return match(graph, values, defaultK);
	}
}
