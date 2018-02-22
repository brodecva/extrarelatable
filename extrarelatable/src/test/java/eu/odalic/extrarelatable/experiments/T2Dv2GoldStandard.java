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
import org.junit.Before;
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
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
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
	private static final double RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD = Double.parseDouble(System.getProperty("eu.odalic.extrarelatable.relativeColumnTypeValuesOccurenceThreshold", "0.6"));
	private static final String RESOURCES_PATH = System.getProperty("eu.odalic.extrarelatable.resourcesPath");
	private static final String INSTANCE_SUBPATH = "extended_instance_goldstandard";
	private static final String SET_SUBPATH = "tables";
	private static final String DECLARED_PROPERTIES_SUBPATH = "property";
	private static final double MINIMUM_PARTITION_RELATIVE_SIZE = Double.parseDouble(System.getProperty("eu.odalic.extrarelatable.minimumPartitionRelativeSize", "0.01"));
	private static final double MAXIMUM_PARTITION_RELATIVE_SIZE = Double.parseDouble(System.getProperty("eu.odalic.extrarelatable.maximumPartitionRelativeSize", "0.99"));
	private static final int TOP_K_NEGHBOURS = Integer.parseInt(System.getProperty("eu.odalic.extrarelatable.topKNeighbours", "25"));;
	private static final int TOP_K_AGGREGATED_RESULTS = Integer.parseInt(System.getProperty("eu.odalic.extrarelatable.topKAggregatedResults", "5"));
	private static final int MINIMUM_PARTITION_SIZE = 2;
	private static final double SAMPLE_SIZE_STEP_RATIO = Double.parseDouble(System.getProperty("eu.odalic.extrarelatable.sampleSizeStepRatio", "0.1"));
	private static final long SEED = Long.parseLong(System.getProperty("eu.odalic.extrarelatable.seed", String.valueOf(System.currentTimeMillis())));
	private static final String PROFILES_DIRECTORY = "profiles";
	private static final String CONVERTED_INPUT_FILES_DIRECTORY = "csvs";
	private static final String CHARACTERS_TO_SANITIZE_REGEX = "[.]";
	private static final String SANITIZE_WITH = "_";
	private static final boolean FILES_ONLY_WITH_PROPERTIES = Boolean.parseBoolean(System.getProperty("eu.odalic.extrarelatable.filesOnlyWithProperties", "true"));
	private static final boolean NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES = Boolean.parseBoolean(System.getProperty("eu.odalic.extrarelatable.numericColumnsOnlyWithProperties", "true"));
	private static final List<Integer> CHOSEN_SAMPLES_INDICES = System.getProperty("eu.odalic.extrarelatable.chosenSampleIndices") == null ? null : Splitter.on(",").splitToList(System.getProperty("eu.odalic.extrarelatable.chosenSampleIndices")).stream().map(e -> Integer.parseInt(e)).collect(ImmutableList.toImmutableList());
	private static final int TEST_REPETITIONS = Integer.parseInt(System.getProperty("eu.odalic.extrarelatable.testRepetitions", "1"));
	private static final Multimap<URI, URI> IS_ACCEPTABLE_FOR_PAIRS = ImmutableMultimap.of(
		URI.create("http://dbpedia.org/ontology/year"), URI.create("http://dbpedia.org/ontology/releaseDate"),
		URI.create("http://dbpedia.org/ontology/year"), URI.create("http://dbpedia.org/ontology/foundingYear")
	);
	private static final double VALUES_WEIGHT = Double.parseDouble(System.getProperty("eu.odalic.extrarelatable.valuesWeight", "1"));
	private static final Set<URI> STOP_PROPERTIES = ImmutableSet.of(URI.create("http://www.w3.org/2000/01/rdf-schema#label"));

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
	@Qualifier("averageDistance")
	private ResultAggregator propertiesResultAggregator;

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

	private TestStatistics.Builder testStatisticsBuilder;
	
	@Before
	public void setUp() {
		this.testStatisticsBuilder = TestStatistics.builder();
	}
	
	@Test
	public void test() throws IOException {
		final CsvWriterSettings csvWriterSettings = new com.univocity.parsers.csv.CsvWriterSettings();
		final CsvWriter csvWriter = new CsvWriter(System.out, StandardCharsets.UTF_8, csvWriterSettings);
		
		final List<TestStatistics> results = new ArrayList<>();
		
		if (CHOSEN_SAMPLES_INDICES != null) {
			for (final int sampleSizeIndex : CHOSEN_SAMPLES_INDICES) {
				final TestStatistics testStatistics = testSample(csvWriter, SAMPLE_SIZE_STEP_RATIO, sampleSizeIndex, TEST_REPETITIONS);
				csvWriter.flush();
				if (testStatistics != null) {
					results.add(testStatistics);
				}
			}
		} else {
			int sampleSizeIndex = 0;
			while (true) {
				final TestStatistics testStatistics = testSample(csvWriter, SAMPLE_SIZE_STEP_RATIO, sampleSizeIndex, TEST_REPETITIONS);
				csvWriter.flush();
				if (testStatistics == null) {
					break;
				}
				
				results.add(testStatistics);
				sampleSizeIndex++;
			};
		}
		
		csvWriter.writeRow(
				"Files",
				"To learn",
				"To test",
				"Learnt",
				"Tested",
				"Columns",
				"Context columns",
				"Numeric columns to learn",
				"Learnt numeric columns",
				"Annotated numeric columns",
				"Numeric columns to learn without property",
				"Numeric columns to test without property",
				"Unique numeric column properties",
				"Unique numeric column properties learnt",
				"Unique numeric column properties tested",
				"Matching",
				"Missing",
				"Nonmatching",
				"Nonmatching available"
		);
		for (final TestStatistics testStatistics : results) {
			csvWriter.writeRow(
					testStatistics.getFilesCount(),
					testStatistics.getLearningFilesCount(),
					testStatistics.getTestFilesCount(),
					testStatistics.getLearntFiles(),
					testStatistics.getTestedFiles(),
					testStatistics.getLearningColumnsCount(),
					testStatistics.getLearntContextColumnsCount(),
					testStatistics.getAttemptedLearntNumericColumns(),
					testStatistics.getLearntNumericColumns(),
					testStatistics.getAnnotatedNumericColumns(),
					testStatistics.getNoPropertyLearningNumericColumns(),
					testStatistics.getNoPropertyTestingNumericColums(),
					testStatistics.getUniqueProperties(),
					testStatistics.getUniquePropertiesLearnt(),
					testStatistics.getUniquePropertiesTested(),
					testStatistics.getMatchingSolutions(),
					testStatistics.getMissingSolutions(),
					testStatistics.getNonmatchingSolutions(),
					testStatistics.getNonmatchingAvailableSolutions()
					);
		}
		
		csvWriter.flush();
		csvWriter.close();
	}
	
	private TestStatistics testSample(final CsvWriter csvWriter, final double sampleSizeStepRatio, final int sampleSizeIndex, final int repetitions) throws IOException {
		final Random random = new Random(SEED);
		testStatisticsBuilder.setSeed(SEED);
		
		testStatisticsBuilder.setRepetitions(repetitions);

		for (int repetition = 0; repetition < repetitions; repetition++) {
			final boolean succeeded = testSample(csvWriter, sampleSizeStepRatio, sampleSizeIndex, random, repetition);
			
			if (!succeeded) {
				return null;
			}
		}
		
		csvWriter.writeEmptyRow();
		csvWriter.writeRow("Finished all sample repetitions.");
		
		return testStatisticsBuilder.build();
	}
	
	private boolean testSample(final CsvWriter csvWriter, final double sampleSizeStepRatio, final int sampleSizeIndex, final Random random, final int repetition) throws IOException {
		final Path resourcesPath = Paths.get(RESOURCES_PATH);
		final Path instancePath = resourcesPath.resolve(INSTANCE_SUBPATH);

		final Path setPath = instancePath.resolve(SET_SUBPATH);
		final Path declaredPropertiesPath = instancePath.resolve(DECLARED_PROPERTIES_SUBPATH);

		final List<Path> files = getFiles(setPath, declaredPropertiesPath, FILES_ONLY_WITH_PROPERTIES);
		testStatisticsBuilder.addFilesCount(files.size());

		final List<Path> testPaths = getTestFiles(random, sampleSizeStepRatio, sampleSizeIndex, files);
		if (testPaths == null) {
			return false;
		}
		
		testStatisticsBuilder.addTestFilesCount(testPaths.size());
		
		final List<Path> learningPaths = getLearningFiles(files, testPaths);
		testStatisticsBuilder.addLearningFilesCount(learningPaths.size());

		final Path inputFilesPath = setPath.resolve(CONVERTED_INPUT_FILES_DIRECTORY);
		final Path profilesPath = setPath.resolve(PROFILES_DIRECTORY);
		
		final BackgroundKnowledgeGraph graph = learn(csvWriter, learningPaths, inputFilesPath, profilesPath,
				declaredPropertiesPath, NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES, repetition);

		csvWriter.writeEmptyRow();
		csvWriter.writeEmptyRow();
		csvWriter.writeEmptyRow();
		csvWriter.writeEmptyRow();
		
		test(csvWriter, testPaths, graph, inputFilesPath, profilesPath, declaredPropertiesPath, NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES, repetition);
		
		csvWriter.writeEmptyRow();
		csvWriter.writeRow("Finished.");
		
		csvWriter.writeEmptyRow();
		
		return true;
	}

	private ImmutableList<Path> getLearningFiles(final List<Path> files, final List<Path> testPaths) {
		return files.stream().filter(e -> !testPaths.contains(e)).collect(ImmutableList.toImmutableList());
	}

	private static List<Path> getTestFiles(final Random random, final double sampleSizeStepRatio, final int sampleSizeIndex, final List<Path> files) {
		final int allFilesSize = files.size();
		final double sampleSize = allFilesSize - sampleSizeIndex * sampleSizeStepRatio * allFilesSize;
		if (sampleSize < 0) {
			return null;
		}
		
		final int wholeSampleSize = (int) sampleSize;
		
		final ImmutableList.Builder<Path> testPathsBuilder = ImmutableList.builder();
		for (int i = 0; i < wholeSampleSize; i++) {
			final int removedIndex = random.nextInt(files.size());

			testPathsBuilder.add(files.remove(removedIndex));
		}
		final List<Path> testPaths = testPathsBuilder.build();
		return testPaths;
	}

	private static List<Path> getFiles(final Path setPath, final Path declaredPropertiesPath, final boolean onlyWithProperties) throws IOException {
		final List<Path> paths = Streams.stream(Files.newDirectoryStream(setPath))
				.filter(path -> path.toFile().isFile()
						&& !path.getFileName().toString().startsWith(FILES_WITHOUT_PROPERTY_PREFIX)
						&& ((!onlyWithProperties) || getPropertiesPath(declaredPropertiesPath, path.getFileName().toString()) != null)
				)
				.collect(Collectors.toCollection(ArrayList::new));

		Collections.sort(paths);
		return paths;
	}

	private BackgroundKnowledgeGraph learn(final CsvWriter csvWriter, final Collection<? extends Path> paths,
			final Path cleanedInputFilesDirectory, final Path profilesDirectory, final Path declaredPropertiesPath,
			final boolean onlyWithProperties, final int repetition)
			throws IOException {
		final BackgroundKnowledgeGraph graph = new BackgroundKnowledgeGraph(propertyTreesMergingStrategy);

		paths.forEach(file -> {
			final Set<PropertyTree> trees = learnFile(csvWriter, file, cleanedInputFilesDirectory, profilesDirectory,
					declaredPropertiesPath, onlyWithProperties, repetition);

			graph.addPropertyTrees(trees);
		});

		return graph;
	}

	private void test(final CsvWriter csvWriter, final Collection<? extends Path> paths, final BackgroundKnowledgeGraph graph,
			final Path convertedInputFilesDirectory, final Path profilesDirectory, final Path declaredPropertiesPath, final boolean onlyWithProperties,
			final int repetition)
			throws IOException {
		paths.forEach(file -> {
			csvWriter.writeRow("File:", file);

			final Map<Integer, URI> solution = getSolution(file, declaredPropertiesPath);
			
			final AnnotationResult result;
			try {
				result = annotateTable(csvWriter, file, graph, convertedInputFilesDirectory, profilesDirectory,
						declaredPropertiesPath, onlyWithProperties, repetition);
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
			
			csvWriter.addValue("Declared properties:");
			csvWriter.addValues(solution);
			csvWriter.writeValuesToRow();
			
			csvWriter.writeRow("First rows:");
			csvWriter.writeRows(
					parsedTable.getRows().subList(0, Math.min(parsedTable.getHeight(), 5)).stream()
					.map(row -> row.toArray()).collect(ImmutableList.toImmutableList())
			);
			
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
				csvWriter.writeRow("Mean distance", "Median distance", "Occurence", "Relative occurence", "URI", "Context properties");
				csvWriter.writeRows(annotation.getProperties().stream().map(property -> {
					final Statistics statistics = propertiesStatistics.get(property);

					return new Object[] { statistics.getAverage(), statistics.getMedian(),
							statistics.getOccurence(), statistics.getRelativeOccurence(), property.getUri(), getContextProperties(property)};
				}).collect(ImmutableList.toImmutableList()));
				
				final URI columnSolution = solution.get(index);
				csvWriter.writeRow("Solution:", columnSolution);
				
				if (columnSolution == null) {
					testStatisticsBuilder.addMissingSolution();
				} else {
					if (annotation.getProperties().stream().map(property -> property.getUri()).anyMatch(uri -> isAcceptableFor(uri, columnSolution))) {
						testStatisticsBuilder.addMatchingSolution();
					} else {
						testStatisticsBuilder.addNonmatchingSolution(repetition, columnSolution);
					}
				}
				
				testStatisticsBuilder.addAnnotatedNumericColumn();
				
				testStatisticsBuilder.addAnnotatedNumericColumn();
			});
			
			testStatisticsBuilder.addTestedFile();
			
			csvWriter.writeEmptyRow();
			csvWriter.writeEmptyRow();
			csvWriter.writeEmptyRow();
		});
	}

	private static Set<URI> getContextProperties(final Property property) {
		return property.getInstances().stream().map(
				i -> i.getContext().getDeclaredContextColumnProperties().values()).flatMap(e -> e.stream()).collect(ImmutableSet.toImmutableSet());
	}

	private boolean isAcceptableFor(final URI first, final URI second) {
		return first.equals(second) || (
			IS_ACCEPTABLE_FOR_PAIRS.get(first) != null &&
			IS_ACCEPTABLE_FOR_PAIRS.get(first).contains(second)
		);
	}

	private static Map<Integer, URI> getSolution(final Path input, final Path declaredPropertiesPath) {
		return getDeclaredPropertyUris(declaredPropertiesPath, input.getFileName().toString());
	}

	private Set<PropertyTree> learnFile(final CsvWriter csvWriter, final Path input, final Path convertedInputFilesDirectory,
			final Path profilesDirectory, final Path declaredPropertiesPath, final boolean onlyWithProperties,
			final int repetition) {
		csvWriter.writeRow("Processing file:", input);

		final Path convertedInput = convert(csvWriter, input, convertedInputFilesDirectory);

		final CsvProfile csvProfile = profile(csvWriter, input, profilesDirectory, convertedInput);

		/* Parse the input file to table. */
		final Dataset dataset = parse(input);
		final HeaderPosition headerPosition = dataset.getHeaderPosition();
		if (headerPosition != HeaderPosition.FIRST_ROW) {
			csvWriter.writeRow("File " + input + " has no regular header!");
			
			testStatisticsBuilder.addIrregularHeaderFile();
			
			return ImmutableSet.of();
		}

		final ParsedTable table = toParsedTable(dataset, input.getFileName().toString());
		if (table.getHeight() < 2) {
			csvWriter.writeRow("Too few rows in " + input + ". Skipping.");
			
			testStatisticsBuilder.addFewRowsFile();
			
			return ImmutableSet.of();
		}

		/* Assign data types to each table cell. */
		final Map<Integer, Type> hints = getHints(csvProfile);

		final TypedTable typedTable = tableAnalyzer.infer(table, Locale.US, hints);
		if (typedTable.getHeight() < 2) {
			csvWriter.writeRow("Too few typed rows in " + input + ". Skipping.");
			
			testStatisticsBuilder.addFewTypedRowsFile();
			
			return ImmutableSet.of();
		}

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, typedTable,
				hints);

		final Map<Integer, URI> declaredPropertyUris = getDeclaredPropertyUris(declaredPropertiesPath,
				input.getFileName().toString());

		Set<PropertyTree> trees = buildTrees(slicedTable, declaredPropertyUris, onlyWithProperties, repetition);
		
		testStatisticsBuilder.addLearntFile();
		
		return trees;
	}

	private static Map<Integer, URI> getDeclaredPropertyUris(final Path declaredPropertiesPath,
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
		return rows.stream().collect(ImmutableMap.toImmutableMap(fields -> Integer.parseInt(fields[fields.length - 1]),
						fields -> URI.create(fields[0])));
		} catch (final ArrayIndexOutOfBoundsException e) {
			return ImmutableMap.of();
		}
	}

	private static Path getPropertiesPath(final Path declaredPropertiesPath, final String tableFileName) {
		final String fileName = com.google.common.io.Files.getNameWithoutExtension(tableFileName);

		final String sanitizedFileName = fileName.replaceAll(CHARACTERS_TO_SANITIZE_REGEX, SANITIZE_WITH);

		final String propertiesFileName = sanitizedFileName + ".csv";

		final Path propertiesPath = declaredPropertiesPath.resolve(propertiesFileName);
		if (!propertiesPath.toFile().exists()) {
			return null;
		}
		
		return propertiesPath;
	}

	private Set<PropertyTree> buildTrees(final SlicedTable slicedTable,
			final Map<? extends Integer, ? extends URI> declaredPropertyUris, final boolean onlyWithProperties, final int repetition) {
		/*
		 * For each numeric column and its set of numeric values compute the possible
		 * sub-contexts and order them by distance in descending order from the set.
		 * 
		 * Use the farthest sub-context to partition the set of values into nodes and
		 * recursively compute the sub-context for them.
		 */
		final ImmutableSet.Builder<PropertyTree> propertyTreesBuilder = ImmutableSet.builder();

		testStatisticsBuilder.addLearningColumnsCount(slicedTable.getWidth());
		
		final Set<Integer> availableContextColumnIndices = slicedTable.getContextColumns().keySet();
		
		testStatisticsBuilder.addLearntContextColumnsCount(availableContextColumnIndices.size());

		for (final Entry<Integer, List<Value>> numericColumn : slicedTable.getDataColumns().entrySet()) {
			testStatisticsBuilder.addAttemptedLearntNumericColumn();
			
			final int columnIndex = numericColumn.getKey();
			final Label label = slicedTable.getHeaders().get(columnIndex);

			final Partition partition = new Partition(numericColumn.getValue().stream().filter(e -> e.isNumeric())
					.map(e -> (NumericValue) e).collect(ImmutableList.toImmutableList()));
			if (partition.size() < MINIMUM_PARTITION_SIZE) {
				testStatisticsBuilder.addTooSmallNumericColumn();
				
				continue;
			}

			final Set<CommonNode> children = buildChildren(partition, availableContextColumnIndices, slicedTable,
					MINIMUM_PARTITION_RELATIVE_SIZE, MAXIMUM_PARTITION_RELATIVE_SIZE, MINIMUM_PARTITION_SIZE);

			final RootNode rootNode = new RootNode(label, ImmutableMultiset.copyOf(partition.getValues()));
			rootNode.addChildren(children);

			final URI declaredPropertyUri = declaredPropertyUris.get(columnIndex);
			if (declaredPropertyUri == null) {
				testStatisticsBuilder.addNoPropertyLearningNumericColumn();
				
				if (onlyWithProperties) {
					continue;
				}
			} else {
				testStatisticsBuilder.addUniqueProperty(repetition, declaredPropertyUri);
				testStatisticsBuilder.addUniquePropertyLearnt(repetition, declaredPropertyUri);
			}

			final Context context = new Context(slicedTable.getHeaders(), slicedTable.getMetadata().getAuthor(),
					slicedTable.getMetadata().getTitle(), declaredPropertyUri, getMeaningfulContextProperties(declaredPropertyUris), columnIndex, availableContextColumnIndices);

			final PropertyTree tree = new PropertyTree(rootNode, context);
			rootNode.setPropertyTree(tree);
			propertyTreesBuilder.add(tree);
			
			testStatisticsBuilder.addLearntNumericColumn();
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
				new Metadata(fileName, dataset.getUrl(), null));

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

	private CsvProfile profile(final CsvWriter csvWriter, final Path input, final Path profilesDirectory, final Path convertedInput) {
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
				csvProfile = csvProfilerService.profile(convertedInput.toFile());

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

	private Path convert(final CsvWriter csvWriter, final Path input, final Path convertedInputFilesDirectory) {
		Path convertedInput = convertedInputFilesDirectory.resolve(input.getFileName() + ".csv");
		if (convertedInput.toFile().exists()) {
			csvWriter.writeRow("File " + input + " already converted.");
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

	private AnnotationResult annotateTable(final CsvWriter csvWriter, final Path input, final BackgroundKnowledgeGraph graph,
			final Path convertedInputFilesDirectory, final Path profilesDirectory, final Path declaredPropertiesPath,
			final boolean onlyWithProperties, final int repetition) {
		final Path convertedInput = convert(csvWriter, input, convertedInputFilesDirectory);

		final CsvProfile csvProfile = profile(csvWriter, input, profilesDirectory, convertedInput);

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

		return new AnnotationResult(parsedTable, annotate(graph, slicedTable, declaredPropertyUris, onlyWithProperties, repetition));
	}

	private Map<Integer, Annotation> annotate(final BackgroundKnowledgeGraph graph, final SlicedTable slicedTable,
			final Map<? extends Integer, ? extends URI> declaredPropertyUris, final boolean onlyWithProperties,
			final int repetition) {
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
			if (declaredPropertyUri == null) {
				testStatisticsBuilder.addNoPropertyTestingNumericColumn();
				
				if (onlyWithProperties) {
					continue;
				}
			} else {
				testStatisticsBuilder.addUniqueProperty(repetition, declaredPropertyUri);
				testStatisticsBuilder.addUniquePropertyTested(repetition, declaredPropertyUri);
			}

			final Context context = new Context(slicedTable.getHeaders(), slicedTable.getMetadata().getAuthor(),
					slicedTable.getMetadata().getTitle(), declaredPropertyUri, getMeaningfulContextProperties(declaredPropertyUris), columnIndex, availableContextColumnIndices);

			final PropertyTree tree = new PropertyTree(rootNode, context);
			rootNode.setPropertyTree(tree);

			final ImmutableMultiset.Builder<MeasuredNode> treeMatchingNodesBuilder = ImmutableMultiset.builder();

			for (final Node node : tree) {
				final SortedSet<MeasuredNode> matchingNodes = topKNodesMatcher.match(graph, node, VALUES_WEIGHT, TOP_K_NEGHBOURS);
				treeMatchingNodesBuilder.addAll(matchingNodes);
			}

			final Multiset<MeasuredNode> treeMatchingNodes = treeMatchingNodesBuilder.build();

			final SetMultimap<Property, MeasuredNode> propertyLevelAggregates = treeMatchingNodes.stream()
					.collect(ImmutableSetMultimap.toImmutableSetMultimap(e -> e.getNode().getProperty(), identity()));
			final SortedSet<Property> propertyAggregates = propertiesResultAggregator
					.aggregate(propertyLevelAggregates);
			final List<Property> properties = cutOff(propertyAggregates);

			final Map<Property, Statistics> propertiesStatistics = getStatistics(properties, propertyLevelAggregates);

			builder.put(columnIndex, Annotation.of(properties, ImmutableList.of(), ImmutableList.of(), propertiesStatistics,
					ImmutableMap.of(), ImmutableMap.of()));
		}

		return builder.build();
	}

	private Map<Integer, URI> getMeaningfulContextProperties(
			final Map<? extends Integer, ? extends URI> declaredPropertyUris) {
		return declaredPropertyUris.entrySet().stream().filter(
				e -> !STOP_PROPERTIES.contains(e.getValue())
			).collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> e.getValue()));
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

	private <T> List<T> cutOff(final Collection<T> aggregates) {
		return ImmutableList.copyOf(aggregates).subList(0,
				Math.min(aggregates.size(), TOP_K_AGGREGATED_RESULTS));
	}
}
