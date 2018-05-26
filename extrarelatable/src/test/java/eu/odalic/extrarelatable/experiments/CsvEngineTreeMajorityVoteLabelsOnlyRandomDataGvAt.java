/**
 * 
 */
package eu.odalic.extrarelatable.experiments;

import static java.util.function.Function.identity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import eu.odalic.extrarelatable.model.annotation.AnnotationResult;
import eu.odalic.extrarelatable.model.annotation.MeasuredNode;
import eu.odalic.extrarelatable.model.annotation.Statistics;
import eu.odalic.extrarelatable.model.bag.Attribute;
import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Context;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.NumericValue;
import eu.odalic.extrarelatable.model.bag.Type;
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
import eu.odalic.extrarelatable.services.csvengine.csvclean.CsvCleanService;
import eu.odalic.extrarelatable.services.csvengine.csvprofiler.CsvProfile;
import eu.odalic.extrarelatable.services.csvengine.csvprofiler.CsvProfilerService;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.ParsedTable;

/**
 * @author Václav Brodec
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/testApplicationContext.xml" })
public class CsvEngineTreeMajorityVoteLabelsOnlyRandomDataGvAt {

	private static final double RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD = 0.6;
	private static final String RESOURCES_PATH = "H:/";
	private static final String SET_SUBPATH = "g";
	private static final double MINIMUM_PARTITION_RELATIVE_SIZE = 0.01;
	private static final double MAXIMUM_PARTITION_RELATIVE_SIZE = 0.99;
	private static final int TOP_K_NEGHBOURS = 25;
	private static final int TOP_K_AGGREGATED_RESULTS = 5;
	private static final int MINIMUM_PARTITION_SIZE = 2;
	private static final int SAMPLE_SIZE = 10;
	private static final long SEED = 5;
	private static final String CLEANED_INPUT_FILES_DIRECTORY = "cleaned";
	private static final String PROFILES_DIRECTORY = "profiles";

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
	private ResultAggregator<MeasuredNode> labelsResultAggregator;
	
	@Autowired
	@Lazy
	@Qualifier("labelText")
	private PropertyTreesMergingStrategy propertyTreesMergingStrategy;
	
	@Autowired
	@Lazy
	private CsvProfilerService csvProfilerService;
	
	@Autowired
	@Lazy
	private CsvCleanService csvCleanService;

	@Test
	public void test() throws IOException {
		final Random random = new Random(SEED);
		
		final Path resourcesPath = Paths.get(RESOURCES_PATH);
		final Path setPath = resourcesPath.resolve(SET_SUBPATH);

		final List<Path> paths = Streams.stream(Files.newDirectoryStream(setPath)).filter(path -> path.toFile().isFile()).collect(Collectors.toCollection(ArrayList::new));
		
		Collections.sort(paths);
		
		final ImmutableList.Builder<Path> testPathsBuilder = ImmutableList.builder();
		for (int i = 0; i < SAMPLE_SIZE; i++) {
			final int removedIndex = random.nextInt(paths.size());
			
			testPathsBuilder.add(paths.remove(removedIndex));
		}
		final List<Path> testPaths = testPathsBuilder.build();
		
		final Set<String> unversionedTestFiles = testPaths.stream().map(e -> toUnversionedFileName(e)).collect(ImmutableSet.toImmutableSet());
		
		final List<Path> learningPaths = paths.stream().filter(e -> !unversionedTestFiles.contains(toUnversionedFileName(e))).collect(ImmutableList.toImmutableList());
		
		final BackgroundKnowledgeGraph graph = learn(learningPaths, setPath.resolve(CLEANED_INPUT_FILES_DIRECTORY), setPath.resolve(PROFILES_DIRECTORY));
		test(testPaths, graph, setPath.resolve(CLEANED_INPUT_FILES_DIRECTORY), setPath.resolve(PROFILES_DIRECTORY));
	}


	private static String toUnversionedFileName(final Path path) {
		return path.getFileName().toString().replaceAll("[0-9]+", "");
	}


	private BackgroundKnowledgeGraph learn(final Collection<? extends Path> paths, final Path cleanedInputFilesDirectory, final Path profilesDirectory) throws IOException {
		final BackgroundKnowledgeGraph graph = new BackgroundKnowledgeGraph(propertyTreesMergingStrategy);

		paths.forEach(file -> {
			final Set<PropertyTree> trees = readFile(file, cleanedInputFilesDirectory, profilesDirectory);

			graph.addPropertyTrees(trees);
		});
		return graph;
	}
	
	private void test(final Collection<? extends Path> paths, final BackgroundKnowledgeGraph graph, final Path cleanedInputFilesDirectory, Path profilesDirectory) throws IOException {
		paths.forEach(input -> {
			final AnnotationResult result = annotateTable(input, graph, cleanedInputFilesDirectory, profilesDirectory);
			final Map<Integer, Annotation> columnIndicesToAnnotations = result.getAnnotations();
			final ParsedTable parsedTable = result.getParsedTable();
			
			System.out.println("File: " + input);
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

	private Set<PropertyTree> readFile(final Path input, final Path cleanedInputFilesDirectory, final Path profilesDirectory) {
		System.out.println("Processing file " + input + "...");
		
		final Path cleanedInput = clean(input, cleanedInputFilesDirectory);
		
		final CsvProfile csvProfile = profile(input, profilesDirectory, cleanedInput);
		
		final Format format = getFormat(csvProfile);
		
		/* Parse the input file to table. */
		final ParsedTable table = parse(input, cleanedInput, format);
		if (table.getHeight() < 2) {
			System.out.println("Too few rows in " + input + ". Skipping.");
			return ImmutableSet.of();
		}

		/* Assign data types to each table cell. */
		final Map<Integer, Type> hints = getHints(csvProfile);
		
		final TypedTable typedTable = tableAnalyzer.infer(table, Locale.GERMAN, hints);
		if (typedTable.getHeight() < 2) {
			System.out.println("Too few typed rows in " + input + ". Skipping.");
			return ImmutableSet.of();
		}

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, typedTable, hints);

		final Context context = getContext(slicedTable);
		
		return buildTrees(slicedTable, context);
	}


	private Set<PropertyTree> buildTrees(final SlicedTable slicedTable, final Context context) {
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


	private Context getContext(final SlicedTable slicedTable) {
		final Context context = new Context(slicedTable.getHeaders(), slicedTable.getMetadata().getAuthor(), slicedTable.getMetadata().getTitle());
		return context;
	}


	private ParsedTable parse(final Path input, final Path cleanedInput, final Format format) {
		final ParsedTable table;
		try (final InputStream inputStream = new BufferedInputStream(Files.newInputStream(cleanedInput))) {
			table = csvTableParser.parse(inputStream, format, new Metadata(input.getFileName().toString(), null, null));
		} catch (final IOException e) {
			throw new RuntimeException("Failed to parse " + input + "!", e);
		}
		return table;
	}


	private Map<Integer, Type> getHints(final CsvProfile csvProfile) {
		final Map<Integer, Type> hints;
		if (csvProfile == null) {
			hints = ImmutableMap.of();
		} else {
			final List<Type> types = csvProfile.getTypes();
			hints = toHints(types);
		}
		return hints;
	}


	private Format getFormat(final CsvProfile csvProfile) {
		final Format format;
		if (csvProfile == null) {
			format = null;
		} else {
			format = new Format(Charset.forName(csvProfile.getEncoding()), csvProfile.getDelimiter() == null ? null : csvProfile.getDelimiter().charAt(0), true, csvProfile.getQuotechar() == null ? null : csvProfile.getQuotechar().charAt(0), null, null);
		}
		return format;
	}


	private CsvProfile profile(final Path input, final Path profilesDirectory, final Path cleanedInput) {
		CsvProfile csvProfile = null;
		final Path profileInput = profilesDirectory.resolve(input.getFileName() + ".json");
		final Path failedProfileNotice = profilesDirectory.resolve(input.getFileName() + ".fail");
		if (profileInput.toFile().exists()) {
			try {
				csvProfile = loadProfile(profileInput);
			} catch (IOException e) {
				throw new RuntimeException("Failed to load profile for " + input + "!", e);
			}
		} else if (failedProfileNotice.toFile().exists()) {
			System.out.println("Previously failed profiling attempt for " + input + "!");
			
			csvProfile = null;
		} else {
			try {
				csvProfile = csvProfilerService.profile(cleanedInput.toFile());
				
				try {
					saveProfile(csvProfile, profileInput);
				} catch (final IOException e) {
					throw new RuntimeException("Failed to save profile for " + input + "!", e);
				}
			} catch (final IllegalStateException e) {
				System.out.println("Failed profiling attempt for " + input + "!");
				
				csvProfile = null;
				try {
					cacheFailedProfiling(profilesDirectory, input);
				} catch (IOException e1) {
					throw new RuntimeException("Failed to note failed profiling for " + input + "!", e1);
				}
			} catch (final IOException e) {
				throw new RuntimeException("Failed to profile " + input + "!", e);
			}
		}
		return csvProfile;
	}


	private Path clean(final Path input, final Path cleanedInputFilesDirectory) {
		Path cleanedInput = cleanedInputFilesDirectory.resolve(input.getFileName());
		final Path failedCleanNotice = cleanedInputFilesDirectory.resolve(input.getFileName() + ".fail");
		if (cleanedInput.toFile().exists()) {
			System.out.println("File " + input + " already cleaned.");
		} else if (failedCleanNotice.toFile().exists()) {
			System.out.println("Previously failed cleaning attempt for " + input + ". Using original instead.");
			
			cleanedInput = input;
		} else {
			try (final InputStream cleanedInputStream = csvCleanService.clean(input.toFile())) {
				Files.copy(cleanedInputStream, cleanedInput);
			} catch (final IllegalStateException e) {
				System.out.println("Failed clean attempt for " + input + "!");
				
				cleanedInput = input;
				try {
					cacheFailedCleaning(cleanedInputFilesDirectory, input);
				} catch (final IOException e1) {
					throw new RuntimeException("Failed to note failed cleaning for " + input + "!", e);
				}
			} catch (final IOException e) {
				throw new RuntimeException("Failed to clean " + input + "!", e);
			}
		}
		return cleanedInput;
	}

	private void cacheFailedCleaning(Path cleanedInputFilesDirectory, Path input) throws IOException {
		Files.createFile(cleanedInputFilesDirectory.resolve(input.getFileName() + ".fail"));
	}


	private void cacheFailedProfiling(Path profilesDirectory, Path input) throws IOException {
		Files.createFile(profilesDirectory.resolve(input.getFileName() + ".fail"));
	}


	private Map<Integer, Type> toHints(final List<? extends Type> types) {
		return IntStream.range(0, types.size()).mapToObj(i -> Integer.valueOf(i)).collect(ImmutableMap.toImmutableMap(i -> i, i -> types.get(i)));
	}

	private void saveProfile(final CsvProfile csvProfile, final Path profileInput)
			throws IOException, JsonGenerationException, JsonMappingException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(profileInput.toFile(), csvProfile);
	}


	private CsvProfile loadProfile(final Path profileInput)
			throws IOException, JsonParseException, JsonMappingException {
		final CsvProfile csvProfile;
		final ObjectMapper mapper = new ObjectMapper();
		csvProfile = mapper.readValue(profileInput.toFile(), CsvProfile.class);
		return csvProfile;
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
			final SharedPairNode subtree = new SharedPairNode(new AttributeValuePair(subattribute, subvalue), ImmutableMultiset.copyOf(subpartition.getValues()));
			subtree.addChildren(subchildren);
			
			children.add(subtree);
		}

		return children.build();
	}
	
	private AnnotationResult annotateTable(final Path input, final BackgroundKnowledgeGraph graph, final Path cleanedInputFilesDirectory, final Path profilesDirectory) {
		final Path cleanedInput = clean(input, cleanedInputFilesDirectory);
		
		final CsvProfile csvProfile = profile(input, profilesDirectory, cleanedInput);
		
		final Format format = getFormat(csvProfile);
		
		/* Parse the input file to table. */
		final ParsedTable parsedTable = parse(input, cleanedInput, format);
		if (parsedTable.getHeight() < 2) {
			throw new IllegalArgumentException("The table to annotate must have at least two rows!");
		}
		
		final Map<Integer, Type> hints = getHints(csvProfile);
		
		final TypedTable typedTable = tableAnalyzer.infer(parsedTable, Locale.GERMAN, hints);
		if (typedTable.getHeight() < 2) {
			throw new IllegalArgumentException("The table to annotate must have at least two rows!");
		}

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, typedTable, hints);

		final Context context = getContext(slicedTable);
		
		return new AnnotationResult(parsedTable, annotate(graph, slicedTable, context));
	}


	private Map<Integer, Annotation> annotate(final BackgroundKnowledgeGraph graph, final SlicedTable slicedTable,
			final Context context) {
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
