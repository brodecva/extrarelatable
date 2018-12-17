/**
 * 
 */
package eu.odalic.extrarelatable.experiments;

import static java.util.function.Function.identity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
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
import com.google.common.base.Splitter;
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
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import eu.odalic.extrarelatable.algorithms.graph.aggregation.ResultAggregator;
import eu.odalic.extrarelatable.algorithms.graph.matching.TopKNodesMatcher;
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
import eu.odalic.extrarelatable.model.bag.NumberLikeValue;
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
import eu.odalic.extrarelatable.model.table.csv.Format;
import eu.odalic.extrarelatable.services.csvengine.csvclean.CsvCleanService;
import eu.odalic.extrarelatable.services.csvengine.csvprofiler.CsvProfile;
import eu.odalic.extrarelatable.services.csvengine.csvprofiler.CsvProfilerService;
import eu.odalic.extrarelatable.services.odalic.ContextCollectionService;
import eu.odalic.extrarelatable.services.odalic.values.ColumnRelationAnnotationValue;
import eu.odalic.extrarelatable.services.odalic.values.EntityCandidateValue;
import eu.odalic.extrarelatable.services.odalic.values.EntityValue;
import eu.odalic.extrarelatable.services.odalic.values.HeaderAnnotationValue;
import eu.odalic.extrarelatable.services.odalic.values.ResultValue;
import eu.odalic.extrarelatable.util.UuidGenerator;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.ParsedTable;

/**
 * @author Václav Brodec
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/testApplicationContext.xml" })
public class Iswc2016 {

	private static final double RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD = Double.parseDouble(
			System.getProperty("eu.odalic.extrarelatable.relativeColumnTypeValuesOccurenceThreshold", "0.6"));
	private static final String INSTANCE_SUBPATH = System.getProperty("eu.odalic.extrarelatable.instancePath");
	private static final String TEST_INSTANCE_SUBPATH = System.getProperty("eu.odalic.extrarelatable.testInstancePath");
	private static final String DECLARED_PROPERTIES_SUBPATH = "property";
	private static final double MINIMUM_PARTITION_RELATIVE_SIZE = Double
			.parseDouble(System.getProperty("eu.odalic.extrarelatable.minimumPartitionRelativeSize", "0.01"));
	private static final double MAXIMUM_PARTITION_RELATIVE_SIZE = Double
			.parseDouble(System.getProperty("eu.odalic.extrarelatable.maximumPartitionRelativeSize", "0.99"));
	private static final int TOP_K_NEGHBOURS = Integer
			.parseInt(System.getProperty("eu.odalic.extrarelatable.topKNeighbours", "25"));;
	private static final int TOP_K_AGGREGATED_RESULTS = Integer
			.parseInt(System.getProperty("eu.odalic.extrarelatable.topKAggregatedResults", "5"));
	private static final int MINIMUM_PARTITION_SIZE = 2;
	private static final long SEED = Long
			.parseLong(System.getProperty("eu.odalic.extrarelatable.seed", String.valueOf(System.currentTimeMillis())));
	private static final String PROFILES_DIRECTORY = "profiles";
	private static final String CLEANED_INPUT_FILES_DIRECTORY = "cleaned";
	private static final String CONTEXT_COLLECTION_RESULTS_SUBPATH = "context";
	private static final boolean FILES_ONLY_WITH_PROPERTIES = Boolean
			.parseBoolean(System.getProperty("eu.odalic.extrarelatable.filesOnlyWithProperties", "true"));
	private static final boolean NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES = Boolean
			.parseBoolean(System.getProperty("eu.odalic.extrarelatable.onlyWithProperties", "true"));
	private static final boolean ONLY_DECLARED_AS_CONTEXT = Boolean
			.parseBoolean(System.getProperty("eu.odalic.extrarelatable.onlyDeclaredAsContext", "false"));
	private static final boolean DRY_RUN = Boolean
			.parseBoolean(System.getProperty("eu.odalic.extrarelatable.dryRun", "false"));
	private static final double VALUES_WEIGHT = Double
			.parseDouble(System.getProperty("eu.odalic.extrarelatable.valuesWeight", "1"));
	private static final double PROPERTIES_WEIGHT = Double
			.parseDouble(System.getProperty("eu.odalic.extrarelatable.propertiesWeight", "0"));
	private static final double CLASSES_WEIGHT = Double
			.parseDouble(System.getProperty("eu.odalic.extrarelatable.classesWeight", "0"));
	private static final Set<URI> STOP_ENTITIES = ImmutableSet
			.of(URI.create("http://www.w3.org/2000/01/rdf-schema#label"));
	private static final Set<String> USED_BASES = System.getProperty("eu.odalic.extrarelatable.odalic.usedBases",
			"GermanDBpediaLocal") == null ? null
					: ImmutableSet.copyOf(Splitter.on(",").split(
							System.getProperty("eu.odalic.extrarelatable.odalic.usedBases", "GermanDBpediaLocal")));
	private static final String PRIMARY_BASE = System.getProperty("eu.odalic.extrarelatable.odalic.primaryBase",
			"GermanDBpediaLocal");
	private static final int MAXIMUM_COLUMN_SAMPLE_SIZE = Integer
			.parseInt(System.getProperty("eu.odalic.extrarelatable.maximumColumnSampleSize", "1000"));
	private static final boolean AVOID_HINTS = Boolean
			.parseBoolean(System.getProperty("eu.odalic.extrarelatable.avoidHints", "false"));

	@Autowired
	BeanFactory beanFactory;

	@Autowired
	@Lazy
	@Qualifier("CsvTableParser")
	private CsvTableParser csvTableParser;

	@Autowired
	@Lazy
	@Qualifier("UuidGenerator")
	private UuidGenerator uuidGenerator;
	
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
	@Qualifier("PropertiesResultAggregator")
	private ResultAggregator<MeasuredNode> propertiesResultAggregator;

	@Autowired
	@Lazy
	@Qualifier("PropertyTreesMergingStrategy")
	private PropertyTreesMergingStrategy propertyTreesMergingStrategy;

	@Autowired
	@Lazy
	private CsvCleanService csvCleanService;

	@Autowired
	@Lazy
	private CsvProfilerService csvProfilerService;

	@Autowired
	@Lazy
	private ContextCollectionService contextCollectionService;

	@Before
	public void setUp() {
	}

	@Test
	public void test() throws IOException {
		final CsvWriterSettings csvWriterSettings = new com.univocity.parsers.csv.CsvWriterSettings();
		final CsvWriter csvWriter = new CsvWriter(System.out, StandardCharsets.UTF_8, csvWriterSettings);

		testSample(csvWriter);

		csvWriter.flush();
		csvWriter.close();
	}
	
	private void testSample(final CsvWriter csvWriter) throws IOException {
		final Random random = new Random(SEED);
		
		testSample(csvWriter, random);
	}

	private boolean testSample(final CsvWriter csvWriter, final Random random) throws IOException {
		if (INSTANCE_SUBPATH == null) {
			throw new IllegalArgumentException("No instance path provided!");
		}

		final Path instancePath = Paths.get(INSTANCE_SUBPATH);

		final Path setPath = instancePath;
		final Path declaredPropertiesPath = instancePath.resolve(DECLARED_PROPERTIES_SUBPATH);
		final Path collectionResultsDirectory = instancePath.resolve(CONTEXT_COLLECTION_RESULTS_SUBPATH);

		final List<Path> learningPaths = getLearningFiles(setPath, declaredPropertiesPath, FILES_ONLY_WITH_PROPERTIES);

		final Path testInstancePath = Paths.get(TEST_INSTANCE_SUBPATH);

		final Path testSetPath = testInstancePath;
		final Path testCollectionResultsDirectory = testInstancePath.resolve(CONTEXT_COLLECTION_RESULTS_SUBPATH);
		
		final List<Path> testPaths;
		if (DRY_RUN) {
			testPaths = ImmutableList.of();
		} else {
			testPaths = getTestFiles(testSetPath);
		}

		final Path cleanedInputFilesPath = setPath.resolve(CLEANED_INPUT_FILES_DIRECTORY);
		final Path profilesPath = setPath.resolve(PROFILES_DIRECTORY);

		final BackgroundKnowledgeGraph graph = learn(csvWriter, learningPaths, cleanedInputFilesPath, profilesPath,
				declaredPropertiesPath, collectionResultsDirectory, NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES,
				ONLY_DECLARED_AS_CONTEXT, random, MAXIMUM_COLUMN_SAMPLE_SIZE);

		csvWriter.writeEmptyRow();
		csvWriter.writeEmptyRow();
		csvWriter.writeEmptyRow();
		csvWriter.writeEmptyRow();

		final Path testCleanedInputFilesPath = testSetPath.resolve(CLEANED_INPUT_FILES_DIRECTORY);
		final Path testProfilesPath = testSetPath.resolve(PROFILES_DIRECTORY);
		
		test(csvWriter, testPaths, graph, testCleanedInputFilesPath, testProfilesPath, 
				testCollectionResultsDirectory,
				random, MAXIMUM_COLUMN_SAMPLE_SIZE);

		csvWriter.writeEmptyRow();
		csvWriter.writeRow("Finished.");

		csvWriter.writeEmptyRow();

		return true;
	}

	private static List<Path> getLearningFiles(final Path setPath, final Path declaredPropertiesPath,
			final boolean onlyWithProperties) throws IOException {
		final List<Path> paths = Streams.stream(Files.newDirectoryStream(setPath))
				.filter(path -> path.toFile().isFile() && ((!onlyWithProperties)
						|| getPropertiesPath(declaredPropertiesPath, path.getFileName().toString()) != null))
				.collect(Collectors.toCollection(ArrayList::new));

		Collections.sort(paths);
		return ImmutableList.copyOf(paths);
	}
	
	private static List<Path> getTestFiles(final Path setPath) throws IOException {
		final List<Path> paths = Streams.stream(Files.newDirectoryStream(setPath))
				.filter(path -> path.toFile().isFile())
				.collect(Collectors.toCollection(ArrayList::new));

		Collections.sort(paths);
		return ImmutableList.copyOf(paths);
	}

	private BackgroundKnowledgeGraph learn(final CsvWriter csvWriter, final Collection<? extends Path> paths,
			final Path cleanedInputFilesDirectory, final Path profilesDirectory, final Path declaredPropertiesPath,
			final Path collectionResultsDirectory, final boolean onlyWithProperties,
			final boolean onlyDeclaredAsContext, final Random random,
			final int maxColumnSampleSize) throws IOException {
		final BackgroundKnowledgeGraph graph = new BackgroundKnowledgeGraph(this.uuidGenerator.generate(), propertyTreesMergingStrategy);

		paths.forEach(file -> {
			final Set<PropertyTree> trees = learnFile(csvWriter, file, cleanedInputFilesDirectory, profilesDirectory,
					declaredPropertiesPath, collectionResultsDirectory, onlyWithProperties, onlyDeclaredAsContext,
					random, maxColumnSampleSize);

			graph.addPropertyTrees(trees);
		});

		return graph;
	}

	private void test(final CsvWriter csvWriter, final Collection<? extends Path> paths,
			final BackgroundKnowledgeGraph graph, final Path cleanedInputFilesDirectory, final Path profilesDirectory,
			final Path collectionResultsDirectory,
			final Random random, int maxColumnSampleSize)
			throws IOException {
		paths.forEach(file -> {
			csvWriter.writeRow("File:", file);

			final AnnotationResult result;
			try {
				result = annotateTable(csvWriter, file, graph, cleanedInputFilesDirectory, profilesDirectory,
						collectionResultsDirectory,
						random, maxColumnSampleSize);
			} catch (final IllegalArgumentException e) {
				csvWriter.writeRow("Error:", e.getMessage());

				return;
			}

			final Map<Integer, Annotation> columnIndicesToAnnotations = result.getAnnotations();
			final ParsedTable parsedTable = result.getParsedTable();

			csvWriter.writeEmptyRow();

			csvWriter.addValue("Headers:");
			csvWriter.addValues(parsedTable.getHeaders());
			csvWriter.writeValuesToRow();

			csvWriter.writeRow("First rows:");
			csvWriter.writeRows(parsedTable.getRows().subList(0, Math.min(parsedTable.getHeight(), 5)).stream()
					.map(row -> row.toArray()).collect(ImmutableList.toImmutableList()));

			csvWriter.writeRow("Numeric columns:");

			columnIndicesToAnnotations.entrySet().forEach(e -> {
				final int index = e.getKey();
				final Annotation annotation = e.getValue();

				final Map<Property, Statistics> propertiesStatistics = annotation.getPropertiesStatistics();

				csvWriter.writeRow("Index:", index);
				csvWriter.writeRow("Header:", parsedTable.getHeaders().get(index));

				csvWriter.addValue("First values:");
				csvWriter.addValues(parsedTable.getColumn(index).subList(0, Math.min(parsedTable.getHeight(), 5)));
				csvWriter.writeValuesToRow();

				csvWriter.writeRow("Properties:");
				csvWriter.writeRow("Mean distance", "Median distance", "Occurence", "Relative occurence", "URI",
						"Context properties");
				csvWriter.writeRows(annotation.getProperties().stream().map(property -> {
					final Statistics statistics = propertiesStatistics.get(property);

					return new Object[] { statistics.getAverage(), statistics.getMedian(), statistics.getOccurence(),
							statistics.getRelativeOccurence(), property.getUri(), getContextProperties(property) };
				}).collect(ImmutableList.toImmutableList()));
			});

			csvWriter.writeEmptyRow();
			csvWriter.writeEmptyRow();
			csvWriter.writeEmptyRow();
		});
	}

	private static Set<URI> getContextProperties(final Property property) {
		return property.getInstances().stream().map(i -> i.getContext().getDeclaredContextColumnProperties().values())
				.flatMap(e -> e.stream()).map(e -> e.getUri()).collect(ImmutableSet.toImmutableSet());
	}

	private Set<PropertyTree> learnFile(final CsvWriter csvWriter, final Path input,
			final Path cleanedInputFilesDirectory, final Path profilesDirectory, final Path declaredPropertiesPath,
			final Path collectionResultsDirectory, final boolean onlyWithProperties,
			final boolean onlyDeclaredAsContext, final Random random,
			final int maxColumnSampleSize) {
		csvWriter.writeRow("Processing file:", input);

		final Path cleanedInput = clean(csvWriter, input, cleanedInputFilesDirectory);

		final CsvProfile csvProfile = profile(csvWriter, input, profilesDirectory, cleanedInput);

		final Format format = getFormat(csvProfile);

		/* Parse the input file to table. */

		final ParsedTable parsedTable = parse(input, cleanedInput, format);
		if (parsedTable.getHeight() < 2) {
			csvWriter.writeRow("Too few rows in " + input + ". Skipping.");

			return ImmutableSet.of();
		}

		/* Assign data types to each table cell. */
		final Map<Integer, Type> hints = getHints(csvProfile);

		final TypedTable typedTable = tableAnalyzer.infer(parsedTable, Locale.forLanguageTag("de-at"), hints);
		if (typedTable.getHeight() < 2) {
			csvWriter.writeRow("Too few typed rows in " + input + ". Skipping.");

			return ImmutableSet.of();
		}

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, typedTable,
				hints);

		final Map<Integer, DeclaredEntity> declaredProperties = getDeclaredProperties(declaredPropertiesPath,
				input.getFileName().toString());

		final Map<Integer, DeclaredEntity> contextProperties;
		final Map<Integer, DeclaredEntity> contextClasses;
		if (!onlyDeclaredAsContext) {
			final ResultValue collectedContext = getCollectedContext(csvWriter, parsedTable, input,
					collectionResultsDirectory, random);
			if (collectedContext == null) {
				contextProperties = ImmutableMap.of();
				contextClasses = ImmutableMap.of();
			} else {
				contextProperties = getContextProperties(collectedContext);
				contextClasses = getContextClasses(collectedContext);
			}
		} else {
			contextProperties = ImmutableMap.of();
			contextClasses = ImmutableMap.of();
		}

		final Set<PropertyTree> trees = buildTrees(slicedTable, declaredProperties, contextProperties, contextClasses,
				onlyWithProperties, onlyDeclaredAsContext, random, maxColumnSampleSize);

		return trees;
	}

	private static Map<Integer, DeclaredEntity> getDeclaredProperties(final Path declaredPropertiesPath,
			final String tableFileName) {
		final Path propertiesPath = getPropertiesPath(declaredPropertiesPath, tableFileName);
		if (propertiesPath == null) {
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
			return rows.stream()
					.collect(ImmutableMap.toImmutableMap(fields -> Integer.parseInt(fields[fields.length - 1]),
							fields -> new DeclaredEntity(URI.create(fields[0]), ImmutableSet.of(fields[1]))));
		} catch (final ArrayIndexOutOfBoundsException e) {
			return ImmutableMap.of();
		}
	}

	private static Path getPropertiesPath(final Path declaredPropertiesPath, final String tableFileName) {
		final Path propertiesPath = declaredPropertiesPath.resolve(tableFileName);
		if (!propertiesPath.toFile().exists()) {
			return null;
		}

		return propertiesPath;
	}

	private Set<PropertyTree> buildTrees(final SlicedTable slicedTable,
			final Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			final Map<? extends Integer, ? extends DeclaredEntity> contextProperties,
			final Map<? extends Integer, ? extends DeclaredEntity> contextClasses, final boolean onlyWithProperties,
			final boolean onlyDeclaredAsContext, final Random random,
			final int maxColumnSampleSize) {
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

			final List<Value> values = createColumnSample(random, numericColumn, maxColumnSampleSize);

			final Partition partition = new Partition(values.stream().filter(e -> e.isNumberLike())
					.map(e -> (NumberLikeValue) e).collect(ImmutableList.toImmutableList()));
			if (partition.size() < MINIMUM_PARTITION_SIZE) {
				continue;
			}

			final Set<CommonNode> children = buildChildren(partition, availableContextColumnIndices, slicedTable,
					MINIMUM_PARTITION_RELATIVE_SIZE, MAXIMUM_PARTITION_RELATIVE_SIZE, MINIMUM_PARTITION_SIZE);

			final RootNode rootNode = new RootNode(label, ImmutableMultiset.copyOf(partition.getValues()));
			rootNode.addChildren(children);

			final DeclaredEntity declaredProperty = declaredProperties.get(columnIndex);
			if (declaredProperty == null) {
				if (onlyWithProperties) {
					continue;
				}
			}

			final Context context = new Context(slicedTable.getHeaders(), slicedTable.getMetadata().getAuthor(),
					slicedTable.getMetadata().getTitle(), declaredProperty,
					onlyDeclaredAsContext ? getMeaningfulEntities(declaredProperties)
							: getMeaningfulEntities(contextProperties),
					onlyDeclaredAsContext ? ImmutableMap.of() : contextClasses, columnIndex,
					availableContextColumnIndices);

			final PropertyTree tree = new PropertyTree(rootNode, context);
			rootNode.setPropertyTree(tree);
			propertyTreesBuilder.add(tree);
		}

		return propertyTreesBuilder.build();
	}

	private List<Value> createColumnSample(final Random random, final Entry<Integer, List<Value>> numericColumn,
			final int maxColumnSampleSize) {
		final List<Value> values = new ArrayList<>(numericColumn.getValue());
		final int initialRowsSize = values.size();
		final int toRemove = Math.max(0, initialRowsSize - maxColumnSampleSize);

		for (int i = 0; i < toRemove; i++) {
			final int removedIndex = random.nextInt(values.size());
			values.remove(removedIndex);
		}
		return values;
	}

	private Format getFormat(final CsvProfile csvProfile) {
		final Format format;
		if (csvProfile == null) {
			format = null;
		} else {
			format = new Format(Charset.forName(csvProfile.getEncoding()),
					csvProfile.getDelimiter() == null ? null : csvProfile.getDelimiter().charAt(0), true,
					csvProfile.getQuotechar() == null ? null : csvProfile.getQuotechar().charAt(0), null, null);
		}
		return format;
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
		if (csvProfile == null || AVOID_HINTS) {
			hints = ImmutableMap.of();
		} else {
			final List<Type> types = csvProfile.getTypes();
			hints = toHints(types);
		}
		return hints;
	}

	private CsvProfile profile(final CsvWriter csvWriter, final Path input, final Path profilesDirectory,
			final Path cleanedInput) {
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
			csvWriter.writeRow("Previously failed profiling attempt for " + input + "!");

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
				csvWriter.writeRow("Failed profiling attempt for " + input + "!");

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

	private Path clean(final CsvWriter csvWriter, final Path input, final Path cleanedInputFilesDirectory) {
		Path cleanedInput = cleanedInputFilesDirectory.resolve(input.getFileName());
		final Path failedCleanNotice = cleanedInputFilesDirectory.resolve(input.getFileName() + ".fail");
		if (cleanedInput.toFile().exists()) {
			csvWriter.writeRow("File " + input + " already cleaned.");
		} else if (failedCleanNotice.toFile().exists()) {
			csvWriter.writeRow("Previously failed cleaning attempt for " + input + ". Using original instead.");

			cleanedInput = input;
		} else {
			try (final InputStream cleanedInputStream = csvCleanService.clean(input.toFile())) {
				Files.copy(cleanedInputStream, cleanedInput);
			} catch (final IllegalStateException e) {
				csvWriter.writeRow("Failed clean attempt for " + input + "!");

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

	private void cacheFailedContextCollection(Path collectionResultsDirectory, Path input) throws IOException {
		Files.createFile(collectionResultsDirectory.resolve(input.getFileName() + ".fail"));
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

	private void saveCollectionResult(final ResultValue result, final Path resultOutput)
			throws IOException, JsonGenerationException, JsonMappingException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(resultOutput.toFile(), result);
	}

	private CsvProfile loadProfile(final Path profileInput)
			throws IOException, JsonParseException, JsonMappingException {
		final CsvProfile csvProfile;
		final ObjectMapper mapper = new ObjectMapper();
		csvProfile = mapper.readValue(profileInput.toFile(), CsvProfile.class);
		return csvProfile;
	}

	private ResultValue loadCollectionResult(final Path resultInput)
			throws IOException, JsonParseException, JsonMappingException {
		final ResultValue result;
		final ObjectMapper mapper = new ObjectMapper();
		result = mapper.readValue(resultInput.toFile(), ResultValue.class);
		return result;
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
			final SharedPairNode subtree = new SharedPairNode(new AttributeValuePair(this.uuidGenerator.generate(), subattribute, subvalue),
					ImmutableMultiset.copyOf(subpartition.getValues()));
			subtree.addChildren(subchildren);

			children.add(subtree);
		}

		return children.build();
	}

	private AnnotationResult annotateTable(final CsvWriter csvWriter, final Path input,
			final BackgroundKnowledgeGraph graph, final Path cleanedInputFilesDirectory, final Path profilesDirectory,
			final Path collectionResultsDirectory,
			final Random random, int maxColumnSampleSize) {
		final Path cleanedInput = clean(csvWriter, input, cleanedInputFilesDirectory);

		final CsvProfile csvProfile = profile(csvWriter, input, profilesDirectory, cleanedInput);

		final Format format = getFormat(csvProfile);

		/* Parse the input file to table. */
		final ParsedTable parsedTable = parse(input, cleanedInput, format);
		if (parsedTable.getHeight() < 2) {
			throw new IllegalArgumentException("Too few rows in " + input + ". Skipping.");
		}

		/* Assign data types to each table cell. */
		final Map<Integer, Type> hints = getHints(csvProfile);

		final TypedTable typedTable = tableAnalyzer.infer(parsedTable, Locale.forLanguageTag("de-at"), hints);
		if (typedTable.getHeight() < 2) {
			throw new IllegalArgumentException("The table to annotate must have at least two rows!");
		}

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, typedTable,
				hints);

		final Map<Integer, DeclaredEntity> contextProperties;
		final Map<Integer, DeclaredEntity> contextClasses;
		final ResultValue collectedContext = getCollectedContext(csvWriter, parsedTable, input,
				collectionResultsDirectory, random);
		if (collectedContext == null) {
			contextProperties = ImmutableMap.of();
			contextClasses = ImmutableMap.of();
		} else {
			contextProperties = getContextProperties(collectedContext);
			contextClasses = getContextClasses(collectedContext);
		}

		return new AnnotationResult(parsedTable, annotate(graph, slicedTable, contextProperties,
				contextClasses, random, maxColumnSampleSize));
	}

	private Map<Integer, DeclaredEntity> getContextClasses(final ResultValue collectedContext) {
		final Map<Integer, DeclaredEntity> result = IntStream.range(0, collectedContext.getHeaderAnnotations().size())
				.filter(i -> {
					final HeaderAnnotationValue annotation = collectedContext.getHeaderAnnotations().get(i);
					if (annotation == null) {
						return false;
					}

					final Map<String, Set<EntityCandidateValue>> chosen = annotation.getChosen();
					if (chosen == null) {
						return false;
					}

					final Set<EntityCandidateValue> baseChosen = chosen.get(PRIMARY_BASE);
					if (baseChosen == null || baseChosen.isEmpty()) {
						return false;
					}

					return true;
				}).mapToObj(i -> Integer.valueOf(i))
				.collect(Collectors.toMap(i -> i, i -> toDeclared(collectedContext.getHeaderAnnotations().get(i)
						.getChosen().get(PRIMARY_BASE).stream().findFirst().get().getEntity())));

		return ImmutableMap.copyOf(result);
	}

	private static DeclaredEntity toDeclared(final EntityValue entity) {
		return new DeclaredEntity(URI.create(entity.getResource()), ImmutableSet.of(entity.getLabel()));
	}

	private Map<Integer, DeclaredEntity> getContextProperties(final ResultValue collectedContext) {
		final Map<Integer, DeclaredEntity> result = collectedContext.getColumnRelationAnnotationsAlternative()
				.entrySet().stream().filter(entry -> {
					final ColumnRelationAnnotationValue annotation = entry.getValue();
					if (annotation == null) {
						return false;
					}

					final Map<String, Set<EntityCandidateValue>> chosen = annotation.getChosen();
					if (chosen == null) {
						return false;
					}

					final Set<EntityCandidateValue> baseChosen = chosen.get(PRIMARY_BASE);
					if (baseChosen == null || baseChosen.isEmpty()) {
						return false;
					}

					return true;
				})
				.collect(Collectors.toMap(entry -> entry.getKey().getSecond().getIndex(),
						entry -> toDeclared(
								entry.getValue().getChosen().get(PRIMARY_BASE).stream().findFirst().get().getEntity()),
						(f, s) -> f));

		return ImmutableMap.copyOf(result);
	}

	private ResultValue getCollectedContext(final CsvWriter csvWriter, final ParsedTable parsedTable, final Path input,
			final Path collectionResultsDirectory, final Random random) {
		ResultValue result = null;
		final Path resultInput = collectionResultsDirectory.resolve(input.getFileName());
		final Path failedCollectionNotice = collectionResultsDirectory.resolve(input.getFileName() + ".fail");
		if (resultInput.toFile().exists()) {
			try {
				result = loadCollectionResult(resultInput);
			} catch (IOException e) {
				throw new RuntimeException("Failed to load context collection result for " + input + "!", e);
			}
		} else if (failedCollectionNotice.toFile().exists()) {
			csvWriter.writeRow("Previously failed context collection attempt for " + input + "!");

			result = null;
		} else {
			try {
				result = contextCollectionService.process(parsedTable, USED_BASES, PRIMARY_BASE, random);

				try {
					saveCollectionResult(result, resultInput);
				} catch (final IOException e) {
					throw new RuntimeException("Failed to save collected context for " + input + "!", e);
				}
			} catch (final Exception e) {
				csvWriter.writeRow("Failed context collection attempt for " + input + "! Cause: " + e.getMessage());

				result = null;
				try {
					cacheFailedContextCollection(collectionResultsDirectory, input);
				} catch (IOException e1) {
					throw new RuntimeException("Failed to note failed context collection for " + input + "!", e1);
				}
			}
		}
		return result;
	}

	private Map<Integer, Annotation> annotate(final BackgroundKnowledgeGraph graph, final SlicedTable slicedTable,
			final Map<? extends Integer, ? extends DeclaredEntity> contextProperties,
			final Map<? extends Integer, ? extends DeclaredEntity> contextClasses,
			final Random random,
			final int maxColumnSampleSize) {
		final ImmutableMap.Builder<Integer, Annotation> builder = ImmutableMap.builder();

		final Set<Integer> availableContextColumnIndices = slicedTable.getContextColumns().keySet();

		for (final Entry<Integer, List<Value>> numericColumn : slicedTable.getDataColumns().entrySet()) {
			final int columnIndex = numericColumn.getKey();
			final Label label = slicedTable.getHeaders().get(columnIndex);

			final List<Value> values = createColumnSample(random, numericColumn, maxColumnSampleSize);

			final Partition partition = new Partition(values.stream().filter(e -> e.isNumberLike())
					.map(e -> (NumberLikeValue) e).collect(ImmutableList.toImmutableList()));
			if (partition.size() < MINIMUM_PARTITION_SIZE) {
				continue;
			}

			final Set<CommonNode> children = buildChildren(partition, availableContextColumnIndices, slicedTable,
					MINIMUM_PARTITION_RELATIVE_SIZE, MAXIMUM_PARTITION_RELATIVE_SIZE, MINIMUM_PARTITION_SIZE);

			final RootNode rootNode = new RootNode(label, ImmutableMultiset.copyOf(partition.getValues()));
			rootNode.addChildren(children);

			final Context context = new Context(slicedTable.getHeaders(), slicedTable.getMetadata().getAuthor(),
					slicedTable.getMetadata().getTitle(), null,
					getMeaningfulEntities(contextProperties),
					contextClasses, columnIndex,
					availableContextColumnIndices);

			final PropertyTree tree = new PropertyTree(rootNode, context);
			rootNode.setPropertyTree(tree);

			final ImmutableMultiset.Builder<MeasuredNode> treeMatchingNodesBuilder = ImmutableMultiset.builder();

			for (final Node node : tree) {
				final SortedSet<MeasuredNode> matchingNodes = topKNodesMatcher.match(graph, node, VALUES_WEIGHT,
						PROPERTIES_WEIGHT, CLASSES_WEIGHT, TOP_K_NEGHBOURS);
				treeMatchingNodesBuilder.addAll(matchingNodes);
			}

			final Multiset<MeasuredNode> treeMatchingNodes = treeMatchingNodesBuilder.build();

			final SetMultimap<Property, MeasuredNode> propertyLevelAggregates = treeMatchingNodes.stream()
					.collect(ImmutableSetMultimap.toImmutableSetMultimap(e -> e.getNode().getProperty(), identity()));
			final SortedSet<Property> propertyAggregates = propertiesResultAggregator
					.aggregate(propertyLevelAggregates);
			final List<Property> properties = cutOff(propertyAggregates);

			final Map<Property, Statistics> propertiesStatistics = getStatistics(properties, propertyLevelAggregates);

			builder.put(columnIndex, Annotation.of(properties, ImmutableList.of(), ImmutableList.of(),
					propertiesStatistics, ImmutableMap.of(), ImmutableMap.of()));
		}

		return builder.build();
	}

	private Map<Integer, DeclaredEntity> getMeaningfulEntities(
			final Map<? extends Integer, ? extends DeclaredEntity> declaredProperties) {
		return declaredProperties.entrySet().stream().filter(e -> !STOP_ENTITIES.contains(e.getValue().getUri()))
				.collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> e.getValue()));
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
			final double relativeOccurence = occurence / ((double) (aspectLevelAggregates.size()));

			final Statistics statistics = Statistics.of(average, median, occurence, relativeOccurence);
			builder.put(aspect, statistics);
		}

		return builder.build();
	}

	private <T> List<T> cutOff(final Collection<T> aggregates) {
		return ImmutableList.copyOf(aggregates).subList(0, Math.min(aggregates.size(), TOP_K_AGGREGATED_RESULTS));
	}
}
