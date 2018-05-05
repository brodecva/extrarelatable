package eu.odalic.extrarelatable.algorithms.graph;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.function.Function.identity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;

import eu.odalic.extrarelatable.model.annotation.Annotation;
import eu.odalic.extrarelatable.model.annotation.MeasuredNode;
import eu.odalic.extrarelatable.model.annotation.Statistics;
import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.graph.Property;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.graph.PropertyTree.Node;
import eu.odalic.extrarelatable.model.table.SlicedTable;

@Component
public final class DefaultAnnotator implements Annotator {

	private final PropertyTreeBuilder propertyTreeBuilder;
	private final TopKNodesMatcher topKNodesMatcher;
	private final ResultAggregator labelsResultAggregator;
	private final ResultAggregator propertiesResultAggregator;
	private final ResultAggregator pairsResultAggregator;
	private final int defaultK;

	public DefaultAnnotator(final PropertyTreeBuilder propertyTreeBuilder, final TopKNodesMatcher topKNodesMatcher,
			@Qualifier("averageDistance") final ResultAggregator propertiesResultAggregator,
			@Qualifier("averageDistance") final ResultAggregator labelsResultAggregator,
			@Qualifier("averageDistance") final ResultAggregator pairsResultAggregator, @Value("${eu.odalic.extrarelatable.topKAggregatedResults?:3}") final int defaultK) {
		checkNotNull(propertyTreeBuilder);
		checkNotNull(topKNodesMatcher);
		checkNotNull(propertiesResultAggregator);
		checkNotNull(labelsResultAggregator);
		checkNotNull(pairsResultAggregator);
		checkArgument(defaultK >= 1, "The default k must be at least one!");

		this.propertyTreeBuilder = propertyTreeBuilder;
		this.topKNodesMatcher = topKNodesMatcher;
		this.propertiesResultAggregator = propertiesResultAggregator;
		this.labelsResultAggregator = labelsResultAggregator;
		this.pairsResultAggregator = pairsResultAggregator;
		this.defaultK = defaultK;
	}

	@Override
	public Map<Integer, Annotation> annotate(final BackgroundKnowledgeGraph graph, final SlicedTable slicedTable,
			final int k) {
		checkNotNull(graph);
		checkNotNull(slicedTable);
		checkArgument(k >= 1, "The k must be at least one!");

		final ImmutableMap.Builder<Integer, Annotation> builder = ImmutableMap.builder();

		slicedTable.getDataColumns().keySet().forEach(columnIndex -> {
			final PropertyTree tree = this.propertyTreeBuilder.build(slicedTable, columnIndex, slicedTable.getMetadata().getCollectedProperties(), slicedTable.getMetadata().getCollectedClasses(), false);

			final ImmutableMultiset.Builder<MeasuredNode> treeMatchingNodesBuilder = ImmutableMultiset.builder();

			for (final Node node : tree) {
				final SortedSet<MeasuredNode> matchingNodes = this.topKNodesMatcher.match(graph, node);
				treeMatchingNodesBuilder.addAll(matchingNodes);
			}

			final Multiset<MeasuredNode> treeMatchingNodes = treeMatchingNodesBuilder.build();

			final SetMultimap<Label, MeasuredNode> labelLevelAggregates = treeMatchingNodes.stream()
					.collect(ImmutableSetMultimap.toImmutableSetMultimap(e -> e.getNode().getLabel(), identity()));
			final SortedSet<Label> labelAggregates = this.labelsResultAggregator.aggregate(labelLevelAggregates);
			final List<Label> labels = cutOff(labelAggregates, k);
			final Map<Label, Statistics> labelStatistics = getStatistics(labels, labelLevelAggregates);

			final SetMultimap<Property, MeasuredNode> propertyLevelAggregates = treeMatchingNodes.stream()
					.collect(ImmutableSetMultimap.toImmutableSetMultimap(e -> e.getNode().getProperty(), identity()));
			final SortedSet<Property> propertyAggregates = this.propertiesResultAggregator
					.aggregate(propertyLevelAggregates);
			final List<Property> properties = cutOff(propertyAggregates, k);
			final Map<Property, Statistics> propertyStatistics = getStatistics(properties, propertyLevelAggregates);

			final SetMultimap<Set<AttributeValuePair>, MeasuredNode> pairLevelAggregates = treeMatchingNodes.stream()
					.collect(ImmutableSetMultimap.toImmutableSetMultimap(e -> e.getNode().getPairs(), identity()));
			final SortedSet<Set<AttributeValuePair>> pairAggregates = this.pairsResultAggregator
					.aggregate(pairLevelAggregates, eu.odalic.extrarelatable.util.Sets.comparator());
			final List<Set<AttributeValuePair>> pairs = cutOff(pairAggregates, k);
			final Map<Set<AttributeValuePair>, Statistics> pairsStatistics = getStatistics(pairs, pairLevelAggregates);

			builder.put(columnIndex,
					Annotation.of(properties, labels, pairs, propertyStatistics, labelStatistics, pairsStatistics));
		});

		return builder.build();
	}

	private static <T> List<T> cutOff(final Collection<T> aggregates, final int limit) {
		return ImmutableList.copyOf(aggregates).subList(0, Math.min(aggregates.size(), limit));
	}

	private static <T> Map<T, Statistics> getStatistics(final List<T> elements,
			final SetMultimap<T, MeasuredNode> labelLevelAggregates) {
		final ImmutableMap.Builder<T, Statistics> builder = ImmutableMap.builder();
		for (final T element : elements) {
			final Set<MeasuredNode> nodes = labelLevelAggregates.get(element);

			final double[] distances = nodes.stream().mapToDouble(e -> e.getDistance()).toArray();

			final double average = new Mean().evaluate(distances);
			final double median = new Median().evaluate(distances);
			final int occurence = nodes.size();
			final double relativeOccurence = occurence / (double) (labelLevelAggregates.size());

			final Statistics statistics = Statistics.of(average, median, occurence, relativeOccurence);
			builder.put(element, statistics);
		}

		return builder.build();
	}

	@Override
	public Map<Integer, Annotation> annotate(final BackgroundKnowledgeGraph graph, final SlicedTable slicedTable) {
		return annotate(graph, slicedTable, this.defaultK);
	}
}
