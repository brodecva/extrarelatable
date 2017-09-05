/**
 * 
 */
package eu.odalic.extrarelatable.experiments;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import eu.odalic.extrarelatable.algorithms.subcontexts.SubcontextCompiler;
import eu.odalic.extrarelatable.algorithms.subcontexts.SubcontextMatcher;
import eu.odalic.extrarelatable.input.TableAnalyzer;
import eu.odalic.extrarelatable.input.TableParser;
import eu.odalic.extrarelatable.input.TableSlicer;
import eu.odalic.extrarelatable.input.csv.Format;
import eu.odalic.extrarelatable.model.bag.Attribute;
import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.NumericValue;
import eu.odalic.extrarelatable.model.bag.TextValue;
import eu.odalic.extrarelatable.model.bag.Value;
import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.graph.PropertyTree.CommonNode;
import eu.odalic.extrarelatable.model.graph.PropertyTree.RootNode;
import eu.odalic.extrarelatable.model.graph.PropertyTree.SharedPairNode;
import eu.odalic.extrarelatable.model.histogram.Partition;
import eu.odalic.extrarelatable.model.histogram.Subcontext;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.ParsedTable;

/**
 * @author Václav Brodec
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml" })
public class Baseline {

	private static final double RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD = 0.9;
	private static final String RESOURCES_PATH = "src/test/resources";
	private static final String SET_SUBPATH = "votes";
	private static final double MINIMUM_PARTITION_RELATIVE_SIZE = 0.01;
	private static final double MAXIMUM_PARTITION_RELATIVE_SIZE = 0.99;

	@Autowired
	@Lazy
	private TableParser tableReader;

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
		final Path testSetPath = resourcesPath.resolve(SET_SUBPATH);

		final BackgroundKnowledgeGraph.Builder graphBuilder = BackgroundKnowledgeGraph.builder();

		Files.newDirectoryStream(testSetPath).forEach(file -> {
			final Set<PropertyTree> trees = readFile(file, new Format());

			graphBuilder.addAll(trees);
		});
		graphBuilder.build();
		
		//TODO Process the files.
		fail("Not yet implemented");
	}

	private Set<PropertyTree> readFile(final Path input, final Format format) {
		/* Parse the input file to table. */
		final ParsedTable table;
		try (final InputStream inputStream = Files.newInputStream(input)) {
			table = tableReader.parse(inputStream, format, new Metadata());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		/* Assign data types to each table cell. */
		final TypedTable parsedTable = tableAnalyzer.infer(table, Locale.GERMAN);

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, parsedTable);

		/*
		 * For each numeric column and its set of numeric values compute the
		 * possible sub-contexts and order them by distance in descending order
		 * from the set.
		 * 
		 * Use the farthest sub-context to partition the set of values into
		 * nodes and recursively compute the sub-context for them.
		 */
		final ImmutableSet.Builder<PropertyTree> propertyTreesBuilder = ImmutableSet.builder();

		final Set<Integer> availableContextColumnIndices = slicedTable.getTextualColumns().keySet();
		
		for (final Entry<Integer, List<Value>> numericColumn : slicedTable.getNumericColumns().entrySet()) {
			final int columnIndex = numericColumn.getKey();
			final Label label = slicedTable.getHeaders().get(columnIndex);
			
			final Partition partition = new Partition(numericColumn.getValue().stream().filter(e -> e.isNumeric()).map(e -> (NumericValue) e).collect(ImmutableList.toImmutableList()));
			
			final Set<CommonNode> children = buildChildren(partition, availableContextColumnIndices, slicedTable);

			final RootNode rootNode = new RootNode(label, ImmutableMultiset.copyOf(partition.getValues()), children);
			final PropertyTree tree = new PropertyTree(rootNode);
			propertyTreesBuilder.add(tree);
		}

		return propertyTreesBuilder.build();
	}

	private Set<CommonNode> buildChildren(final Partition partition, final Set<Integer> availableContextColumnIndices, final TypedTable table) {
		final Set<Subcontext> subcontexts = subcontextCompiler.compile(partition, availableContextColumnIndices, table, MINIMUM_PARTITION_RELATIVE_SIZE,
				MAXIMUM_PARTITION_RELATIVE_SIZE);
		if (subcontexts.isEmpty()) {
			return ImmutableSet.of();
		}
		
		final ImmutableSet.Builder<CommonNode> children = ImmutableSet.builder();
		
		final Subcontext winningSubcontext = subcontextMatcher.match(subcontexts, partition);
		final Attribute subattribute = winningSubcontext.getAttribute();
		final int parentalPartitionSize = partition.size();
		
		for (final Entry<TextValue, Partition> partitionEntry : winningSubcontext.getPartitions().entrySet()) {
			final Partition subpartition = partitionEntry.getValue();
			final int subpartitionSize = subpartition.size();
			if (subpartitionSize < MINIMUM_PARTITION_RELATIVE_SIZE * parentalPartitionSize) {
				continue;
			}
			if (subpartitionSize > MAXIMUM_PARTITION_RELATIVE_SIZE * parentalPartitionSize) {
				continue;
			}
			
			final int usedContextColumnIndex = winningSubcontext.getColumnIndex();

			final Set<CommonNode> subchildren = buildChildren(subpartition,
					Sets.difference(availableContextColumnIndices, ImmutableSet.of(usedContextColumnIndex)), table);
			
			final TextValue subvalue = partitionEntry.getKey();
			final SharedPairNode subtree = new SharedPairNode(new AttributeValuePair(subattribute, subvalue), ImmutableMultiset.copyOf(partition.getValues()), subchildren);
			
			children.add(subtree);
		}

		return children.build();
	}
}
