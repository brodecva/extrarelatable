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
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
public class DataGvAt {

	private static final double RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD = Double.parseDouble(
			System.getProperty("eu.odalic.extrarelatable.relativeColumnTypeValuesOccurenceThreshold", "0.6"));
	private static final String INSTANCE_SUBPATH = System.getProperty("eu.odalic.extrarelatable.instancePath");
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
	private static final double SAMPLE_SIZE_STEP_RATIO = Double
			.parseDouble(System.getProperty("eu.odalic.extrarelatable.sampleSizeStepRatio", "0.1"));
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
	private static final List<Integer> CHOSEN_SAMPLES_INDICES = System
			.getProperty("eu.odalic.extrarelatable.chosenSampleIndices") == null ? null
					: Splitter.on(",").splitToList(System.getProperty("eu.odalic.extrarelatable.chosenSampleIndices"))
							.stream().map(e -> Integer.parseInt(e)).collect(ImmutableList.toImmutableList());
	private static final int TEST_REPETITIONS = Integer
			.parseInt(System.getProperty("eu.odalic.extrarelatable.testRepetitions", "1"));
	private static final boolean DRY_RUN = Boolean
			.parseBoolean(System.getProperty("eu.odalic.extrarelatable.dryRun", "false"));
	private static final boolean GROUP_DEPENDENT = Boolean
			.parseBoolean(System.getProperty("eu.odalic.extrarelatable.groupDependent", "true"));
	private static final Multimap<URI, URI> IS_ACCEPTABLE_FOR_PAIRS = ImmutableMultimap.<URI, URI>builder()
			.put(URI.create("http://dbpedia.org/ontology/population"),
					URI.create("http://dbpedia.org/ontology/numberOfEmployees"))
			.put(URI.create("http://dbpedia.org/ontology/numberOfEmployees"),
					URI.create("http://dbpedia.org/ontology/population"))
			.put(URI.create("http://dbpedia.org/ontology/population"),
					URI.create("http://dbpedia.org/ontology/numberOfDeaths"))
			.put(URI.create("http://dbpedia.org/ontology/numberOfDeaths"),
					URI.create("http://dbpedia.org/ontology/population"))
			.put(URI.create("http://dbpedia.org/ontology/population"),
					URI.create("http://dbpedia.org/ontology/popularVote"))
			.put(URI.create("http://dbpedia.org/ontology/popularVote"),
					URI.create("http://dbpedia.org/ontology/population"))
			.put(URI.create("http://dbpedia.org/ontology/population"),
					URI.create("http://dbpedia.org/ontology/numberOfHouses"))
			.put(URI.create("http://dbpedia.org/ontology/numberOfHouses"),
					URI.create("http://dbpedia.org/ontology/population"))
			.build();
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
	private static final boolean FOLDING = Boolean
			.parseBoolean(System.getProperty("eu.odalic.extrarelatable.folding", "true"));

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

		if (FOLDING) {
			final TestStatistics testStatistics = testFolding(csvWriter, SAMPLE_SIZE_STEP_RATIO, TEST_REPETITIONS);
			csvWriter.flush();
			results.add(testStatistics);
		} else {
			if (CHOSEN_SAMPLES_INDICES != null) {
				for (final int sampleSizeIndex : CHOSEN_SAMPLES_INDICES) {
					final TestStatistics testStatistics = testSample(csvWriter, SAMPLE_SIZE_STEP_RATIO, sampleSizeIndex,
							TEST_REPETITIONS);
					csvWriter.flush();
					if (testStatistics != null) {
						results.add(testStatistics);
					}
				}
			} else {
				int sampleSizeIndex = 0;
				while (true) {
					final TestStatistics testStatistics = testSample(csvWriter, SAMPLE_SIZE_STEP_RATIO, sampleSizeIndex,
							TEST_REPETITIONS);
					csvWriter.flush();
					if (testStatistics == null) {
						break;
					}
	
					results.add(testStatistics);
					sampleSizeIndex++;
				}
			}
		}

		for (final TestStatistics testStatistics : results) {
			csvWriter.writeRow("Weighted average precision iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageWeightedPrecision().toArray());
			
			csvWriter.writeRow("Weighted average recall iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageWeightedRecall().toArray());
			
			csvWriter.writeRow("Weighted average F-measure iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageWeightedFMeasure().toArray());
			
			csvWriter.writeRow("Average accuracy iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageAccuracy().toArray());
			
			csvWriter.writeRow("Average error rate iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageErrorRate().toArray());
			
			csvWriter.writeRow("Overall accuracy iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageOverallAccuracy().toArray());
			
			csvWriter.writeRow("Overall error rate iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageOverallErrorRate().toArray());
			
			csvWriter.writeRow("uPrecision iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageMicroAveragedPrecision().toArray());
			
			csvWriter.writeRow("MPrecision iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageMacroAveragedPrecision().toArray());
			
			csvWriter.writeRow("uRecall iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageMicroAveragedRecall().toArray());
			
			csvWriter.writeRow("MRecall iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageMacroAveragedRecall().toArray());
			
			csvWriter.writeRow("uF-measure iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageMicroAveragedFMeasure().toArray());
			
			csvWriter.writeRow("MF-measure iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsAverageMacroAveragedFMeasure().toArray());
			
			csvWriter.writeRow("Kappa iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsKappa().toArray());
			
			csvWriter.writeRow("Learning time iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsLearningTime().toArray());
			
			csvWriter.writeRow("Testing time iterations");
			csvWriter.writeRow(testStatistics.getAllRepetitionsTestingTime().toArray());
			
			csvWriter.writeEmptyRow();
			
			csvWriter.writeRow("Files", "To learn", "To test", "Learnt", "Tested", "Irregular header", "Few rows",
					"Few typed rows");
			csvWriter.writeRow(testStatistics.getFilesCount(), testStatistics.getLearningFilesCount(),
					testStatistics.getTestFilesCount(), testStatistics.getLearntFiles(),
					testStatistics.getTestedFiles(), testStatistics.getIrregularHeaderFiles(),
					testStatistics.getFewRowsFiles(), testStatistics.getFewTypedRowsFiles());

			csvWriter.writeRow("Learning columns", "Learnt context columns", "Testing columns",
					"Tested context columns");
			csvWriter.writeRow(testStatistics.getLearningColumnsCount(), testStatistics.getLearntContextColumnsCount(),
					testStatistics.getTestingColumnsCount(), testStatistics.getTestedContextColumnsCount());

			csvWriter.writeRow("Attempted numerics columns to learn", "Attempted numeric to test",
					"Learnt numeric columns", "Annotated numeric columns");
			csvWriter.writeRow(testStatistics.getAttemptedLearntNumericColumns(),
					testStatistics.getAttemptedTestedNumericColumns(), testStatistics.getLearntNumericColumns(),
					testStatistics.getAnnotatedNumericColumns());

			csvWriter.writeRow("Numeric columns to learn without property", "Numeric columns to test without property");
			csvWriter.writeRow(testStatistics.getNoPropertyLearningNumericColumns(),
					testStatistics.getNoPropertyTestingNumericColums());
			csvWriter.writeRow("In test missing property columns", "In learning missing property columns");
			csvWriter.writeRow(testStatistics.getInTestMissingColumns(), testStatistics.getInLearningMissingColumns());
			csvWriter.writeRow("Not enough numeric learning columns", "Not enough numeric testing columns");
			csvWriter.writeRow(testStatistics.getTooSmallLearningNumericColumns(),
					testStatistics.getTooSmallTestingNumericColumns());

			csvWriter.writeRow("Unique numeric column properties", "Unique numeric column properties learnt",
					"Unique numeric column properties tested");
			csvWriter.writeRow(testStatistics.getUniqueProperties(), testStatistics.getUniquePropertiesLearnt(),
					testStatistics.getUniquePropertiesTested());

			csvWriter.writeRow("Missing", "Matching", "Nonmatching", "Nonmatching available", "Success ratio");
			csvWriter.writeRow(testStatistics.getMissingSolutions(), testStatistics.getMatchingSolutions(),
					testStatistics.getNonmatchingSolutions(), testStatistics.getNonmatchingAvailableSolutions(),
					testStatistics.getMatchingSolutions() / (testStatistics.getMatchingSolutions()
							+ testStatistics.getNonmatchingAvailableSolutions()));

			csvWriter.writeRow("Instance matching", "Instance nonmatching", "Instance nonmatching available",
					"Instance success ratio");
			csvWriter.writeRow(testStatistics.getInstanceMatchingSolutions(),
					testStatistics.getInstanceNonmatchingSolutions(),
					testStatistics.getInstanceNonmatchingAvailableSolutions(),
					testStatistics.getInstanceMatchingSolutions() / (testStatistics.getInstanceMatchingSolutions()
							+ testStatistics.getInstanceNonmatchingAvailableSolutions()));

			csvWriter.writeRow("Weighted average precision", "Weighted average recall", "Weighted average F-measure");
			csvWriter.writeRow(testStatistics.getAverageWeightedPrecision(), testStatistics.getAverageWeightedRecall(),
					testStatistics.getAverageWeightedFMeasure());
			
			csvWriter.writeRow("Average accuracy", "Average error rate", "Overall accuracy", "Overall error rate");
			csvWriter.writeRow(testStatistics.getAverageAccuracy(), testStatistics.getAverageErrorRate(),
					testStatistics.getAverageOverallAccuracy(), testStatistics.getAverageOverallErrorRate());
			
			csvWriter.writeRow("uPrecision", "MPrecision", "uRecall", "MRecall");
			csvWriter.writeRow(testStatistics.getAverageMicroAveragedPrecision(),
					testStatistics.getAverageMacroAveragedPrecision(), testStatistics.getAverageMicroAveragedRecall(), testStatistics.getAverageMacroAveragedRecall());
			
			csvWriter.writeRow("uF-measure", "MF-measure", "Kappa");
			csvWriter.writeRow(testStatistics.getAverageMicroAveragedFMeasure(), testStatistics.getAverageMacroAveragedFMeasure(), testStatistics.getAverageKappa());
			
			
			csvWriter.writeRow("Learning time (s)", "Testing time (s)");
			csvWriter.writeRow(Duration.ofNanos((long) testStatistics.getLearningTime()).getSeconds(), Duration.ofNanos((long) testStatistics.getTestingTime()).getSeconds());

			csvWriter.writeRow("Learning time (s)", "Testing time (s)");
			csvWriter.writeRow(Duration.ofNanos((long) testStatistics.getLearningTime()).getSeconds(),
					Duration.ofNanos((long) testStatistics.getTestingTime()).getSeconds());

			csvWriter.writeEmptyRow();
		}

		csvWriter.flush();
		csvWriter.close();
	}
	
	private TestStatistics testFolding(final CsvWriter csvWriter, final double sampleSizeStepRatio, final int repetitions) throws IOException {
		final Random random = new Random(SEED);
		testStatisticsBuilder.setSeed(SEED);
		
		if (INSTANCE_SUBPATH == null) {
			throw new IllegalArgumentException("No instance path provided!");
		}

		final Path instancePath = Paths.get(INSTANCE_SUBPATH);

		final Path setPath = instancePath;
		final Path declaredPropertiesPath = instancePath.resolve(DECLARED_PROPERTIES_SUBPATH);

		final List<Path> files = getFiles(setPath, declaredPropertiesPath, FILES_ONLY_WITH_PROPERTIES);
		
		final int foldsCount = (int) Math.round(1d / sampleSizeStepRatio);
		final int foldSize = (int) Math.round(files.size() * sampleSizeStepRatio);
		
		final int foldedRepetitions = foldsCount * repetitions;
		testStatisticsBuilder.setRepetitions(foldedRepetitions);
		
		int foldedRepetition = 0;
		for (int repetition = 0; repetition < repetitions; repetition++) {
			final List<List<Path>> folds = getFolds(random, foldsCount, foldSize, files);
			
			for (int fold = 0; fold < foldsCount; fold++) {
				testStatisticsBuilder.addFilesCount(files.size());
				
				testSample(csvWriter, fold, files, folds, random, foldedRepetition);
				foldedRepetition++;
			}
		}

		csvWriter.writeEmptyRow();
		csvWriter.writeRow("Finished all sample repetitions.");

		return testStatisticsBuilder.build();
	}

	private TestStatistics testSample(final CsvWriter csvWriter, final double sampleSizeStepRatio,
			final int sampleSizeIndex, final int repetitions) throws IOException {
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

	private boolean testSample(final CsvWriter csvWriter, final double sampleSizeStepRatio, final int sampleSizeIndex,
			final Random random, final int repetition) throws IOException {
		if (INSTANCE_SUBPATH == null) {
			throw new IllegalArgumentException("No instance path provided!");
		}

		final Path instancePath = Paths.get(INSTANCE_SUBPATH);

		final Path setPath = instancePath;
		final Path declaredPropertiesPath = instancePath.resolve(DECLARED_PROPERTIES_SUBPATH);
		final Path collectionResultsDirectory = instancePath.resolve(CONTEXT_COLLECTION_RESULTS_SUBPATH);

		final List<Path> files = getFiles(setPath, declaredPropertiesPath, FILES_ONLY_WITH_PROPERTIES);
		testStatisticsBuilder.addFilesCount(files.size());

		final List<Path> testPaths = getTestFiles(random, sampleSizeStepRatio, sampleSizeIndex, files);
		if (testPaths == null) {
			return false;
		}

		testStatisticsBuilder.addTestFilesCount(testPaths.size());

		final List<Path> learningPaths = getLearningFiles(files, testPaths);
		testStatisticsBuilder.addLearningFilesCount(learningPaths.size());

		final Path cleanedInputFilesPath = setPath.resolve(CLEANED_INPUT_FILES_DIRECTORY);
		final Path profilesPath = setPath.resolve(PROFILES_DIRECTORY);

		final Set<URI> testProperties;
		if (NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES) {
			testProperties = getTestProperties(csvWriter, testPaths, cleanedInputFilesPath, profilesPath,
					declaredPropertiesPath, NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES);
		} else {
			testProperties = ImmutableSet.of();
		}

		final long learningStart = System.nanoTime();
		
		final BackgroundKnowledgeGraph graph = learn(csvWriter, learningPaths, cleanedInputFilesPath, profilesPath,
				declaredPropertiesPath, collectionResultsDirectory, NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES,
				ONLY_DECLARED_AS_CONTEXT, repetition, random, MAXIMUM_COLUMN_SAMPLE_SIZE, testProperties);

		final long learningStop = System.nanoTime();
		
		csvWriter.writeEmptyRow();
		csvWriter.writeEmptyRow();
		csvWriter.writeEmptyRow();
		csvWriter.writeEmptyRow();

		final long testStart = System.nanoTime();
		
		test(csvWriter, testPaths, graph, cleanedInputFilesPath, profilesPath, declaredPropertiesPath,
				collectionResultsDirectory, NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES, ONLY_DECLARED_AS_CONTEXT, repetition,
				random, MAXIMUM_COLUMN_SAMPLE_SIZE);

		final long testStop = System.nanoTime();
		
		testStatisticsBuilder.addLearningTime(repetition, learningStop - learningStart);
		testStatisticsBuilder.addTestingTime(repetition, testStop - testStart);
		
		csvWriter.writeEmptyRow();
		csvWriter.writeRow("Finished.");

		csvWriter.writeEmptyRow();

		return true;
	}
	
	private boolean testSample(final CsvWriter csvWriter, final int fold, final List<Path> files, final List<List<Path>> folds,
			final Random random, final int repetition) throws IOException {
		final Path instancePath = Paths.get(INSTANCE_SUBPATH);

		final Path setPath = instancePath;
		final Path declaredPropertiesPath = instancePath.resolve(DECLARED_PROPERTIES_SUBPATH);
		final Path collectionResultsDirectory = instancePath.resolve(CONTEXT_COLLECTION_RESULTS_SUBPATH);

		final List<Path> testPaths = folds.get(fold);

		testStatisticsBuilder.addTestFilesCount(testPaths.size());

		final List<Path> learningPaths = getLearningFiles(files, testPaths);
		testStatisticsBuilder.addLearningFilesCount(learningPaths.size());

		final Path cleanedInputFilesPath = setPath.resolve(CLEANED_INPUT_FILES_DIRECTORY);
		final Path profilesPath = setPath.resolve(PROFILES_DIRECTORY);

		final Set<URI> testProperties;
		if (NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES) {
			testProperties = getTestProperties(csvWriter, testPaths, cleanedInputFilesPath, profilesPath,
					declaredPropertiesPath, NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES);
		} else {
			testProperties = ImmutableSet.of();
		}

		final long learningStart = System.nanoTime();
		
		final BackgroundKnowledgeGraph graph = learn(csvWriter, learningPaths, cleanedInputFilesPath, profilesPath,
				declaredPropertiesPath, collectionResultsDirectory, NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES,
				ONLY_DECLARED_AS_CONTEXT, repetition, random, MAXIMUM_COLUMN_SAMPLE_SIZE, testProperties);

		final long learningStop = System.nanoTime();
		
		csvWriter.writeEmptyRow();
		csvWriter.writeEmptyRow();
		csvWriter.writeEmptyRow();
		csvWriter.writeEmptyRow();

		final long testStart = System.nanoTime();
		
		test(csvWriter, testPaths, graph, cleanedInputFilesPath, profilesPath, declaredPropertiesPath,
				collectionResultsDirectory, NUMERIC_COLUMNS_ONLY_WITH_PROPERTIES, ONLY_DECLARED_AS_CONTEXT, repetition,
				random, MAXIMUM_COLUMN_SAMPLE_SIZE);

		final long testStop = System.nanoTime();
		
		testStatisticsBuilder.addLearningTime(repetition, learningStop - learningStart);
		testStatisticsBuilder.addTestingTime(repetition, testStop - testStart);
		
		csvWriter.writeEmptyRow();
		csvWriter.writeRow("Finished.");

		csvWriter.writeEmptyRow();

		return true;
	}

	private List<Path> getLearningFiles(final List<Path> files, final List<Path> testPaths) {
		return files.stream().filter(e -> !testPaths.contains(e)).collect(ImmutableList.toImmutableList());
	}

	private static List<Path> getTestFiles(final Random random, final double sampleSizeStepRatio,
			final int sampleSizeIndex, final List<Path> files) {
		if (DRY_RUN) {
			return ImmutableList.of();
		}
		
		final List<Path> filesCopy = new ArrayList<>(files);

		final int allFilesSize = files.size();
		final double sampleSize = allFilesSize - sampleSizeIndex * sampleSizeStepRatio * allFilesSize;
		if (sampleSize < 0) {
			return null;
		}

		final int wholeSampleSize = (int) sampleSize;

		final ImmutableList.Builder<Path> testPathsBuilder = ImmutableList.builder();
		for (int i = 0; i < wholeSampleSize; i++) {
			final int removedIndex = random.nextInt(filesCopy.size());

			testPathsBuilder.add(filesCopy.remove(removedIndex));
		}
		final List<Path> testPaths = testPathsBuilder.build();
		return testPaths;
	}
	
	private static List<List<Path>> getFolds(final Random random, final int foldsCount, final int foldSize, final List<Path> files) {
		final List<Path> filesCopy = new ArrayList<>(files);
		
		final List<List<Path>> folds = new ArrayList<>(foldsCount);
		for (int fold = 0; fold < foldsCount - 1; fold++) {
			final ImmutableList.Builder<Path> foldPathsBuilder = ImmutableList.builder();
			for (int i = 0; i < foldSize; i++) {
				final int removedIndex = random.nextInt(filesCopy.size());

				foldPathsBuilder.add(filesCopy.remove(removedIndex));
			}
			final List<Path> foldPaths = foldPathsBuilder.build();
			
			folds.add(foldPaths);
		}
		folds.add(ImmutableList.copyOf(filesCopy));
		
		return folds;
	}

	private static List<Path> getFiles(final Path setPath, final Path declaredPropertiesPath,
			final boolean onlyWithProperties) throws IOException {
		final List<Path> paths = Streams.stream(Files.newDirectoryStream(setPath))
				.filter(path -> path.toFile().isFile() && ((!onlyWithProperties)
						|| getPropertiesPath(declaredPropertiesPath, path.getFileName().toString()) != null))
				.collect(Collectors.toCollection(ArrayList::new));

		Collections.sort(paths);
		return ImmutableList.copyOf(paths);
	}

	private BackgroundKnowledgeGraph learn(final CsvWriter csvWriter, final Collection<? extends Path> paths,
			final Path cleanedInputFilesDirectory, final Path profilesDirectory, final Path declaredPropertiesPath,
			final Path collectionResultsDirectory, final boolean onlyWithProperties,
			final boolean onlyDeclaredAsContext, final int repetition, final Random random,
			final int maxColumnSampleSize, final Set<? extends URI> whitelistedProperties) throws IOException {
		final BackgroundKnowledgeGraph graph = new BackgroundKnowledgeGraph(this.uuidGenerator.generate(), propertyTreesMergingStrategy);

		paths.forEach(file -> {
			final Set<PropertyTree> trees = learnFile(csvWriter, file, cleanedInputFilesDirectory, profilesDirectory,
					declaredPropertiesPath, collectionResultsDirectory, onlyWithProperties, onlyDeclaredAsContext,
					repetition, random, maxColumnSampleSize, whitelistedProperties);

			graph.addPropertyTrees(trees);
		});

		return graph;
	}

	private void test(final CsvWriter csvWriter, final Collection<? extends Path> paths,
			final BackgroundKnowledgeGraph graph, final Path cleanedInputFilesDirectory, final Path profilesDirectory,
			final Path declaredPropertiesPath, final Path collectionResultsDirectory, final boolean onlyWithProperties,
			final boolean onlyDeclaredAsContext, final int repetition, final Random random, int maxColumnSampleSize)
			throws IOException {
		paths.forEach(file -> {
			csvWriter.writeRow("File:", file);

			final Map<Integer, DeclaredEntity> solution = getSolution(file, declaredPropertiesPath);

			final AnnotationResult result;
			try {
				result = annotateTable(csvWriter, file, graph, cleanedInputFilesDirectory, profilesDirectory,
						declaredPropertiesPath, collectionResultsDirectory, onlyWithProperties, onlyDeclaredAsContext,
						repetition, random, maxColumnSampleSize);
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

				final DeclaredEntity columnSolution = solution.get(index);
				final List<Property> annotatedProperties = annotation.getProperties();

				csvWriter.writeRow("Properties:");
				csvWriter.writeRow("Mean distance", "Median distance", "Occurence", "Relative occurence", "URI",
						"Context properties");
				csvWriter.writeRows(annotation.getProperties().stream().map(property -> {
					final Statistics statistics = propertiesStatistics.get(property);

					return new Object[] { statistics.getAverage(), statistics.getMedian(), statistics.getOccurence(),
							statistics.getRelativeOccurence(), property.getUri(), getContextProperties(property) };
				}).collect(ImmutableList.toImmutableList()));

				csvWriter.writeRow("Solution:", columnSolution == null ? null : columnSolution.getUri());
				csvWriter.writeRow("Solution matched:",
						annotation.getProperties().stream().map(property -> property.getUri())
								.anyMatch(uri -> isAcceptableFor(uri, columnSolution.getUri())));
				csvWriter.writeRow("Solution matched in an instance:",
						annotation.getProperties().stream().flatMap(property -> property.getInstances().stream())
								.map(instance -> instance.getContext().getDeclaredProperty())
								.filter(declaredProperty -> declaredProperty != null)
								.map(declaredProperty -> declaredProperty.getUri())
								.anyMatch(uri -> isAcceptableFor(uri, columnSolution.getUri())));
				csvWriter.writeRow("Solution available:",
						columnSolution != null && columnSolution.getUri() != null
								&& testStatisticsBuilder.getUniquePropertiesLearnt(repetition).stream()
										.anyMatch(uri -> isAcceptableFor(uri, columnSolution.getUri())));

				if (columnSolution == null) {
					testStatisticsBuilder.addMissingSolution();
				} else {
					if (annotation.getProperties().stream().map(property -> property.getUri())
							.anyMatch(uri -> isAcceptableFor(uri, columnSolution.getUri()))) {
						testStatisticsBuilder.addMatchingSolution();
					} else {
						testStatisticsBuilder.addNonmatchingSolution(repetition, columnSolution.getUri());
					}

					if (testStatisticsBuilder.getUniquePropertiesLearnt(repetition).contains(columnSolution.getUri())) {
						testStatisticsBuilder.addPropertyOccurence(repetition, columnSolution.getUri());

						final Optional<URI> match = annotatedProperties.stream().map(property -> property.getUri())
								.filter(uri -> isAcceptableFor(uri, columnSolution.getUri())).findFirst();
						if (match.isPresent()) {
							testStatisticsBuilder.addTrue(repetition, columnSolution.getUri());
						} else {
							testStatisticsBuilder.addPropertyOccurence(repetition, annotatedProperties.get(0).getUri());
							
							testStatisticsBuilder.addFalse(repetition, annotatedProperties.get(0).getUri(),
									columnSolution.getUri());
						}
					} else {
						testStatisticsBuilder.addInLearningMissingColumn();
					}

					if (annotation.getProperties().stream().flatMap(property -> property.getInstances().stream())
							.map(instance -> instance.getContext().getDeclaredProperty())
							.filter(declaredProperty -> declaredProperty != null)
							.map(declaredProperty -> declaredProperty.getUri())
							.anyMatch(uri -> isAcceptableFor(uri, columnSolution.getUri()))) {
						testStatisticsBuilder.addInstanceMatchingSolution();
					} else {
						testStatisticsBuilder.addInstanceNonmatchingSolution(repetition, columnSolution.getUri());
					}
				}

				testStatisticsBuilder.addAnnotatedNumericColumn();
			});

			testStatisticsBuilder.addTestedFile();

			csvWriter.writeEmptyRow();
			csvWriter.writeEmptyRow();
			csvWriter.writeEmptyRow();
		});
	}

	private static Set<URI> getContextProperties(final Property property) {
		return property.getInstances().stream().map(i -> i.getContext().getDeclaredContextColumnProperties().values())
				.flatMap(e -> e.stream()).map(e -> e.getUri()).collect(ImmutableSet.toImmutableSet());
	}

	private boolean isAcceptableFor(final URI first, final URI second) {
		if (first == null || second == null) {
			return false;
		}

		return first.equals(second)
				|| (GROUP_DEPENDENT && IS_ACCEPTABLE_FOR_PAIRS.get(first) != null && IS_ACCEPTABLE_FOR_PAIRS.get(first).contains(second));
	}

	private static Map<Integer, DeclaredEntity> getSolution(final Path input, final Path declaredPropertiesPath) {
		return getDeclaredProperties(declaredPropertiesPath, input.getFileName().toString());
	}

	private Set<PropertyTree> learnFile(final CsvWriter csvWriter, final Path input,
			final Path cleanedInputFilesDirectory, final Path profilesDirectory, final Path declaredPropertiesPath,
			final Path collectionResultsDirectory, final boolean onlyWithProperties,
			final boolean onlyDeclaredAsContext, final int repetition, final Random random,
			final int maxColumnSampleSize, final Set<? extends URI> whitelistedProperties) {
		csvWriter.writeRow("Processing file:", input);

		final Path cleanedInput = clean(csvWriter, input, cleanedInputFilesDirectory);

		final CsvProfile csvProfile = profile(csvWriter, input, profilesDirectory, cleanedInput);

		final Format format = getFormat(csvProfile);

		/* Parse the input file to table. */

		final ParsedTable parsedTable = parse(input, cleanedInput, format);
		if (parsedTable.getHeight() < 2) {
			csvWriter.writeRow("Too few rows in " + input + ". Skipping.");

			testStatisticsBuilder.addFewRowsFile();

			return ImmutableSet.of();
		}

		/* Assign data types to each table cell. */
		final Map<Integer, Type> hints = getHints(csvProfile);

		final TypedTable typedTable = tableAnalyzer.infer(parsedTable, Locale.forLanguageTag("de-at"), hints);
		if (typedTable.getHeight() < 2) {
			csvWriter.writeRow("Too few typed rows in " + input + ". Skipping.");

			testStatisticsBuilder.addFewTypedRowsFile();

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
				onlyWithProperties, onlyDeclaredAsContext, repetition, random, maxColumnSampleSize,
				whitelistedProperties);

		testStatisticsBuilder.addLearntFile();

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
			final boolean onlyDeclaredAsContext, final int repetition, final Random random,
			final int maxColumnSampleSize, final Set<? extends URI> whitelistedProperties) {
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

			final List<Value> values = createColumnSample(random, numericColumn, maxColumnSampleSize);

			final Partition partition = new Partition(values.stream().filter(e -> e.isNumberLike())
					.map(e -> (NumberLikeValue) e).collect(ImmutableList.toImmutableList()));
			if (partition.size() < MINIMUM_PARTITION_SIZE) {
				testStatisticsBuilder.addTooSmallLearningNumericColumn();

				continue;
			}

			final Set<CommonNode> children = buildChildren(partition, availableContextColumnIndices, slicedTable,
					MINIMUM_PARTITION_RELATIVE_SIZE, MAXIMUM_PARTITION_RELATIVE_SIZE, MINIMUM_PARTITION_SIZE);

			final RootNode rootNode = new RootNode(label, ImmutableMultiset.copyOf(partition.getValues()));
			rootNode.addChildren(children);

			final DeclaredEntity declaredProperty = declaredProperties.get(columnIndex);
			if (declaredProperty == null) {
				testStatisticsBuilder.addNoPropertyLearningNumericColumn();

				if (onlyWithProperties) {
					continue;
				}
			} else {
				if (onlyWithProperties) {
					if (!whitelistedProperties.contains(declaredProperty.getUri())) {
						testStatisticsBuilder.addInTestMissingColumn();

						continue;
					}
				}

				testStatisticsBuilder.addUniqueProperty(repetition, declaredProperty.getUri());
				testStatisticsBuilder.addUniquePropertyLearnt(repetition, declaredProperty.getUri());
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

			testStatisticsBuilder.addLearntNumericColumn();
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
			final Path declaredPropertiesPath, final Path collectionResultsDirectory, final boolean onlyWithProperties,
			final boolean onlyDeclaredAsContext, final int repetition, final Random random, int maxColumnSampleSize) {
		final Path cleanedInput = clean(csvWriter, input, cleanedInputFilesDirectory);

		final CsvProfile csvProfile = profile(csvWriter, input, profilesDirectory, cleanedInput);

		final Format format = getFormat(csvProfile);

		/* Parse the input file to table. */
		final ParsedTable parsedTable = parse(input, cleanedInput, format);
		if (parsedTable.getHeight() < 2) {
			testStatisticsBuilder.addFewRowsFile();

			throw new IllegalArgumentException("Too few rows in " + input + ". Skipping.");
		}

		/* Assign data types to each table cell. */
		final Map<Integer, Type> hints = getHints(csvProfile);

		final TypedTable typedTable = tableAnalyzer.infer(parsedTable, Locale.forLanguageTag("de-at"), hints);
		if (typedTable.getHeight() < 2) {
			testStatisticsBuilder.addFewTypedRowsFile();

			throw new IllegalArgumentException("The table to annotate must have at least two rows!");
		}

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, typedTable,
				hints);

		final Map<Integer, DeclaredEntity> declaredPropertyUris = getDeclaredProperties(declaredPropertiesPath,
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

		return new AnnotationResult(parsedTable, annotate(graph, slicedTable, declaredPropertyUris, contextProperties,
				contextClasses, onlyWithProperties, onlyDeclaredAsContext, repetition, random, maxColumnSampleSize));
	}

	private List<URI> getTestProperties(final CsvWriter csvWriter, final Path input,
			final Path cleanedInputFilesDirectory, final Path profilesDirectory, final Path declaredPropertiesPath,
			final boolean onlyWithProperties) {
		final Path cleanedInput = clean(csvWriter, input, cleanedInputFilesDirectory);

		final CsvProfile csvProfile = profile(csvWriter, input, profilesDirectory, cleanedInput);

		final Format format = getFormat(csvProfile);

		/* Parse the input file to table. */
		final ParsedTable parsedTable = parse(input, cleanedInput, format);
		if (parsedTable.getHeight() < 2) {
			testStatisticsBuilder.addFewRowsFile();

			throw new IllegalArgumentException("Too few rows in " + input + ". Skipping.");
		}

		/* Assign data types to each table cell. */
		final Map<Integer, Type> hints = getHints(csvProfile);

		final TypedTable typedTable = tableAnalyzer.infer(parsedTable, Locale.forLanguageTag("de-at"), hints);
		if (typedTable.getHeight() < 2) {
			testStatisticsBuilder.addFewTypedRowsFile();

			throw new IllegalArgumentException("The table to annotate must have at least two rows!");
		}

		/* Determine the column types. */
		final SlicedTable slicedTable = tableSlicer.slice(RELATIVE_COLUMN_TYPE_VALUES_OCCURENCE_THRESHOLD, typedTable,
				hints);

		final Map<Integer, DeclaredEntity> declaredPropertyUris = getDeclaredProperties(declaredPropertiesPath,
				input.getFileName().toString());

		final Set<Integer> numericIndices = slicedTable.getDataColumns().keySet();

		return declaredPropertyUris.entrySet().stream().filter(e -> numericIndices.contains(e.getKey())).filter(e -> {
			final int index = e.getKey();
			final List<Value> values = slicedTable.getColumn(index);

			final Partition partition = new Partition(values.stream().filter(v -> v.isNumberLike())
					.map(v -> (NumberLikeValue) v).collect(ImmutableList.toImmutableList()));
			return partition.size() >= MINIMUM_PARTITION_SIZE;
		}).map(e -> e.getValue().getUri()).filter(uri -> uri != null).collect(ImmutableList.toImmutableList());
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

	private Set<URI> getTestProperties(final CsvWriter csvWriter, final Collection<? extends Path> paths,
			final Path cleanedInputFilesDirectory, final Path profilesDirectory, final Path declaredPropertiesPath,
			final boolean onlyWithProperties) throws IOException {
		final ImmutableList.Builder<URI> resultBuilder = ImmutableList.builder();
		paths.forEach(file -> {
			csvWriter.writeRow("File:", file);

			try {
				resultBuilder.addAll(getTestProperties(csvWriter, file, cleanedInputFilesDirectory, profilesDirectory,
						declaredPropertiesPath, onlyWithProperties));
			} catch (final IllegalArgumentException e) {
				csvWriter.writeRow("Error:", e.getMessage());
			}
		});

		return resultBuilder.build().stream().distinct().collect(ImmutableSet.toImmutableSet());
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
			final Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			final Map<? extends Integer, ? extends DeclaredEntity> contextProperties,
			final Map<? extends Integer, ? extends DeclaredEntity> contextClasses, final boolean onlyWithProperties,
			final boolean onlyDeclaredAsContext, final int repetition, final Random random,
			final int maxColumnSampleSize) {
		final ImmutableMap.Builder<Integer, Annotation> builder = ImmutableMap.builder();

		testStatisticsBuilder.addTestingColumnsCount(slicedTable.getWidth());

		final Set<Integer> availableContextColumnIndices = slicedTable.getContextColumns().keySet();

		testStatisticsBuilder.addTestedContextColumnsCount(availableContextColumnIndices.size());

		for (final Entry<Integer, List<Value>> numericColumn : slicedTable.getDataColumns().entrySet()) {
			testStatisticsBuilder.addAttemptedTestedNumericColumn();

			final int columnIndex = numericColumn.getKey();
			final Label label = slicedTable.getHeaders().get(columnIndex);

			final List<Value> values = createColumnSample(random, numericColumn, maxColumnSampleSize);

			final Partition partition = new Partition(values.stream().filter(e -> e.isNumberLike())
					.map(e -> (NumberLikeValue) e).collect(ImmutableList.toImmutableList()));
			if (partition.size() < MINIMUM_PARTITION_SIZE) {
				testStatisticsBuilder.addTooSmallTestingNumericColumn();

				continue;
			}

			final Set<CommonNode> children = buildChildren(partition, availableContextColumnIndices, slicedTable,
					MINIMUM_PARTITION_RELATIVE_SIZE, MAXIMUM_PARTITION_RELATIVE_SIZE, MINIMUM_PARTITION_SIZE);

			final RootNode rootNode = new RootNode(label, ImmutableMultiset.copyOf(partition.getValues()));
			rootNode.addChildren(children);

			final DeclaredEntity declaredProperty = declaredProperties.get(columnIndex);
			if (declaredProperty == null) {
				testStatisticsBuilder.addNoPropertyTestingNumericColumn();

				if (onlyWithProperties) {
					continue;
				}
			} else {
				testStatisticsBuilder.addUniqueProperty(repetition, declaredProperty.getUri());
				testStatisticsBuilder.addUniquePropertyTested(repetition, declaredProperty.getUri());
			}

			final Context context = new Context(slicedTable.getHeaders(), slicedTable.getMetadata().getAuthor(),
					slicedTable.getMetadata().getTitle(), declaredProperty,
					onlyDeclaredAsContext ? getMeaningfulEntities(declaredProperties)
							: getMeaningfulEntities(contextProperties),
					onlyDeclaredAsContext ? ImmutableMap.of() : contextClasses, columnIndex,
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
