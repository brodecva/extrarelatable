/**
 * 
 */
package eu.odalic.extrarelatable.experiments;

import static java.util.function.Function.identity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;

import eu.odalic.extrarelatable.algorithms.graph.ResultAggregator;
import eu.odalic.extrarelatable.algorithms.graph.TopKNodesMatcher;
import eu.odalic.extrarelatable.algorithms.subcontext.SubcontextCompiler;
import eu.odalic.extrarelatable.algorithms.subcontext.SubcontextMatcher;
import eu.odalic.extrarelatable.algorithms.table.TableAnalyzer;
import eu.odalic.extrarelatable.algorithms.table.TableSlicer;
import eu.odalic.extrarelatable.algorithms.table.csv.CsvTableParser;
import eu.odalic.extrarelatable.model.annotation.Annotation;
import eu.odalic.extrarelatable.model.annotation.MeasuredNode;
import eu.odalic.extrarelatable.model.annotation.Statistics;
import eu.odalic.extrarelatable.model.bag.Attribute;
import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Context;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.NumericValue;
import eu.odalic.extrarelatable.model.bag.Value;
import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.graph.PropertyTree.CommonNode;
import eu.odalic.extrarelatable.model.graph.PropertyTree.Node;
import eu.odalic.extrarelatable.model.graph.PropertyTree.RootNode;
import eu.odalic.extrarelatable.model.graph.PropertyTree.SharedPairNode;
import eu.odalic.extrarelatable.model.graph.PropertyTreesMergingStrategy;
import eu.odalic.extrarelatable.model.subcontext.Partition;
import eu.odalic.extrarelatable.model.subcontext.Subcontext;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;
import eu.odalic.extrarelatable.model.table.csv.Format;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.ParsedTable;

/**
 * @author Václav Brodec
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/testApplicationContext.xml" })
public class TreeMajorityVoteLabelsOnlyRandomDataGvAt {

	private static final double RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD = 0.6;
	private static final String RESOURCES_PATH = "I:/";
	private static final String SET_SUBPATH = "g";
	private static final double MINIMUM_PARTITION_RELATIVE_SIZE = 0.01;
	private static final double MAXIMUM_PARTITION_RELATIVE_SIZE = 0.99;
	private static final int TOP_K_NEGHBOURS = 25;
	private static final int TOP_K_AGGREGATED_RESULTS = 5;
	private static final int MINIMUM_PARTITION_SIZE = 2;
	private static final int SAMPLE_SIZE = 10;
	private static final long SEED = 5;

	@Autowired
	@Lazy
	@Qualifier("automatic")
	private CsvTableParser csvTableParser;

	@Autowired
	@Lazy
	private TableAnalyzer tableAnalyzer;
	
	@Autowired
	@Lazy
	private TableSlicer tableSlicer;

	@Autowired
	@Lazy
	private SubcontextCompiler subcontextCompiler;

	@Autowired
	@Lazy
	private SubcontextMatcher subcontextMatcher;
	
	@Autowired
	@Lazy
	private TopKNodesMatcher topKNodesMatcher;
	
	@Autowired
	@Lazy
	@Qualifier("majorityVote")
	private ResultAggregator labelsResultAggregator;
	
	@Autowired
	@Lazy
	@Qualifier("labelText")
	private PropertyTreesMergingStrategy propertyTreesMergingStrategy;

	@Test
	public void test() throws IOException {
		final Random random = new Random(SEED);
		
		final Path resourcesPath = Paths.get(RESOURCES_PATH);
		final Path setPath = resourcesPath.resolve(SET_SUBPATH);

		final List<Path> paths = Streams.stream(Files.newDirectoryStream(setPath)).collect(Collectors.toCollection(ArrayList::new));
		
		final ImmutableList.Builder<Path> testPathsBuilder = ImmutableList.builder();
		for (int i = 0; i < SAMPLE_SIZE; i++) {
			final int removedIndex = random.nextInt(paths.size());
			
			testPathsBuilder.add(paths.remove(removedIndex));
		}
		final List<Path> testPaths = testPathsBuilder.build();
		
		final Set<String> unversionedTestFiles = testPaths.stream().map(e -> toUnversionedFileName(e)).collect(ImmutableSet.toImmutableSet());
		
		final List<Path> learningPaths = paths.stream().filter(e -> !unversionedTestFiles.contains(toUnversionedFileName(e))).collect(ImmutableList.toImmutableList());
		
		final BackgroundKnowledgeGraph graph = learn(learningPaths);
		test(testPaths, graph);
	}


	private static String toUnversionedFileName(final Path path) {
		return path.getFileName().toString().replaceAll("[0-9]+", "");
	}


	private BackgroundKnowledgeGraph learn(final Collection<? extends Path> paths) throws IOException {
		final BackgroundKnowledgeGraph graph = new BackgroundKnowledgeGraph(propertyTreesMergingStrategy);

		paths.forEach(file -> {
			final Set<PropertyTree> trees = readFile(file, null);

			graph.addPropertyTrees(trees);
		});
		return graph;
	}
	
	private void test(final Collection<? extends Path> paths, final BackgroundKnowledgeGraph graph) throws IOException {
		paths.forEach(file -> {
			final ParsedTable parsedTable = parse(file, null);
			final Map<Integer, Annotation> columnIndicesToAnnotations = annotateTable(parsedTable, graph);
			
			System.out.println("File: " + file);
			System.out.println("Headers: ");
			System.out.println(parsedTable.getHeaders().stream().collect(Collectors.joining(";")));
			System.out.println("First rows: ");
			System.out.println(parsedTable.getRows().subList(0, Math.min(parsedTable.getHeight(), 5)).stream().map(row -> Joiner.on(";").join(row)).collect(Collectors.joining("\n")));
			columnIndicesToAnnotations.entrySet().forEach(e -> {
				final int index = e.getKey();
				final Annotation annotation = e.getValue();
				final Map<Label, Statistics> labelsStatistics = annotation.getLabelsStatistics();
				
				System.out.println("--------------------------------------------------------------------------------");
				System.out.println("Index:" + index);
				System.out.println("Header:" + parsedTable.getHeaders().get(index));
				System.out.println("First values:");
				System.out.println(parsedTable.getColumn(index).subList(0, Math.min(parsedTable.getHeight(), 5)).stream().collect(Collectors.joining("\t")));
				System.out.println("Labels:");
				System.out.println("Mean distance\tMedian distance\tOccurence\tRelative occurence\tText\tIndex\tFile\tFirst values\tFirst rows");
				System.out.println(annotation.getLabels().stream().map(label -> {
						final Statistics statistics = labelsStatistics.get(label);
						
						return Joiner.on("\t").join(
							statistics.getAverage(),
							statistics.getMedian(),
							statistics.getOccurence(),
							statistics.getRelativeOccurence(),
							label.getText(),
							label.getIndex(),
							label.getFile(),
							label.getFirstValues(),
							label.getFirstRows()
						);
					}).collect(Collectors.joining("\n"))
				);
			});
			System.out.println("================================================================================");
		});
	}

	private Set<PropertyTree> readFile(final Path input, final Format format) {
		System.out.println("Processing file " + input + "...");
		
		/* Parse the input file to table. */
		final ParsedTable table;
		try (final InputStream inputStream = new BufferedInputStream(Files.newInputStream(input))) {
			table = csvTableParser.parse(inputStream, format, new Metadata(input.getFileName().toString(), null, null));
		} catch (final IOException e) {
			System.out.println("Failed to parse " + input + "!");
			e.printStackTrace();
			return ImmutableSet.of();
		}
		
		if (table.getHeight() < 2) {
			return ImmutableSet.of();
		}

		/* Assign data types to each table cell. */
		final TypedTable parsedTable = tableAnalyzer.infer(table, Locale.GERMAN);
		if (parsedTable.getHeight() < 2) {
			return ImmutableSet.of();
		}

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, parsedTable);

		final Context context = new Context(slicedTable.getHeaders(), slicedTable.getMetadata().getAuthor(), slicedTable.getMetadata().getTitle());
		
		/*
		 * For each numeric column and its set of numeric values compute the
		 * possible sub-contexts and order them by distance in descending order
		 * from the set.
		 * 
		 * Use the farthest sub-context to partition the set of values into
		 * nodes and recursively compute the sub-context for them.
		 */
		final ImmutableSet.Builder<PropertyTree> propertyTreesBuilder = ImmutableSet.builder();

		final Set<Integer> availableContextColumnIndices = slicedTable.getContextColumns().keySet();
		
		for (final Entry<Integer, List<Value>> numericColumn : slicedTable.getDataColumns().entrySet()) {
			final int columnIndex = numericColumn.getKey();
			final Label label = slicedTable.getHeaders().get(columnIndex);
			
			final Partition partition = new Partition(numericColumn.getValue().stream().filter(e -> e.isNumeric()).map(e -> (NumericValue) e).collect(ImmutableList.toImmutableList()));
			if (partition.size() < MINIMUM_PARTITION_SIZE) {
				continue;
			}
			
			final Set<CommonNode> children = buildChildren(partition, availableContextColumnIndices, slicedTable, MINIMUM_PARTITION_RELATIVE_SIZE, MAXIMUM_PARTITION_RELATIVE_SIZE, MINIMUM_PARTITION_SIZE);
			
			final RootNode rootNode = new RootNode(label, ImmutableMultiset.copyOf(partition.getValues()));
			rootNode.addChildren(children);
			
			final PropertyTree tree = new PropertyTree(rootNode, context);
			rootNode.setPropertyTree(tree);
			propertyTreesBuilder.add(tree);
		}

		return propertyTreesBuilder.build();
	}

	private Set<CommonNode> buildChildren(final Partition partition, final Set<Integer> availableContextColumnIndices, final TypedTable table,
			final double minimumPartitionRelativeSize, final double maximumPartitionRelativeSize, final int minimumPartitionSize) {
		final Set<Subcontext> subcontexts = subcontextCompiler.compile(partition, availableContextColumnIndices, table, minimumPartitionRelativeSize,
				maximumPartitionRelativeSize, minimumPartitionSize);
		if (subcontexts.isEmpty()) {
			return ImmutableSet.of();
		}
		
		final ImmutableSet.Builder<CommonNode> children = ImmutableSet.builder();
		
		final Subcontext winningSubcontext = subcontextMatcher.match(subcontexts, partition, minimumPartitionRelativeSize,
				maximumPartitionRelativeSize, minimumPartitionSize);
		if (winningSubcontext == null) {
			return ImmutableSet.of();
		}
		
		final Attribute subattribute = winningSubcontext.getAttribute();
		final int parentalPartitionSize = partition.size();
		
		for (final Entry<Value, Partition> partitionEntry : winningSubcontext.getPartitions().entrySet()) {
			final Partition subpartition = partitionEntry.getValue();
			final int subpartitionSize = subpartition.size();
			if (subpartitionSize < minimumPartitionRelativeSize * parentalPartitionSize) {
				continue;
			}
			if (subpartitionSize > maximumPartitionRelativeSize * parentalPartitionSize) {
				continue;
			}
			if (subpartitionSize < minimumPartitionSize) {
				continue;
			}
			
			final int usedContextColumnIndex = winningSubcontext.getColumnIndex();

			final Set<CommonNode> subchildren = buildChildren(subpartition,
					Sets.difference(availableContextColumnIndices, ImmutableSet.of(usedContextColumnIndex)), table, minimumPartitionRelativeSize,
				maximumPartitionRelativeSize, minimumPartitionSize);
			
			final Value subvalue = partitionEntry.getKey();
			final SharedPairNode subtree = new SharedPairNode(new AttributeValuePair(subattribute, subvalue), ImmutableMultiset.copyOf(partition.getValues()));
			subtree.addChildren(subchildren);
			
			children.add(subtree);
		}

		return children.build();
	}
	
	private ParsedTable parse(final Path input, final Format format) {
		System.out.println("Processing file " + input + "...");
		
		/* Parse the input file to table. */
		try (final InputStream inputStream = new BufferedInputStream(Files.newInputStream(input))) {
			return csvTableParser.parse(inputStream, format, new Metadata(input.getFileName().toString(), null, null));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Map<Integer, Annotation> annotateTable(final ParsedTable table, final BackgroundKnowledgeGraph graph) {
		if (table.getHeight() < 2) {
			throw new IllegalArgumentException("The table to annotate must have at least two rows!");
		}

		/* Assign data types to each table cell. */
		final TypedTable parsedTable = tableAnalyzer.infer(table, Locale.GERMAN);
		if (parsedTable.getHeight() < 2) {
			throw new IllegalArgumentException("The table to annotate must have at least two rows!");
		}

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, parsedTable);

		final Context context = new Context(slicedTable.getHeaders(), slicedTable.getMetadata().getAuthor(), slicedTable.getMetadata().getTitle());
		
		final ImmutableMap.Builder<Integer, Annotation> builder = ImmutableMap.builder();

		final Set<Integer> availableContextColumnIndices = slicedTable.getContextColumns().keySet();
		
		for (final Entry<Integer, List<Value>> numericColumn : slicedTable.getDataColumns().entrySet()) {
			final int columnIndex = numericColumn.getKey();
			final Label label = slicedTable.getHeaders().get(columnIndex);
			
			final Partition partition = new Partition(numericColumn.getValue().stream().filter(e -> e.isNumeric()).map(e -> (NumericValue) e).collect(ImmutableList.toImmutableList()));
			if (partition.size() < MINIMUM_PARTITION_SIZE) {
				continue;
			}
			
			final Set<CommonNode> children = buildChildren(partition, availableContextColumnIndices, slicedTable, MINIMUM_PARTITION_RELATIVE_SIZE, MAXIMUM_PARTITION_RELATIVE_SIZE, MINIMUM_PARTITION_SIZE);
			
			final RootNode rootNode = new RootNode(label, ImmutableMultiset.copyOf(partition.getValues()));
			rootNode.addChildren(children);
			
			final PropertyTree tree = new PropertyTree(rootNode, context);
			rootNode.setPropertyTree(tree);
			
			final ImmutableMultiset.Builder<MeasuredNode> treeMatchingNodesBuilder = ImmutableMultiset.builder(); 
			
			for (final Node node : tree) {
				final SortedSet<MeasuredNode> matchingNodes = topKNodesMatcher.match(graph, node.getValues(), TOP_K_NEGHBOURS);
				treeMatchingNodesBuilder.addAll(matchingNodes);
			}
			
			final Multiset<MeasuredNode> treeMatchingNodes = treeMatchingNodesBuilder.build();
			
			final SetMultimap<Label, MeasuredNode> labelLevelAggregates = treeMatchingNodes.stream().collect(ImmutableSetMultimap.toImmutableSetMultimap(e -> e.getNode().getLabel(), identity()));
			final SortedSet<Label> labelAggregates = labelsResultAggregator.aggregate(labelLevelAggregates);
			final List<Label> labels = cutOff(labelAggregates);
			
			final Map<Label, Statistics> statistics = getStatistics(labels, labelLevelAggregates);
			
			builder.put(columnIndex, Annotation.of(ImmutableList.of(), labels, ImmutableList.of(), ImmutableMap.of(), statistics, ImmutableMap.of()));
		}

		return builder.build();
	}
	
	private static Map<Label, Statistics> getStatistics(final List<Label> labels,
			final SetMultimap<Label, MeasuredNode> labelLevelAggregates) {
		final ImmutableMap.Builder<Label, Statistics> builder = ImmutableMap.builder();
		for (final Label label : labels) {
			final Set<MeasuredNode> nodes = labelLevelAggregates.get(label);
			
			final double[] distances = nodes.stream().mapToDouble(e -> e.getDistance()).toArray();
			
			final double average = new Mean().evaluate(distances);
			final double median = new Median().evaluate(distances);
			final int occurence = nodes.size();
			final double relativeOccurence = occurence / (double) (labelLevelAggregates.size());
			
			final Statistics statistics = Statistics.of(average, median, occurence, relativeOccurence);
			builder.put(label, statistics);
		}
		
		return builder.build();
	}


	private <T> List<T> cutOff(final Collection<T> labelAggregates) {
		return ImmutableList.copyOf(labelAggregates).subList(0,  Math.min(labelAggregates.size(), TOP_K_AGGREGATED_RESULTS));
	}
}
