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
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import eu.odalic.extrarelatable.algorithms.graph.ResultAggregator;
import eu.odalic.extrarelatable.algorithms.graph.TopKNodesMatcher;
import eu.odalic.extrarelatable.algorithms.subcontext.SubcontextCompiler;
import eu.odalic.extrarelatable.algorithms.subcontext.SubcontextMatcher;
import eu.odalic.extrarelatable.algorithms.table.TableAnalyzer;
import eu.odalic.extrarelatable.algorithms.table.TableSlicer;
import eu.odalic.extrarelatable.algorithms.table.csv.CsvTableParser;
import eu.odalic.extrarelatable.model.annotation.Annotation;
import eu.odalic.extrarelatable.model.annotation.MeasuredNode;
import eu.odalic.extrarelatable.model.bag.Attribute;
import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Context;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.NumericValue;
import eu.odalic.extrarelatable.model.bag.Value;
import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.graph.Property;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.graph.PropertyTree.CommonNode;
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
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml" })
public class Trivial {

	private static final double RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD = 0.9;
	private static final String RESOURCES_PATH = "src/test/resources";
	private static final String SET_SUBPATH = "health";
	private static final String LEARN_SUBPATH = "learn";
	private static final String TEST_SUBPATH = "test";
	private static final double MINIMUM_PARTITION_RELATIVE_SIZE = 0.01;
	private static final double MAXIMUM_PARTITION_RELATIVE_SIZE = 0.99;
	private static final int TOP_K_NEGHBOURS = 5;
	private static final int TOP_K_AGGREGATED_RESULTS = 1;
	private static final int MINIMUM_PARTITION_SIZE = 2;

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
	private ResultAggregator<MeasuredNode> propertiesResultAggregator;
	
	@Autowired
	@Lazy
	@Qualifier("majorityVote")
	private ResultAggregator<MeasuredNode> labelsResultAggregator;
	
	@Autowired
	@Lazy
	@Qualifier("majorityVote")
	private ResultAggregator<MeasuredNode> pairsResultAggregator;
	
	@Autowired
	@Lazy
	@Qualifier("labelText")
	private PropertyTreesMergingStrategy propertyTreesMergingStrategy;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		final Path resourcesPath = Paths.get(RESOURCES_PATH);
		final Path setPath = resourcesPath.resolve(SET_SUBPATH);
		final Path learnSetPath = setPath.resolve(LEARN_SUBPATH);
		final Path testSetPath = setPath.resolve(TEST_SUBPATH);

		final BackgroundKnowledgeGraph graph = learn(learnSetPath);
		test(testSetPath, graph);
	}


	private BackgroundKnowledgeGraph learn(final Path learnSetPath) throws IOException {
		final BackgroundKnowledgeGraph graph = new BackgroundKnowledgeGraph(propertyTreesMergingStrategy);

		Files.newDirectoryStream(learnSetPath).forEach(file -> {
			final Set<PropertyTree> trees = readFile(file, null);

			graph.addPropertyTrees(trees);
		});
		return graph;
	}
	
	private void test(final Path testSetPath, final BackgroundKnowledgeGraph graph) throws IOException {
		Files.newDirectoryStream(testSetPath).forEach(file -> {
			final Map<Integer, Annotation> columnIndicesToAnnotations = annotateFile(file, null, graph);
			
			System.out.println("File: " + file);
			columnIndicesToAnnotations.entrySet().forEach(e -> {
				final Annotation annotation = e.getValue();
				
				System.out.println("--------------------------------------------------------------------------------");
				System.out.println("Index:" + e.getKey());
				System.out.println("Properties: " + annotation.getProperties().stream().map(property ->
						property.getInstances().stream().map(instance ->
							instance.getRoot().getLabel().getText()
						).collect(Collectors.joining(", "))
					).collect(Collectors.joining("; "))
				);
				System.out.println("Labels: " + annotation.getLabels().stream().map(label ->
						label.getText()
					).collect(Collectors.joining(";"))
				);
				System.out.println("Pairs: " + annotation.getAttributeValuePairs().stream().map(pairs ->
						pairs.stream().map(pair ->
							pair.getAttribute().getName() + ":" + pair.getValue().getText()
						).collect(Collectors.joining(", "))
					).collect(Collectors.joining("; "))
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
			throw new RuntimeException(e);
		}
		if (table.getHeight() < 2) {
			return ImmutableSet.of();
		}

		/* Assign data types to each table cell. */
		final TypedTable parsedTable = tableAnalyzer.infer(table, Locale.GERMAN);
		
		
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
			
			final Set<CommonNode> children = buildChildren(partition, availableContextColumnIndices, slicedTable);
			
			final RootNode rootNode = new RootNode(label, ImmutableMultiset.copyOf(partition.getValues()));
			rootNode.addChildren(children);
			
			final PropertyTree tree = new PropertyTree(rootNode, context);
			rootNode.setPropertyTree(tree);
			propertyTreesBuilder.add(tree);
		}

		return propertyTreesBuilder.build();
	}

	private Set<CommonNode> buildChildren(final Partition partition, final Set<Integer> availableContextColumnIndices, final TypedTable table) {
		final Set<Subcontext> subcontexts = subcontextCompiler.compile(partition, availableContextColumnIndices, table, MINIMUM_PARTITION_RELATIVE_SIZE,
				MAXIMUM_PARTITION_RELATIVE_SIZE, MINIMUM_PARTITION_SIZE);
		if (subcontexts.isEmpty()) {
			return ImmutableSet.of();
		}
		
		final ImmutableSet.Builder<CommonNode> children = ImmutableSet.builder();
		
		final Subcontext winningSubcontext = subcontextMatcher.match(subcontexts, partition, MINIMUM_PARTITION_RELATIVE_SIZE,
				MAXIMUM_PARTITION_RELATIVE_SIZE, MINIMUM_PARTITION_SIZE);
		if (winningSubcontext == null) {
			return ImmutableSet.of();
		}
		
		final Attribute subattribute = winningSubcontext.getAttribute();
		final int parentalPartitionSize = partition.size();
		
		for (final Entry<Value, Partition> partitionEntry : winningSubcontext.getPartitions().entrySet()) {
			final Partition subpartition = partitionEntry.getValue();
			final int subpartitionSize = subpartition.size();
			if (subpartitionSize < MINIMUM_PARTITION_RELATIVE_SIZE * parentalPartitionSize) {
				continue;
			}
			if (subpartitionSize > MAXIMUM_PARTITION_RELATIVE_SIZE * parentalPartitionSize) {
				continue;
			}
			if (subpartitionSize < MINIMUM_PARTITION_SIZE) {
				continue;
			}

			final int usedContextColumnIndex = winningSubcontext.getColumnIndex();

			final Set<CommonNode> subchildren = buildChildren(subpartition,
					Sets.difference(availableContextColumnIndices, ImmutableSet.of(usedContextColumnIndex)), table);
			
			final Value subvalue = partitionEntry.getKey();
			final SharedPairNode subtree = new SharedPairNode(new AttributeValuePair(subattribute, subvalue), ImmutableMultiset.copyOf(subpartition.getValues()));
			subtree.addChildren(subchildren);
			
			children.add(subtree);
		}

		return children.build();
	}
	
	private Map<Integer, Annotation> annotateFile(final Path input, final Format format, final BackgroundKnowledgeGraph graph) {
		/* Parse the input file to table. */
		final ParsedTable table;
		try (final InputStream inputStream = new BufferedInputStream(Files.newInputStream(input))) {
			table = csvTableParser.parse(inputStream, format, new Metadata(input.getFileName().toString(), null, null));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		if (table.getHeight() < 2) {
			throw new IllegalArgumentException("Too small table to annotate!");
		}

		/* Assign data types to each table cell. */
		final TypedTable parsedTable = tableAnalyzer.infer(table, Locale.GERMAN);

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, parsedTable);

		final ImmutableMap.Builder<Integer, Annotation> builder = ImmutableMap.builder();
		
		for (final Entry<Integer, List<Value>> numericColumn : slicedTable.getDataColumns().entrySet()) {
			final int columnIndex = numericColumn.getKey();
			
			final List<NumericValue> numericValues = numericColumn.getValue().stream().filter(e -> e.isNumeric()).map(e -> (NumericValue) e).collect(ImmutableList.toImmutableList());
			
			final SortedSet<MeasuredNode> matchingNodes = topKNodesMatcher.match(graph, numericValues, TOP_K_NEGHBOURS);
			
			final SetMultimap<Property, MeasuredNode> propertyLevelAggregates = matchingNodes.stream().collect(ImmutableSetMultimap.toImmutableSetMultimap(e -> e.getNode().getProperty(), identity()));
			final SortedSet<Property> propertyAggregates = propertiesResultAggregator.aggregate(propertyLevelAggregates);
			final List<Property> properties = cutOff(propertyAggregates);
			
			final SetMultimap<Label, MeasuredNode> labelLevelAggregates = matchingNodes.stream().collect(ImmutableSetMultimap.toImmutableSetMultimap(e -> e.getNode().getLabel(), identity()));
			final SortedSet<Label> labelAggregates = labelsResultAggregator.aggregate(labelLevelAggregates);
			final List<Label> labels = cutOff(labelAggregates);
			
			final SetMultimap<Set<AttributeValuePair>, MeasuredNode> pairLevelAggregates = matchingNodes.stream().collect(ImmutableSetMultimap.toImmutableSetMultimap(e -> e.getNode().getPairs(), identity()));
			final SortedSet<Set<AttributeValuePair>> pairAggregates = pairsResultAggregator.aggregate(pairLevelAggregates, eu.odalic.extrarelatable.util.Sets.comparator());
			final List<Set<AttributeValuePair>> pairs = cutOff(pairAggregates);
			
			builder.put(columnIndex, Annotation.of(properties, labels, pairs));
		}

		return builder.build();
	}

	private <T> List<T> cutOff(final Collection<T> labelAggregates) {
		return ImmutableList.copyOf(labelAggregates).subList(0,  Math.min(labelAggregates.size(), TOP_K_AGGREGATED_RESULTS));
	}
}
