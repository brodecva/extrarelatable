/**
 * 
 */
package eu.odalic.extrarelatable.experiments;

import static java.util.function.Function.identity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import eu.odalic.extrarelatable.algorithms.graph.ResultAggregator;
import eu.odalic.extrarelatable.algorithms.graph.TopKNodesMatcher;
import eu.odalic.extrarelatable.algorithms.subcontext.SubcontextCompiler;
import eu.odalic.extrarelatable.algorithms.subcontext.SubcontextMatcher;
import eu.odalic.extrarelatable.algorithms.table.TableAnalyzer;
import eu.odalic.extrarelatable.algorithms.table.TableSlicer;
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
import eu.odalic.extrarelatable.model.graph.Property;
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
import eu.odalic.extrarelatable.services.csvengine.csvprofiler.CsvProfile;
import eu.odalic.extrarelatable.services.csvengine.csvprofiler.CsvProfilerService;
import eu.odalic.extrarelatable.services.dwtc.DwtcToCsvService;
import eu.odalic.extrarelatable.util.Matrix;
import webreduce.data.Dataset;
import webreduce.data.HeaderPosition;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.NestedListsParsedTable;
import eu.odalic.extrarelatable.model.table.ParsedTable;

/**
 * @author Václav Brodec
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml" })
public class T2Dv2GoldStandard {

	private static final String FILES_WITHOUT_PROPERTY_PREFIX = "CC-MAIN";
	private static final double RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD = 0.6;
	private static final String RESOURCES_PATH = "C:/Users/brodecva/Documents/Vysoká škola/Diplomová práce/Datasets";
	private static final String INSTANCE_SUBPATH = "extended_instance_goldstandard";
	private static final String SET_SUBPATH = "tables";
	private static final String DECLARED_PROPERTIES_SUBPATH = "property";
	private static final double MINIMUM_PARTITION_RELATIVE_SIZE = 0.01;
	private static final double MAXIMUM_PARTITION_RELATIVE_SIZE = 0.99;
	private static final int TOP_K_NEGHBOURS = 25;
	private static final int TOP_K_AGGREGATED_RESULTS = 5;
	private static final int MINIMUM_PARTITION_SIZE = 2;
	private static final int SAMPLE_SIZE = 10;
	private static final long SEED = 1;
	private static final String PROFILES_DIRECTORY = "profiles";
	private static final String CONVERTED_INPUT_FILES_DIRECTORY = "csvs";
	private static final String CHARACTERS_TO_SANITIZE_REGEX = "[.]";
	private static final String SANITIZE_WITH = "_";

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
	private ResultAggregator propertiesResultAggregator;

	@Autowired
	@Lazy
	@Qualifier("majorityVote")
	private ResultAggregator labelsResultAggregator;

	@Autowired
	@Lazy
	@Qualifier("propertyUri")
	private PropertyTreesMergingStrategy propertyTreesMergingStrategy;

	@Autowired
	@Lazy
	private CsvProfilerService csvProfilerService;

	@Autowired
	@Lazy
	private DwtcToCsvService dwtcToCsvService;

	@Test
	public void test() throws IOException {
		final Random random = new Random(SEED);

		final Path resourcesPath = Paths.get(RESOURCES_PATH);
		final Path instancePath = resourcesPath.resolve(INSTANCE_SUBPATH);

		final Path setPath = instancePath.resolve(SET_SUBPATH);
		final Path declaredPropertiesPath = instancePath.resolve(DECLARED_PROPERTIES_SUBPATH);

		final List<Path> files = getFiles(setPath);

		final List<Path> testPaths = getTestFiles(random, files);
		final List<Path> learningPaths = getLearningFiles(files, testPaths);

		final Path inputFilesPath = setPath.resolve(CONVERTED_INPUT_FILES_DIRECTORY);
		final Path profilesPath = setPath.resolve(PROFILES_DIRECTORY);

		final BackgroundKnowledgeGraph graph = learn(learningPaths, inputFilesPath, profilesPath,
				declaredPropertiesPath);

		test(testPaths, graph, inputFilesPath, profilesPath, declaredPropertiesPath);
	}

	private ImmutableList<Path> getLearningFiles(final List<Path> files, final List<Path> testPaths) {
		return files.stream().filter(e -> !testPaths.contains(e)).collect(ImmutableList.toImmutableList());
	}

	private static List<Path> getTestFiles(final Random random, final List<Path> files) {
		final ImmutableList.Builder<Path> testPathsBuilder = ImmutableList.builder();
		for (int i = 0; i < SAMPLE_SIZE; i++) {
			final int removedIndex = random.nextInt(files.size());

			testPathsBuilder.add(files.remove(removedIndex));
		}
		final List<Path> testPaths = testPathsBuilder.build();
		return testPaths;
	}

	private static List<Path> getFiles(final Path setPath) throws IOException {
		final List<Path> paths = Streams.stream(Files.newDirectoryStream(setPath))
				.filter(path -> path.toFile().isFile()
						&& !path.getFileName().toString().startsWith(FILES_WITHOUT_PROPERTY_PREFIX))
				.collect(Collectors.toCollection(ArrayList::new));

		Collections.sort(paths);
		return paths;
	}

	private BackgroundKnowledgeGraph learn(final Collection<? extends Path> paths,
			final Path cleanedInputFilesDirectory, final Path profilesDirectory, final Path declaredPropertiesPath)
			throws IOException {
		final BackgroundKnowledgeGraph graph = new BackgroundKnowledgeGraph(propertyTreesMergingStrategy);

		paths.forEach(file -> {
			final Set<PropertyTree> trees = learnFile(file, cleanedInputFilesDirectory, profilesDirectory,
					declaredPropertiesPath);

			graph.addPropertyTrees(trees);
		});

		return graph;
	}

	private void test(final Collection<? extends Path> paths, final BackgroundKnowledgeGraph graph,
			final Path convertedInputFilesDirectory, final Path profilesDirectory, final Path declaredPropertiesPath)
			throws IOException {
		paths.forEach(file -> {
			System.out.println("File: " + file);

			final AnnotationResult result;
			try {
				result = annotateTable(file, graph, convertedInputFilesDirectory, profilesDirectory,
						declaredPropertiesPath);
			} catch (final IllegalArgumentException e) {
				System.out.println("Error: " + e.getMessage());

				return;
			}
			
			final Map<Integer, URI> solution = getSolution(file, declaredPropertiesPath);
			
			final Map<Integer, Annotation> columnIndicesToAnnotations = result.getAnnotations();
			final ParsedTable parsedTable = result.getParsedTable();

			System.out.println("Headers: ");
			System.out.println(parsedTable.getHeaders().stream().collect(Collectors.joining(";")));
			System.out.println("First rows: ");
			System.out.println(parsedTable.getRows().subList(0, Math.min(parsedTable.getHeight(), 5)).stream()
					.map(row -> Joiner.on(";").join(row)).collect(Collectors.joining("\n")));
			columnIndicesToAnnotations.entrySet().forEach(e -> {
				final int index = e.getKey();
				final Annotation annotation = e.getValue();

				final Map<Label, Statistics> labelsStatistics = annotation.getLabelsStatistics();
				final Map<Property, Statistics> propertiesStatistics = annotation.getPropertiesStatistics();

				System.out.println("--------------------------------------------------------------------------------");
				System.out.println("Index:" + index);
				System.out.println("Header:" + parsedTable.getHeaders().get(index));
				System.out.println("First values:");
				System.out.println(parsedTable.getColumn(index).subList(0, Math.min(parsedTable.getHeight(), 5))
						.stream().collect(Collectors.joining("\t")));
				System.out.println("Labels:");
				System.out.println(
						"Mean distance\tMedian distance\tOccurence\tRelative occurence\tText\tIndex\tFile\tFirst values\tFirst rows");
				System.out.println(annotation.getLabels().stream().map(label -> {
					final Statistics statistics = labelsStatistics.get(label);

					return Joiner.on("\t").join(statistics.getAverage(), statistics.getMedian(),
							statistics.getOccurence(), statistics.getRelativeOccurence(), label.getText(),
							label.getIndex(), label.getFile(), label.getFirstValues(), label.getFirstRows());
				}).collect(Collectors.joining("\n")));

				System.out.println("Properties:");
				System.out.println("Mean distance\tMedian distance\tOccurence\tRelative occurence\tURI");
				System.out.println(annotation.getProperties().stream().map(property -> {
					final Statistics statistics = propertiesStatistics.get(property);

					return Joiner.on("\t").useForNull("null").join(statistics.getAverage(), statistics.getMedian(),
							statistics.getOccurence(), statistics.getRelativeOccurence(), property.getUri());
				}).collect(Collectors.joining("\n")));
				
				System.out.println("Solution:");
				System.out.println(solution.get(index));
			});
			System.out.println("================================================================================");
		});
	}

	private static Map<Integer, URI> getSolution(final Path input, final Path declaredPropertiesPath) {
		return getDeclaredPropertyUris(declaredPropertiesPath, input.getFileName().toString());
	}

	private Set<PropertyTree> learnFile(final Path input, final Path convertedInputFilesDirectory,
			final Path profilesDirectory, final Path declaredPropertiesPath) {
		System.out.println("Processing file " + input + "...");

		final Path convertedInput = convert(input, convertedInputFilesDirectory);

		final CsvProfile csvProfile = profile(input, profilesDirectory, convertedInput);

		/* Parse the input file to table. */
		final Dataset dataset = parse(input);
		final HeaderPosition headerPosition = dataset.getHeaderPosition();
		if (headerPosition != HeaderPosition.FIRST_ROW) {
			System.out.println("File " + input + " has no regular header!");
			return ImmutableSet.of();
		}

		final ParsedTable table = toParsedTable(dataset, input.getFileName().toString());
		if (table.getHeight() < 2) {
			System.out.println("Too few rows in " + input + ". Skipping.");
			return ImmutableSet.of();
		}

		/* Assign data types to each table cell. */
		final Map<Integer, Type> hints = getHints(csvProfile);

		final TypedTable typedTable = tableAnalyzer.infer(table, Locale.US, hints);
		if (typedTable.getHeight() < 2) {
			System.out.println("Too few typed rows in " + input + ". Skipping.");
			return ImmutableSet.of();
		}

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, typedTable,
				hints);

		final Map<Integer, URI> declaredPropertyUris = getDeclaredPropertyUris(declaredPropertiesPath,
				input.getFileName().toString());

		return buildTrees(slicedTable, declaredPropertyUris);
	}

	private static Map<Integer, URI> getDeclaredPropertyUris(final Path declaredPropertiesPath,
			final String tableFileName) {
		final String fileName = com.google.common.io.Files.getNameWithoutExtension(tableFileName);

		final String sanitizedFileName = fileName.replaceAll(CHARACTERS_TO_SANITIZE_REGEX, SANITIZE_WITH);

		final String propertiesFileName = sanitizedFileName + ".csv";

		final Path propertiesPath = declaredPropertiesPath.resolve(propertiesFileName);

		if (!propertiesPath.toFile().exists()) {
			return ImmutableMap.of();
		}

		final CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setLineSeparatorDetectionEnabled(true);
		
		final CsvFormat csvFormat = new CsvFormat();
		csvFormat.setDelimiter(',');
		csvFormat.setQuote('"');
		parserSettings.setFormat(csvFormat);
		parserSettings.setDelimiterDetectionEnabled(false);
		parserSettings.setQuoteDetectionEnabled(false);

		final RowListProcessor rowProcessor = new RowListProcessor();
		parserSettings.setProcessor(rowProcessor);

		final CsvParser parser = new CsvParser(parserSettings);
		parser.parse(propertiesPath.toFile(), StandardCharsets.UTF_8);

		final List<String[]> rows = rowProcessor.getRows();
		
		try {
		return rows.stream().collect(ImmutableMap.toImmutableMap(fields -> Integer.parseInt(fields[fields.length - 1]),
						fields -> URI.create(fields[0])));
		} catch (final ArrayIndexOutOfBoundsException e) {
			return ImmutableMap.of();
		}
	}

	private Set<PropertyTree> buildTrees(final SlicedTable slicedTable,
			final Map<? extends Integer, ? extends URI> declaredPropertyUris) {
		/*
		 * For each numeric column and its set of numeric values compute the possible
		 * sub-contexts and order them by distance in descending order from the set.
		 * 
		 * Use the farthest sub-context to partition the set of values into nodes and
		 * recursively compute the sub-context for them.
		 */
		final ImmutableSet.Builder<PropertyTree> propertyTreesBuilder = ImmutableSet.builder();

		final Set<Integer> availableContextColumnIndices = slicedTable.getContextColumns().keySet();

		for (final Entry<Integer, List<Value>> numericColumn : slicedTable.getDataColumns().entrySet()) {
			final int columnIndex = numericColumn.getKey();
			final Label label = slicedTable.getHeaders().get(columnIndex);

			final Partition partition = new Partition(numericColumn.getValue().stream().filter(e -> e.isNumeric())
					.map(e -> (NumericValue) e).collect(ImmutableList.toImmutableList()));
			if (partition.size() < MINIMUM_PARTITION_SIZE) {
				continue;
			}

			final Set<CommonNode> children = buildChildren(partition, availableContextColumnIndices, slicedTable,
					MINIMUM_PARTITION_RELATIVE_SIZE, MAXIMUM_PARTITION_RELATIVE_SIZE, MINIMUM_PARTITION_SIZE);

			final RootNode rootNode = new RootNode(label, ImmutableMultiset.copyOf(partition.getValues()));
			rootNode.addChildren(children);

			final URI declaredPropertyUri = declaredPropertyUris.get(columnIndex);

			final Context context = new Context(slicedTable.getHeaders(), slicedTable.getMetadata().getAuthor(),
					slicedTable.getMetadata().getTitle(), declaredPropertyUri);

			final PropertyTree tree = new PropertyTree(rootNode, context);
			rootNode.setPropertyTree(tree);
			propertyTreesBuilder.add(tree);
		}

		return propertyTreesBuilder.build();
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

	private static ParsedTable toParsedTable(final Dataset dataset, final String fileName) {
		final ParsedTable table = NestedListsParsedTable.fromColumns(Matrix.fromArray(dataset.getRelation()),
				new Metadata(fileName, dataset.getUrl()));

		return table;
	}

	private Dataset parse(final Path input) {
		final Dataset dataset;
		try (final InputStream datasetInputStream = Files.newInputStream(input, StandardOpenOption.READ)) {
			dataset = webreduce.data.Dataset.fromJson(datasetInputStream);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to read " + input + "!", e);
		}
		return dataset;
	}

	private CsvProfile profile(final Path input, final Path profilesDirectory, final Path convertedInput) {
		CsvProfile csvProfile = null;
		final Path profileInput = profilesDirectory.resolve(input.getFileName());
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
				csvProfile = csvProfilerService.profile(convertedInput.toFile());

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

	private Path convert(final Path input, final Path convertedInputFilesDirectory) {
		Path convertedInput = convertedInputFilesDirectory.resolve(input.getFileName() + ".csv");
		if (convertedInput.toFile().exists()) {
			System.out.println("File " + input + " already converted.");
		} else {
			try {
				dwtcToCsvService.convert(input, convertedInput);
			} catch (final IOException e) {
				throw new RuntimeException("Failed to convert " + input + "!", e);
			}
		}
		return convertedInput;
	}

	private void cacheFailedProfiling(Path profilesDirectory, Path input) throws IOException {
		Files.createFile(profilesDirectory.resolve(input.getFileName() + ".fail"));
	}

	private Map<Integer, Type> toHints(final List<? extends Type> types) {
		return IntStream.range(0, types.size()).mapToObj(i -> Integer.valueOf(i))
				.collect(ImmutableMap.toImmutableMap(i -> i, i -> types.get(i)));
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

	private Set<CommonNode> buildChildren(final Partition partition, final Set<Integer> availableContextColumnIndices,
			final TypedTable table, final double minimumPartitionRelativeSize,
			final double maximumPartitionRelativeSize, final int minimumPartitionSize) {
		final Set<Subcontext> subcontexts = subcontextCompiler.compile(partition, availableContextColumnIndices, table,
				minimumPartitionRelativeSize, maximumPartitionRelativeSize, minimumPartitionSize);
		if (subcontexts.isEmpty()) {
			return ImmutableSet.of();
		}

		final ImmutableSet.Builder<CommonNode> children = ImmutableSet.builder();

		final Subcontext winningSubcontext = subcontextMatcher.match(subcontexts, partition,
				minimumPartitionRelativeSize, maximumPartitionRelativeSize, minimumPartitionSize);
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
					Sets.difference(availableContextColumnIndices, ImmutableSet.of(usedContextColumnIndex)), table,
					minimumPartitionRelativeSize, maximumPartitionRelativeSize, minimumPartitionSize);

			final Value subvalue = partitionEntry.getKey();
			final SharedPairNode subtree = new SharedPairNode(new AttributeValuePair(subattribute, subvalue),
					ImmutableMultiset.copyOf(partition.getValues()));
			subtree.addChildren(subchildren);

			children.add(subtree);
		}

		return children.build();
	}

	private AnnotationResult annotateTable(final Path input, final BackgroundKnowledgeGraph graph,
			final Path convertedInputFilesDirectory, final Path profilesDirectory, final Path declaredPropertiesPath) {
		final Path convertedInput = convert(input, convertedInputFilesDirectory);

		final CsvProfile csvProfile = profile(input, profilesDirectory, convertedInput);

		/* Parse the input file to table. */
		final Dataset dataset = parse(input);
		final HeaderPosition headerPosition = dataset.getHeaderPosition();
		if (headerPosition != HeaderPosition.FIRST_ROW) {
			throw new IllegalArgumentException("File " + input + " has no regular header!");
		}

		final ParsedTable parsedTable = toParsedTable(dataset, input.getFileName().toString());
		if (parsedTable.getHeight() < 2) {
			throw new IllegalArgumentException("Too few rows in " + input + ". Skipping.");
		}

		/* Assign data types to each table cell. */
		final Map<Integer, Type> hints = getHints(csvProfile);

		final TypedTable typedTable = tableAnalyzer.infer(parsedTable, Locale.US, hints);
		if (typedTable.getHeight() < 2) {
			throw new IllegalArgumentException("The table to annotate must have at least two rows!");
		}

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, typedTable,
				hints);

		final Map<Integer, URI> declaredPropertyUris = getDeclaredPropertyUris(declaredPropertiesPath,
				input.getFileName().toString());

		return new AnnotationResult(parsedTable, annotate(graph, slicedTable, declaredPropertyUris));
	}

	private Map<Integer, Annotation> annotate(final BackgroundKnowledgeGraph graph, final SlicedTable slicedTable,
			final Map<? extends Integer, ? extends URI> declaredPropertyUris) {
		final ImmutableMap.Builder<Integer, Annotation> builder = ImmutableMap.builder();

		final Set<Integer> availableContextColumnIndices = slicedTable.getContextColumns().keySet();

		for (final Entry<Integer, List<Value>> numericColumn : slicedTable.getDataColumns().entrySet()) {
			final int columnIndex = numericColumn.getKey();
			final Label label = slicedTable.getHeaders().get(columnIndex);

			final Partition partition = new Partition(numericColumn.getValue().stream().filter(e -> e.isNumeric())
					.map(e -> (NumericValue) e).collect(ImmutableList.toImmutableList()));
			if (partition.size() < MINIMUM_PARTITION_SIZE) {
				continue;
			}

			final Set<CommonNode> children = buildChildren(partition, availableContextColumnIndices, slicedTable,
					MINIMUM_PARTITION_RELATIVE_SIZE, MAXIMUM_PARTITION_RELATIVE_SIZE, MINIMUM_PARTITION_SIZE);

			final RootNode rootNode = new RootNode(label, ImmutableMultiset.copyOf(partition.getValues()));
			rootNode.addChildren(children);

			final URI declaredPropertyUri = declaredPropertyUris.get(columnIndex);

			final Context context = new Context(slicedTable.getHeaders(), slicedTable.getMetadata().getAuthor(),
					slicedTable.getMetadata().getTitle(), declaredPropertyUri);

			final PropertyTree tree = new PropertyTree(rootNode, context);
			rootNode.setPropertyTree(tree);

			final ImmutableMultiset.Builder<MeasuredNode> treeMatchingNodesBuilder = ImmutableMultiset.builder();

			for (final Node node : tree) {
				final SortedSet<MeasuredNode> matchingNodes = topKNodesMatcher.match(graph, node.getValues(),
						TOP_K_NEGHBOURS);
				treeMatchingNodesBuilder.addAll(matchingNodes);
			}

			final Multiset<MeasuredNode> treeMatchingNodes = treeMatchingNodesBuilder.build();

			final SetMultimap<Property, MeasuredNode> propertyLevelAggregates = treeMatchingNodes.stream()
					.collect(ImmutableSetMultimap.toImmutableSetMultimap(e -> e.getNode().getProperty(), identity()));
			final SortedSet<Property> propertyAggregates = propertiesResultAggregator
					.aggregate(propertyLevelAggregates);
			final List<Property> properties = cutOff(propertyAggregates);

			final SetMultimap<Label, MeasuredNode> labelLevelAggregates = treeMatchingNodes.stream()
					.collect(ImmutableSetMultimap.toImmutableSetMultimap(e -> e.getNode().getLabel(), identity()));
			final SortedSet<Label> labelAggregates = labelsResultAggregator.aggregate(labelLevelAggregates);
			final List<Label> labels = cutOff(labelAggregates);

			final Map<Property, Statistics> propertiesStatistics = getStatistics(properties, propertyLevelAggregates);
			final Map<Label, Statistics> labelStatistics = getStatistics(labels, labelLevelAggregates);

			builder.put(columnIndex, Annotation.of(properties, labels, ImmutableList.of(), propertiesStatistics,
					labelStatistics, ImmutableMap.of()));
		}

		return builder.build();
	}

	private static <T> Map<T, Statistics> getStatistics(final Iterable<T> aspects,
			final SetMultimap<T, ? extends MeasuredNode> aspectLevelAggregates) {
		final ImmutableMap.Builder<T, Statistics> builder = ImmutableMap.builder();
		for (final T aspect : aspects) {
			final Set<? extends MeasuredNode> nodes = aspectLevelAggregates.get(aspect);

			final double[] distances = nodes.stream().mapToDouble(e -> e.getDistance()).toArray();

			final double average = new Mean().evaluate(distances);
			final double median = new Median().evaluate(distances);
			final int occurence = nodes.size();
			final double relativeOccurence = occurence / (double) (aspectLevelAggregates.size());

			final Statistics statistics = Statistics.of(average, median, occurence, relativeOccurence);
			builder.put(aspect, statistics);
		}

		return builder.build();
	}

	private <T> List<T> cutOff(final Collection<T> labelAggregates) {
		return ImmutableList.copyOf(labelAggregates).subList(0,
				Math.min(labelAggregates.size(), TOP_K_AGGREGATED_RESULTS));
	}
}