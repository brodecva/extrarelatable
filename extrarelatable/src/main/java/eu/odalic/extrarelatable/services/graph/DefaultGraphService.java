package eu.odalic.extrarelatable.services.graph;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import eu.odalic.extrarelatable.algorithms.graph.Annotator;
import eu.odalic.extrarelatable.algorithms.graph.PropertyTreesBuilder;
import eu.odalic.extrarelatable.algorithms.table.TableAnalyzer;
import eu.odalic.extrarelatable.algorithms.table.TableSlicer;
import eu.odalic.extrarelatable.algorithms.table.csv.CsvTableParser;
import eu.odalic.extrarelatable.algorithms.table.csv.CsvTableWriter;
import eu.odalic.extrarelatable.model.annotation.AnnotationResult;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.Type;
import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.graph.PropertyTreesMergingStrategy;
import eu.odalic.extrarelatable.model.graph.SearchResult;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.NestedListsParsedTable;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;
import eu.odalic.extrarelatable.model.table.csv.Format;
import eu.odalic.extrarelatable.services.csvengine.csvclean.CsvCleanService;
import eu.odalic.extrarelatable.services.csvengine.csvprofiler.CsvProfile;
import eu.odalic.extrarelatable.services.csvengine.csvprofiler.CsvProfilerService;
import eu.odalic.extrarelatable.services.dwtc.DwtcToCsvService;
import eu.odalic.extrarelatable.services.odalic.ContextCollectionService;
import eu.odalic.extrarelatable.services.odalic.values.ColumnRelationAnnotationValue;
import eu.odalic.extrarelatable.services.odalic.values.EntityCandidateValue;
import eu.odalic.extrarelatable.services.odalic.values.EntityValue;
import eu.odalic.extrarelatable.services.odalic.values.HeaderAnnotationValue;
import eu.odalic.extrarelatable.services.odalic.values.ResultValue;
import eu.odalic.extrarelatable.services.persistence.FileCachingService;
import eu.odalic.extrarelatable.services.persistence.GraphsPersitingService;
import eu.odalic.extrarelatable.util.Lists;
import eu.odalic.extrarelatable.util.Matrix;
import webreduce.data.Dataset;
import webreduce.data.HeaderPosition;

/**
 * Default implementation of {@link GraphService}.
 * 
 * @author Václav Brodec
 *
 */
@Service
public class DefaultGraphService implements GraphService {

	/**
	 * Suffix that is assigned to cached failed attempts to either profile or clean the input CSV file. 
	 */
	private static final String DOT_LEADING_FAILED_RESULT_FILE_SUFFIX = ".fail";
	/**
	 * JSON suffix
	 */
	private static final String DOT_LEADING_JSON_SUFFIX = ".json";
	/**
	 * CSV file suffix
	 */
	private static final String DOT_LEADING_CSV_SUFFIX = ".csv";
	/**
	 * Characters that have to be sanitized in order to obtain valid file name (e.g. for declared properties of the DWTC format tables).
	 */
	private static final String CHARACTERS_TO_SANITIZE_REGEX = "[.]";	
	/**
	 * Sanitizing string used to replace the sanitized characters.
	 */
	private static final String SANITIZE_WITH = "_";

	/**
	 * Pattern to cut out locale language to from the graph name.
	 */
	private static final Pattern LOCALE_LANGUAGE_TAG_IN_GRAPH_NAME_PATTERN = Pattern.compile("^.*__([^_]+)$");
	/**
	 * Default locale.
	 */
	private static final Locale DEFAULT_LOCALE = Locale.getDefault();

	/**
	 * JSON-encoded tables sub-path in DWTC format.
	 */
	private static final Path TABLES_SUBPATH = Paths.get("tables");
	/**
	 * Declared properties sub-path in DWTC format.
	 */
	private static final Path DECLARED_PROPERTIES_SUBPATH = Paths.get("property");
	/**
	 * Parsed tables sub-path of tables in modified DWTC format.
	 */
	private static final Path PARSED_TABLES_CSVS_SUBPATH = Paths.get("csvs");
	/**
	 * Profiles sub-path in both modified DWTC (tables sub-paths) and raw tables format (root sub-path).
	 */
	private static final Path PROFILES_SUBPATH = Paths.get("profiles");
	/**
	 * Cleaned files sub-path in both modified DWTC (tables sub-path) and raw tables format (root sub-path). 
	 */
	private static final Path CLEANED_CSVS_SUBPATH = Paths.get("cleaned");
	/**
	 * Collected context sub-path in both modified DWTC (tables sub-path) and raw tables format (root sub-path).
	 */
	private static final Path CONTEXT_COLLECTION_RESULTS_SUBPATH = Paths.get("context");

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGraphService.class);
	
	/**
	 * Name of the file within context directory which holds the configuration of context-collecting bases (used ones separated by {@link #USED_BASES_CONTEXT_BASES_CONFIGURATION_DELIMITER}} and the primary one in the second row)
	 */
	private static final String CONTEXT_BASES_CONFIGURATION_FILE_NAME = "odalic.conf";
	/**
	 * Separator of used bases on the first rows in the {@value #CONTEXT_BASES_CONFIGURATION_FILE_NAME} configuration file. 
	 */
	private static final char USED_BASES_CONTEXT_BASES_CONFIGURATION_DELIMITER = ';';
	/**
	 * First row index, holding the used bases in the {@value #CONTEXT_BASES_CONFIGURATION_FILE_NAME} file.
	 */
	private static final int USED_BASES_CONTEXT_BASES_CONFIGURATION_INDEX = 0;
	/**
	 * Second row index, holding the primary base in the {@value #CONTEXT_BASES_CONFIGURATION_FILE_NAME} file.
	 */private static final int PRIMARY_BASE_CONTEXT_BASES_CONFIGURATION_INDEX = 1;

	private final PropertyTreesMergingStrategy propertyTreesMergingStrategy;
	private final FileCachingService fileCachingService;
	private final CsvProfilerService csvProfilerService;
	private final CsvCleanService csvCleanerService;
	private final CsvTableParser csvTableParser;
	private final TableAnalyzer tableAnalyzer;
	private final PropertyTreesBuilder propertyTreesBuilder;
	private final TableSlicer tableSlicer;
	private final Annotator annotator;
	private final CsvTableWriter csvTableWriter;
	private final DwtcToCsvService dwtcToCsvService;
	private final GraphsPersitingService graphsPersistingService;
	private final ContextCollectionService contextCollectionService;

	/**
	 * Background knowledge graphs mapped to their names.
	 */
	private final Map<String, BackgroundKnowledgeGraph> graphs;
	
	/**
	 * Random generator used to sample the input tables.
	 */
	private Random random;

	DefaultGraphService(PropertyTreesMergingStrategy propertyTreesMergingStrategy,
			FileCachingService fileCachingService, CsvProfilerService csvProfilerService,
			CsvCleanService csvCleanerService, CsvTableParser csvTableParser, TableAnalyzer tableAnalyzer,
			PropertyTreesBuilder propertyTreesBuilder, TableSlicer tableSlicer, Annotator annotator,
			CsvTableWriter csvTableWriter, DwtcToCsvService dwtcToCsvService,
			GraphsPersitingService graphsPersistingService, ContextCollectionService contextCollectionService,
			Map<String, BackgroundKnowledgeGraph> graphs) {
		checkNotNull(propertyTreesMergingStrategy);
		checkNotNull(fileCachingService);
		checkNotNull(csvProfilerService);
		checkNotNull(csvCleanerService);
		checkNotNull(csvTableParser);
		checkNotNull(tableAnalyzer);
		checkNotNull(propertyTreesBuilder);
		checkNotNull(tableSlicer);
		checkNotNull(annotator);
		checkNotNull(csvTableWriter);
		checkNotNull(dwtcToCsvService);
		checkNotNull(graphsPersistingService);
		checkNotNull(contextCollectionService);
		checkNotNull(graphs);

		graphs.entrySet().stream().forEach(e -> {
			checkNotNull(e.getKey());
			checkNotNull(e.getValue());
		});

		this.propertyTreesMergingStrategy = propertyTreesMergingStrategy;
		this.fileCachingService = fileCachingService;
		this.csvProfilerService = csvProfilerService;
		this.csvCleanerService = csvCleanerService;
		this.csvTableParser = csvTableParser;
		this.tableAnalyzer = tableAnalyzer;
		this.propertyTreesBuilder = propertyTreesBuilder;
		this.tableSlicer = tableSlicer;
		this.annotator = annotator;
		this.csvTableWriter = csvTableWriter;
		this.dwtcToCsvService = dwtcToCsvService;
		this.graphsPersistingService = graphsPersistingService;
		this.contextCollectionService = contextCollectionService;
		this.graphs = graphs;
	}

	/**
	 * Initializes the graph service.
	 * 
	 * @param propertyTreesMergingStrategy merges the property trees of the background knowledge graph
	 * @param fileCachingService caches immediate results and result from remote services
	 * @param csvProfilerService profiles the CSV files (mainly for deduction of used data types)
	 * @param csvCleanerService cleans provided CSV files and converts them to uniform format
	 * @param csvTableParser parses CSV tables
	 * @param tableAnalyzer
	 * @param propertyTreesBuilder
	 * @param tableSlicer 
	 * @param annotator the actual annotating service of the numeric columns
	 * @param csvTableWriter CSV writer used to cache the tables obtained in other formats (such as DWTC)
	 * @param dwtcToCsvService converts tables from the DWTC format to CSV
	 * @param graphsPersitingService stores the graphs in persistent storage between application runs
	 * @param contextCollectionService collects additional class and property context for the ERT algorithm
	 * @param graphsPath path to directory containing the graphs to learn (one graph per sub-directory) 
	 * @param onlyWithProperties determines whether only the numeric columns with existing declared property are learned from referential files
	 * @throws IOException whenever I/O exception occurs
	 */
	@Autowired
	public DefaultGraphService(
			@Qualifier("PropertyTreesMergingStrategy") PropertyTreesMergingStrategy propertyTreesMergingStrategy,
			FileCachingService fileCachingService, CsvProfilerService csvProfilerService,
			CsvCleanService csvCleanerService, @Qualifier("CsvTableParser") CsvTableParser csvTableParser,
			TableAnalyzer tableAnalyzer, PropertyTreesBuilder propertyTreesBuilder, TableSlicer tableSlicer,
			Annotator annotator, CsvTableWriter csvTableWriter, DwtcToCsvService dwtcToCsvService,
			final GraphsPersitingService graphsPersitingService,
			final ContextCollectionService contextCollectionService,
			final @Nullable @Value("${eu.odalic.extrarelatable.graphsPath}") String graphsPath,
			final @Value("${eu.odalic.extrarelatable.onlyWithProperties:false}") boolean onlyWithProperties)
			throws IOException {
		this(propertyTreesMergingStrategy, fileCachingService, csvProfilerService, csvCleanerService, csvTableParser,
				tableAnalyzer, propertyTreesBuilder, tableSlicer, annotator, csvTableWriter, dwtcToCsvService,
				graphsPersitingService, contextCollectionService, new HashMap<>(graphsPersitingService.load()));

		initializeGraphs(graphsPath, onlyWithProperties);
	}

	private void initializeGraphs(final String graphsPath, final boolean onlyWithProperties) throws IOException {
		final Set<BackgroundKnowledgeGraph> loadedGraphs = loadGraphs(Paths.get(graphsPath), onlyWithProperties);
		
		loadedGraphs.forEach(graph -> this.graphsPersistingService.persist(graph.getName(), graph));
		
		if (graphsPath != null) {
			this.graphs.putAll(loadedGraphs.stream()
					.collect(ImmutableMap.toImmutableMap(graph -> graph.getName(), graph -> graph)));
		}
	}

	private Set<BackgroundKnowledgeGraph> loadGraphs(final Path graphsPath, boolean onlyWithProperties)
			throws IOException {
		return Streams.stream(Files.newDirectoryStream(graphsPath)).sorted().filter(path -> {
			if (!path.toFile().isDirectory()) {
				LOGGER.warn(path + " is not a directory.");

				return false;
			} else {
				return true;
			}
		}).map(path -> {
			try {
				return loadGraph(path, onlyWithProperties);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}).filter(graph -> graph != null).collect(ImmutableSet.toImmutableSet());
	}

	private BackgroundKnowledgeGraph loadGraph(final Path graphPath, boolean onlyWithProperties) throws IOException {
		checkArgument(graphPath.toFile().exists(), "The " + graphPath + " does not exist!");
		checkArgument(graphPath.toFile().isDirectory(), "The " + graphPath + " is not a directory!");

		final String graphName = graphPath.getFileName().toString();
		if (this.graphs.containsKey(graphName)) {
			LOGGER.warn("Graph " + graphName + " already loaded. Skipping...");
			return null;
		}

		final Locale locale = tryGetLocaleFromGraphName(graphName);

		final Path tablesPath = graphPath.resolve(TABLES_SUBPATH);

		// Supported referential files format detection
		if (tablesPath.toFile().exists() && tablesPath.toFile().isDirectory()) {
			return loadDwtcGraph(graphName, tablesPath, tablesPath.resolve(PROFILES_SUBPATH),
					tablesPath.resolve(PARSED_TABLES_CSVS_SUBPATH), graphPath.resolve(DECLARED_PROPERTIES_SUBPATH),
					graphPath.resolve(CONTEXT_COLLECTION_RESULTS_SUBPATH),
					onlyWithProperties, locale);
		} else {
			return loadBagOfTablesGraph(graphName, graphPath, graphPath.resolve(PROFILES_SUBPATH),
					graphPath.resolve(CLEANED_CSVS_SUBPATH), graphPath.resolve(DECLARED_PROPERTIES_SUBPATH),
					graphPath.resolve(CONTEXT_COLLECTION_RESULTS_SUBPATH), onlyWithProperties, locale);
		}
	}

	private Locale tryGetLocaleFromGraphName(String graphName) {
		final Locale locale = getLocaleFromGraphName(graphName);
		if (locale == null) {
			return DEFAULT_LOCALE;
		}

		return locale;
	}

	private BackgroundKnowledgeGraph loadBagOfTablesGraph(final String graphName, final Path graphPath,
			final Path profilesPath, final Path cleanedCsvsPath, Path declaredPropertiesPath,
			Path collectionResultsDirectory,
			boolean onlyWithProperties, Locale locale) throws IOException {
		checkArgument(graphPath.toFile().exists() && graphPath.toFile().isDirectory(),
				"The directory for " + graphName + " is missing!");
		checkArgument(profilesPath.toFile().exists() && profilesPath.toFile().isDirectory(),
				"The profiles directory for " + graphName + " is missing!");
		checkArgument(cleanedCsvsPath.toFile().exists() && cleanedCsvsPath.toFile().isDirectory(),
				"The cleaned directory for " + graphName + " is missing!");

		final BackgroundKnowledgeGraph graph = new BackgroundKnowledgeGraph(graphName,
				this.propertyTreesMergingStrategy);

		Streams.stream(Files.newDirectoryStream(graphPath)).filter(tablePath -> tablePath.toFile().isFile()).sorted()
				.forEach(tablePath -> graph.addPropertyTrees(learnTable(tablePath, cleanedCsvsPath, profilesPath,
						declaredPropertiesPath, collectionResultsDirectory, onlyWithProperties, locale)));

		return graph;
	}

	private BackgroundKnowledgeGraph loadDwtcGraph(final String graphName, final Path tablesPath,
			final Path profilesPath, final Path csvsPath, final Path declaredPropertiesPath, final Path collectionResultsDirectory, boolean onlyWithProperties,
			Locale locale) throws IOException {
		checkArgument(tablesPath.toFile().exists() && tablesPath.toFile().isDirectory(),
				"The directory for " + graphName + " is missing!");
		checkArgument(profilesPath.toFile().exists() && profilesPath.toFile().isDirectory(),
				"The profiles directory for " + graphName + " is missing!");
		checkArgument(csvsPath.toFile().exists() && csvsPath.toFile().isDirectory(),
				"The csvs directory for " + graphName + " is missing!");
		checkArgument(declaredPropertiesPath.toFile().exists() && declaredPropertiesPath.toFile().isDirectory(),
				"The property directory for " + graphName + " is missing!");

		final BackgroundKnowledgeGraph graph = new BackgroundKnowledgeGraph(graphName,
				this.propertyTreesMergingStrategy);

		Streams.stream(Files.newDirectoryStream(tablesPath)).filter(tablePath -> tablePath.toFile().isFile()).sorted()
				.forEach(tablePath -> graph.addPropertyTrees(learnDwtcTable(tablePath, csvsPath, profilesPath,
						declaredPropertiesPath, collectionResultsDirectory, onlyWithProperties, locale)));

		return graph;
	}

	private Set<PropertyTree> learnTable(Path tablePath, Path cleanedCsvsPath, Path profilesPath,
			Path declaredPropertiesPath, Path collectionResultsDirectory, boolean onlyWithProperties, final Locale locale) {
		final Path cleanedInput = getCleanedInput(tablePath, cleanedCsvsPath);

		final CsvProfile csvProfile = getProfile(cleanedInput, profilesPath, false);

		final Format format = getFormat(csvProfile);

		final Map<Integer, DeclaredEntity> declaredProperties = getDeclaredTableProperties(declaredPropertiesPath,
				tablePath.getFileName().toString());

		final ParsedTable table = parse(tablePath, cleanedInput, format, declaredProperties, locale);
		if (table.getHeight() < 2) {
			LOGGER.warn("Too few rows in " + tablePath + ". Skipping.");
			return ImmutableSet.of();
		}
		
		final Map<Integer, DeclaredEntity> contextProperties;
		final Map<Integer, DeclaredEntity> contextClasses;
		final boolean onlyDeclaredAsContext;
		
		final Path basesConfigurationPath = collectionResultsDirectory.resolve(CONTEXT_BASES_CONFIGURATION_FILE_NAME);
		if (basesConfigurationPath.toFile().exists() && basesConfigurationPath.toFile().isFile()) {
			onlyDeclaredAsContext = false;
			
			List<String> basesConfigurationEntries;
			try {
				basesConfigurationEntries = Files.readAllLines(basesConfigurationPath);
			} catch (IOException e) {
				throw new RuntimeException("Failed to read context collection bases configuration!", e);
			}
			final Set<String> usedBases = ImmutableSet.copyOf(Splitter.on(USED_BASES_CONTEXT_BASES_CONFIGURATION_DELIMITER).split(basesConfigurationEntries.get(USED_BASES_CONTEXT_BASES_CONFIGURATION_INDEX)));
			final String primaryBase = basesConfigurationEntries.get(PRIMARY_BASE_CONTEXT_BASES_CONFIGURATION_INDEX);
				
			final ResultValue collectedContext = geCollectedTableContext(table, tablePath,
					collectionResultsDirectory, this.random, usedBases, primaryBase);
			if (collectedContext == null) {
				contextProperties = ImmutableMap.of();
				contextClasses = ImmutableMap.of();
			} else {
				contextProperties = getContextProperties(collectedContext, primaryBase);
				contextClasses = getContextClasses(collectedContext, primaryBase);
			}
		} else {
			onlyDeclaredAsContext = true;
			
			contextProperties = ImmutableMap.of();
			contextClasses = ImmutableMap.of();
		}

		final Map<Integer, Type> typeHints = tryGetHints(csvProfile);

		final TypedTable typedTable = tableAnalyzer.infer(table, locale, typeHints);
		if (typedTable.getHeight() < 2) {
			LOGGER.warn("Too few typed rows in " + tablePath + ". Skipping.");
			return ImmutableSet.of();
		}

		final SlicedTable slicedTable = tableSlicer.slice(typedTable, typeHints);

		return this.propertyTreesBuilder.build(slicedTable, declaredProperties, ImmutableMap.of(), contextProperties, contextClasses, onlyWithProperties, onlyDeclaredAsContext);
	}

	private Locale getLocaleFromGraphName(final String graphName) {
		final Matcher matcher = LOCALE_LANGUAGE_TAG_IN_GRAPH_NAME_PATTERN.matcher(graphName);
		if (!matcher.find()) {
			return null;
		}

		final String languageTag = matcher.group(1);

		return Locale.forLanguageTag(languageTag);
	}

	private Path getCleanedInput(final Path input, final Path cleanedInputFilesDirectory) {
		Path cleanedInput = cleanedInputFilesDirectory.resolve(input.getFileName());
		final Path failedCleanNotice = cleanedInputFilesDirectory
				.resolve(input.getFileName() + DOT_LEADING_FAILED_RESULT_FILE_SUFFIX);
		if (cleanedInput.toFile().exists()) {
			System.out.println("File " + input + " already cleaned.");
		} else if (failedCleanNotice.toFile().exists()) {
			System.out.println("Previously failed cleaning attempt for " + input + ". Using original instead.");

			cleanedInput = input;
		} else {
			try (final InputStream cleanedInputStream = this.csvCleanerService.clean(input.toFile())) {
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

	private static void cacheFailedCleaning(Path cleanedInputFilesDirectory, Path input) throws IOException {
		Files.createFile(
				cleanedInputFilesDirectory.resolve(input.getFileName() + DOT_LEADING_FAILED_RESULT_FILE_SUFFIX));
	}

	private ParsedTable parse(final Path input, final Path cleanedInput, final Format format,
			final Map<Integer, DeclaredEntity> declaredProperties, final Locale locale) {
		final ParsedTable table;
		try (final InputStream inputStream = new BufferedInputStream(Files.newInputStream(cleanedInput))) {
			table = csvTableParser.parse(inputStream, format,
					new Metadata(input.getFileName().toString(), null, locale.toLanguageTag(), declaredProperties,
							ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of()));
		} catch (final IOException e) {
			throw new RuntimeException("Failed to parse " + input + "!", e);
		}
		return table;
	}

	private static Format getFormat(final CsvProfile csvProfile) {
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

	private Set<PropertyTree> learnDwtcTable(Path tablePath, Path csvsPath, Path profilesPath,
			Path declaredPropertiesPath, Path collectionResultsDirectory, boolean onlyWithProperties, Locale locale) {
		final Path convertedInput = convertDwtcToCsv(tablePath, csvsPath);

		final CsvProfile csvProfile = getProfile(convertedInput, profilesPath, true);

		final Dataset dataset = parseDwtc(tablePath);
		final HeaderPosition headerPosition = dataset.getHeaderPosition();
		if (headerPosition != HeaderPosition.FIRST_ROW) {
			LOGGER.warn("File " + tablePath + " has no regular header!");
			return ImmutableSet.of();
		}

		final Map<Integer, DeclaredEntity> declaredProperties = getDeclaredDwtcTableProperties(declaredPropertiesPath,
				tablePath.getFileName().toString());

		final ParsedTable table = toParsedTable(dataset, tablePath.getFileName().toString(), declaredProperties,
				locale);
		if (table.getHeight() < 2) {
			LOGGER.warn("Too few rows in " + tablePath + ". Skipping.");
			return ImmutableSet.of();
		}
		
		final Map<Integer, DeclaredEntity> contextProperties;
		final Map<Integer, DeclaredEntity> contextClasses;
		final boolean onlyDeclaredAsContext;
		
		final Path basesConfigurationPath = collectionResultsDirectory.resolve(CONTEXT_BASES_CONFIGURATION_FILE_NAME);
		if (basesConfigurationPath.toFile().exists() && basesConfigurationPath.toFile().isFile()) {
			onlyDeclaredAsContext = false;
			
			List<String> basesConfigurationEntries;
			try {
				basesConfigurationEntries = Files.readAllLines(basesConfigurationPath);
			} catch (IOException e) {
				throw new RuntimeException("Failed to read context collection bases configuration!", e);
			}
			final Set<String> usedBases = ImmutableSet.copyOf(Splitter.on(USED_BASES_CONTEXT_BASES_CONFIGURATION_DELIMITER).split(basesConfigurationEntries.get(USED_BASES_CONTEXT_BASES_CONFIGURATION_INDEX)));
			final String primaryBase = basesConfigurationEntries.get(PRIMARY_BASE_CONTEXT_BASES_CONFIGURATION_INDEX);
				
			final ResultValue collectedContext = getCollectedDwtcContext(table, tablePath,
					collectionResultsDirectory, this.random, usedBases, primaryBase);
			if (collectedContext == null) {
				contextProperties = ImmutableMap.of();
				contextClasses = ImmutableMap.of();
			} else {
				contextProperties = getContextProperties(collectedContext, primaryBase);
				contextClasses = getContextClasses(collectedContext, primaryBase);
			}
		} else {
			onlyDeclaredAsContext = true;
			
			contextProperties = ImmutableMap.of();
			contextClasses = ImmutableMap.of();
		}

		final Map<Integer, Type> typeHints = tryGetHints(csvProfile);

		final TypedTable typedTable = tableAnalyzer.infer(table, locale, typeHints);
		if (typedTable.getHeight() < 2) {
			LOGGER.warn("Too few typed rows in " + tablePath + ". Skipping.");
			return ImmutableSet.of();
		}

		final SlicedTable slicedTable = this.tableSlicer.slice(typedTable, typeHints);

		return this.propertyTreesBuilder.build(slicedTable, declaredProperties, ImmutableMap.of(), contextProperties, contextClasses, onlyWithProperties, onlyDeclaredAsContext);
	}
	
	private Map<Integer, DeclaredEntity> getContextClasses(final ResultValue collectedContext, final String primaryBase) {
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

					final Set<EntityCandidateValue> baseChosen = chosen.get(primaryBase);
					if (baseChosen == null || baseChosen.isEmpty()) {
						return false;
					}

					return true;
				}).mapToObj(i -> Integer.valueOf(i))
				.collect(Collectors.toMap(i -> i, i -> toDeclared(collectedContext.getHeaderAnnotations().get(i)
						.getChosen().get(primaryBase).stream().findFirst().get().getEntity())));

		return ImmutableMap.copyOf(result);
	}

	private static DeclaredEntity toDeclared(final EntityValue entity) {
		return new DeclaredEntity(URI.create(entity.getResource()), ImmutableSet.of(entity.getLabel()));
	}

	private Map<Integer, DeclaredEntity> getContextProperties(final ResultValue collectedContext, final String primaryBase) {
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

					final Set<EntityCandidateValue> baseChosen = chosen.get(primaryBase);
					if (baseChosen == null || baseChosen.isEmpty()) {
						return false;
					}

					return true;
				})
				.collect(Collectors.toMap(entry -> entry.getKey().getSecond().getIndex(),
						entry -> toDeclared(
								entry.getValue().getChosen().get(primaryBase).stream().findFirst().get().getEntity()),
						(f, s) -> f));

		return ImmutableMap.copyOf(result);
	}

	private static Map<Integer, DeclaredEntity> getDeclaredDwtcTableProperties(final Path declaredPropertiesPath,
			final String tableFileName) {
		final Path propertiesPath = getDwtcTablePropertiesPath(declaredPropertiesPath, tableFileName);

		return getDeclaredProperties(propertiesPath);
	}

	private static Map<Integer, DeclaredEntity> getDeclaredTableProperties(final Path declaredPropertiesPath,
			final String tableFileName) {
		final Path propertiesPath = getTablePropertiesPath(declaredPropertiesPath, tableFileName);

		return getDeclaredProperties(propertiesPath);
	}

	private static Map<Integer, DeclaredEntity> getDeclaredProperties(final Path propertiesPath) {
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

	private static Path getDwtcTablePropertiesPath(final Path declaredPropertiesPath, final String tableFileName) {
		final String fileName = com.google.common.io.Files.getNameWithoutExtension(tableFileName);

		final String sanitizedFileName = fileName.replaceAll(CHARACTERS_TO_SANITIZE_REGEX, SANITIZE_WITH);

		final String propertiesFileName = sanitizedFileName + DOT_LEADING_CSV_SUFFIX;

		final Path propertiesPath = declaredPropertiesPath.resolve(propertiesFileName);
		if (!propertiesPath.toFile().exists()) {
			return null;
		}

		return propertiesPath;
	}

	private static Path getTablePropertiesPath(final Path declaredPropertiesPath, final String tableFileName) {
		final Path propertiesPath = declaredPropertiesPath.resolve(tableFileName);
		if (!propertiesPath.toFile().exists()) {
			return null;
		}

		return propertiesPath;
	}

	private static ParsedTable toParsedTable(final Dataset dataset, final String fileName,
			Map<Integer, DeclaredEntity> declaredProperties, final Locale locale) {
		final ParsedTable table = NestedListsParsedTable.fromColumns(Matrix.fromArray(dataset.getRelation()),
				new Metadata(fileName, dataset.getUrl(), locale.toLanguageTag(), declaredProperties, ImmutableMap.of(),
						ImmutableMap.of(), ImmutableMap.of()));

		return table;
	}

	private Path convertDwtcToCsv(final Path dwtcTablePath, final Path csvsPath) {
		final Path csvTablePath = csvsPath
				.resolve(com.google.common.io.Files.getNameWithoutExtension(dwtcTablePath.getFileName().toString())
						+ DOT_LEADING_CSV_SUFFIX);
		if (csvTablePath.toFile().exists()) {
			LOGGER.info("File " + dwtcTablePath + " already converted.");
			return csvTablePath;
		}

		try {
			this.dwtcToCsvService.convert(dwtcTablePath, csvTablePath);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to convert " + dwtcTablePath + "!", e);
		}

		return csvTablePath;
	}

	private Dataset parseDwtc(final Path input) {
		final Dataset dataset;
		try (final InputStream datasetInputStream = Files.newInputStream(input, StandardOpenOption.READ)) {
			dataset = webreduce.data.Dataset.fromJson(datasetInputStream);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to read " + input + "!", e);
		}
		return dataset;
	}

	private CsvProfile getProfile(final Path input, final Path profilesDirectory, boolean replaceExtension) {
		CsvProfile csvProfile = null;

		final Path profileInput;
		final Path failedProfileNotice;
		if (replaceExtension) {
			final String fileName = com.google.common.io.Files.getNameWithoutExtension(input.getFileName().toString());

			profileInput = profilesDirectory.resolve(fileName + DOT_LEADING_JSON_SUFFIX);
			failedProfileNotice = profilesDirectory.resolve(fileName + DOT_LEADING_FAILED_RESULT_FILE_SUFFIX);
		} else {
			profileInput = profilesDirectory.resolve(input.getFileName());
			failedProfileNotice = profilesDirectory
					.resolve(input.getFileName() + DOT_LEADING_FAILED_RESULT_FILE_SUFFIX);
		}

		if (profileInput.toFile().exists()) {
			try {
				csvProfile = loadProfile(profileInput);
			} catch (IOException e) {
				throw new RuntimeException("Failed to load profile for " + input + "!", e);
			}
		} else if (failedProfileNotice.toFile().exists()) {
			LOGGER.warn("Previously failed profiling attempt for " + input + "!");

			csvProfile = null;
		} else {
			try {
				csvProfile = csvProfilerService.profile(input.toFile());

				try {
					saveProfile(csvProfile, profileInput);
				} catch (final IOException e) {
					throw new RuntimeException("Failed to save profile for " + input + "!", e);
				}
			} catch (final IllegalStateException e) {
				LOGGER.warn("Failed profiling attempt for " + input + "!");

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

	private void saveProfile(final CsvProfile csvProfile, final Path profileInput)
			throws IOException, JsonGenerationException, JsonMappingException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(profileInput.toFile(), csvProfile);
	}

	private static void cacheFailedProfiling(Path profilesDirectory, Path input) throws IOException {
		Files.createFile(profilesDirectory.resolve(input.getFileName() + DOT_LEADING_FAILED_RESULT_FILE_SUFFIX));
	}

	private CsvProfile loadProfile(final Path profileInput)
			throws IOException, JsonParseException, JsonMappingException {
		final CsvProfile csvProfile;
		final ObjectMapper mapper = new ObjectMapper();
		csvProfile = mapper.readValue(profileInput.toFile(), CsvProfile.class);
		return csvProfile;
	}

	private void saveCollectionResult(final ResultValue result, final Path resultOutput)
			throws IOException, JsonGenerationException, JsonMappingException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(resultOutput.toFile(), result);
	}
	
	private ResultValue loadCollectionResult(final Path resultInput)
			throws IOException, JsonParseException, JsonMappingException {
		final ResultValue result;
		final ObjectMapper mapper = new ObjectMapper();
		result = mapper.readValue(resultInput.toFile(), ResultValue.class);
		return result;
	}
	
	@Override
	public void create(final String name) {
		checkNotNull(name);

		final BackgroundKnowledgeGraph graph = new BackgroundKnowledgeGraph(name, this.propertyTreesMergingStrategy);

		this.graphs.put(name, graph);
		this.graphsPersistingService.persist(name, graph);
	}

	@Override
	public boolean exists(final String name) {
		checkNotNull(name);

		return this.graphs.containsKey(name);
	}

	@Override
	public void learn(final String graphName, final InputStream input, final @Nullable Format format,
			final Metadata metadata, final boolean onlyWithProperties, final boolean contextCollected, final boolean onlyDeclaredAsContext,
			final Set<? extends String> usedBases, final String primaryBase) throws IOException {
		checkNotNull(graphName);
		checkNotNull(input);
		checkNotNull(metadata);
		checkNotNull(usedBases);
		usedBases.stream().forEach(e -> {
			checkNotNull(e);
		});
		
		checkArgument((!contextCollected) || primaryBase != null, "To collect the context, specify primary and other used bases!");
		checkArgument(primaryBase == null || usedBases.contains(primaryBase), "To collect the context, specify the used bases!");

		final BackgroundKnowledgeGraph graph = this.graphs.get(graphName);
		checkArgument(graph != null, "Unknown graph!");

		final Path cachedInput = this.fileCachingService.cache(input);

		learn(graph, cachedInput, format, metadata, onlyWithProperties, contextCollected, onlyDeclaredAsContext, usedBases, primaryBase);
		this.graphsPersistingService.persist(graph.getName(), graph);
	}

	private void learn(final BackgroundKnowledgeGraph graph, final Path input, @Nullable Format format,
			Metadata metadata, final boolean onlyWithProperties, final boolean contextCollected, final boolean onlyDeclaredAsContext, final Set<? extends String> usedBases, final String primaryBase) throws IOException {
		CsvProfile csvProfile;
		try {
			csvProfile = this.csvProfilerService.profile(input.toFile());
		} catch (final IllegalStateException e) {
			LOGGER.warn("Profiling has failed!", e);

			csvProfile = null;
		}

		final Format usedFormat = getFormat(csvProfile, format);

		ParsedTable table;
		try (final InputStream cleanedInput = this.csvCleanerService.clean(input.toFile())) {
			table = this.csvTableParser.parse(cleanedInput, usedFormat, metadata);
		} catch (final IllegalStateException e) {
			LOGGER.warn("Cleaning has failed! Using the raw input...", e);

			try (final InputStream inputStream = new FileInputStream(input.toFile())) {
				table = this.csvTableParser.parse(inputStream, usedFormat, metadata);
			}
		}
		checkArgument(table.getHeight() >= 2, "Too few rows in " + input + ".");
		checkArgument(csvProfile == null || csvProfile.getColumns() == table.getWidth(),
				"The number of profiled columns does not match!");

		final Map<Integer, Type> typeHints = tryGetHints(csvProfile);

		final Locale locale = tryGetLocale(metadata);

		final TypedTable typedTable = this.tableAnalyzer.infer(table, locale, typeHints);
		checkArgument(typedTable.getHeight() >= 2, "Too few typed rows in " + input + ".");

		final SlicedTable slicedTable = this.tableSlicer.slice(typedTable, typeHints);

		final Map<Integer, DeclaredEntity> contextProperties;
		final Map<Integer, DeclaredEntity> contextClasses;
		
		if (contextCollected) {
			final ResultValue collectedContext = this.contextCollectionService.process(table, usedBases, primaryBase, this.random);
			
			contextProperties = getContextProperties(collectedContext, primaryBase);
			contextClasses = getContextClasses(collectedContext, primaryBase);
		} else {
			contextProperties = metadata.getCollectedProperties();
			contextClasses = metadata.getCollectedClasses();
		}
		
		final Set<PropertyTree> trees = this.propertyTreesBuilder.build(slicedTable, metadata.getDeclaredProperties(),
				metadata.getDeclaredClasses(), contextProperties, contextClasses, onlyWithProperties, onlyDeclaredAsContext);

		graph.addPropertyTrees(trees);
	}
	
	private ResultValue getCollectedDwtcContext(final ParsedTable table, final Path input,
			final Path collectionResultsDirectory, final Random random, final Set<? extends String> usedBases, final String primaryBase) {
		ResultValue result = null;
		
		final String fileName = com.google.common.io.Files.getNameWithoutExtension(input.getFileName().toString());
		
		final Path resultInput = collectionResultsDirectory.resolve(fileName + ".json");
		final Path failedCollectionNotice = collectionResultsDirectory.resolve(fileName + ".fail");
		if (resultInput.toFile().exists()) {
			try {
				result = loadCollectionResult(resultInput);
			} catch (IOException e) {
				throw new RuntimeException("Failed to load context collection result for " + input + "!", e);
			}
		} else if (failedCollectionNotice.toFile().exists()) {
			LOGGER.warn("Previously failed context collection attempt for " + input + "!");

			result = null;
		} else {
			try {
				result = contextCollectionService.process(table, usedBases, primaryBase, random);

				try {
					saveCollectionResult(result, resultInput);
				} catch (final IOException e) {
					throw new RuntimeException("Failed to save collected context for " + input + "!", e);
				}
			} catch (final Exception e) {
				LOGGER.error("Failed context collection attempt for " + input + "! Cause: " + e.getMessage());

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
	
	private ResultValue geCollectedTableContext(final ParsedTable table, final Path input,
			final Path collectionResultsDirectory, final Random random, final Set<? extends String> usedBases, final String primaryBase) {
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
			LOGGER.warn("Previously failed context collection attempt for " + input + "!");

			result = null;
		} else {
			try {
				result = contextCollectionService.process(table, usedBases, primaryBase, random);

				try {
					saveCollectionResult(result, resultInput);
				} catch (final IOException e) {
					throw new RuntimeException("Failed to save collected context for " + input + "!", e);
				}
			} catch (final Exception e) {
				LOGGER.error("Failed context collection attempt for " + input + "! Cause: " + e.getMessage());

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
	
	private void cacheFailedContextCollection(Path collectionResultsDirectory, Path input) throws IOException {
		Files.createFile(collectionResultsDirectory.resolve(input.getFileName() + ".fail"));
	}

	@Override
	public void learn(String graphName, final ParsedTable table, final boolean onlyWithProperties, final boolean contextCollected, final boolean onlyDeclaredAsContext,
			final Set<? extends String> usedBases, final String primaryBase) throws IOException {
		checkNotNull(graphName);
		checkNotNull(table);
		checkNotNull(usedBases);
		usedBases.stream().forEach(e -> {
			checkNotNull(e);
		});
		
		checkArgument((!contextCollected) || primaryBase != null, "To collect the context, specify primary and other used bases!");
		checkArgument(primaryBase == null || usedBases.contains(primaryBase), "To collect the context, specify the used bases!");
		checkArgument(table.getHeight() >= 2, "Too few rows in the table.");

		final BackgroundKnowledgeGraph graph = this.graphs.get(graphName);
		checkArgument(graph != null, "Unknown graph!");

		final Path cachedInput = this.fileCachingService.provideTemporaryFile();

		this.csvTableWriter.write(cachedInput.toFile(), table);

		CsvProfile csvProfile;
		try {
			csvProfile = this.csvProfilerService.profile(cachedInput.toFile());
		} catch (final IllegalStateException e) {
			LOGGER.warn("Profiling has failed!", e);

			csvProfile = null;
		}
		checkArgument(csvProfile == null || csvProfile.getColumns() == table.getWidth(),
				"The number of profiled columns does not match!");

		final Map<Integer, Type> typeHints = tryGetHints(csvProfile);

		final Locale locale = tryGetLocale(table.getMetadata());

		final TypedTable typedTable = this.tableAnalyzer.infer(table, locale, typeHints);
		checkArgument(typedTable.getHeight() >= 2, "Too few typed rows in the table.");

		final SlicedTable slicedTable = this.tableSlicer.slice(typedTable, typeHints);

		final Metadata metadata = slicedTable.getMetadata();
		
		final Map<Integer, DeclaredEntity> contextProperties;
		final Map<Integer, DeclaredEntity> contextClasses;
		
		if (contextCollected) {
			final ResultValue collectedContext = this.contextCollectionService.process(table, usedBases, primaryBase, this.random);
			
			contextProperties = getContextProperties(collectedContext, primaryBase);
			contextClasses = getContextClasses(collectedContext, primaryBase);
		} else {
			contextProperties = metadata.getCollectedProperties();
			contextClasses = metadata.getCollectedClasses();
		}

		final Set<PropertyTree> trees = this.propertyTreesBuilder.build(slicedTable, metadata.getDeclaredProperties(),
				metadata.getDeclaredClasses(), contextProperties, contextClasses, onlyWithProperties, onlyDeclaredAsContext);

		graph.addPropertyTrees(trees);

		this.graphsPersistingService.persist(graph.getName(), graph);
	}

	private static Format getFormat(final CsvProfile csvProfile, final Format forcedFormat) {
		if (forcedFormat != null) {
			return forcedFormat;
		}

		if (csvProfile == null) {
			return null;
		} else {
			return new Format(Charset.forName(csvProfile.getEncoding()),
					csvProfile.getDelimiter() == null ? null : csvProfile.getDelimiter().charAt(0), true,
					csvProfile.getQuotechar() == null ? null : csvProfile.getQuotechar().charAt(0), null, null);
		}
	}

	@Override
	public AnnotationResult annotate(String graphName, InputStream input, Format format, Metadata metadata, final boolean contextCollected, final boolean onlyDeclaredAsContext,
			final Set<? extends String> usedBases, final String primaryBase)
			throws IOException {
		checkNotNull(graphName);
		checkNotNull(input);
		checkNotNull(metadata);
		checkNotNull(usedBases);
		usedBases.stream().forEach(e -> {
			checkNotNull(e);
		});
		
		checkArgument((!contextCollected) || primaryBase != null, "To collect the context, specify primary and other used bases!");
		checkArgument(primaryBase == null || usedBases.contains(primaryBase), "To collect the context, specify the used bases!");

		final BackgroundKnowledgeGraph graph = this.graphs.get(graphName);
		checkArgument(graph != null, "Unknown graph!");

		final Path cachedInput = this.fileCachingService.cache(input);

		CsvProfile csvProfile;
		try {
			csvProfile = this.csvProfilerService.profile(cachedInput.toFile());
		} catch (final Exception e) {
			LOGGER.warn("Profiling has failed!", e);

			csvProfile = null;
		}

		final Format usedFormat = getFormat(csvProfile, format);

		ParsedTable table;
		try (final InputStream cleanedInput = this.csvCleanerService.clean(cachedInput.toFile())) {
			table = this.csvTableParser.parse(cleanedInput, usedFormat, metadata);
		} catch (final IllegalStateException e) {
			LOGGER.warn("Cleaning has failed! Using the raw input...", e);

			table = this.csvTableParser.parse(input, usedFormat, metadata);
		}
		checkArgument(table.getHeight() >= 2, "Too few rows in " + input + ".");
		checkArgument(csvProfile == null || csvProfile.getColumns() == table.getWidth(),
				"The number of profiled columns does not match!");

		final Map<Integer, Type> typeHints = tryGetHints(csvProfile);

		final Locale locale = tryGetLocale(metadata);

		final TypedTable typedTable = this.tableAnalyzer.infer(table, locale, typeHints);
		checkArgument(typedTable.getHeight() >= 2, "Too few typed rows in " + input + ".");

		final SlicedTable slicedTable = this.tableSlicer.slice(typedTable, typeHints);
		
		final Map<Integer, DeclaredEntity> contextProperties;
		final Map<Integer, DeclaredEntity> contextClasses;
		
		if (contextCollected) {
			final ResultValue collectedContext = this.contextCollectionService.process(table, usedBases, primaryBase, this.random);
			
			contextProperties = getContextProperties(collectedContext, primaryBase);
			contextClasses = getContextClasses(collectedContext, primaryBase);
		} else {
			contextProperties = metadata.getCollectedProperties();
			contextClasses = metadata.getCollectedClasses();
		}		

		return new AnnotationResult(table, this.annotator.annotate(graph, slicedTable, metadata.getDeclaredProperties(), metadata.getDeclaredClasses(), contextProperties, contextClasses, onlyDeclaredAsContext));
	}

	@Override
	public AnnotationResult annotate(final String graphName, final ParsedTable table, final boolean contextCollected,
			final boolean onlyDeclaredAsContext,
			final Set<? extends String> usedBases, final String primaryBase) throws IOException {
		checkNotNull(graphName);
		checkNotNull(table);
		checkNotNull(usedBases);
		usedBases.stream().forEach(e -> {
			checkNotNull(e);
		});
		
		checkArgument((!contextCollected) || primaryBase != null, "To collect the context, specify primary and other used bases!");
		checkArgument(primaryBase == null || usedBases.contains(primaryBase), "To collect the context, specify the used bases!");
		checkArgument(table.getHeight() >= 2, "Too few rows in the table.");

		final BackgroundKnowledgeGraph graph = this.graphs.get(graphName);
		checkArgument(graph != null, "Unknown graph!");

		final Path cachedInput = this.fileCachingService.provideTemporaryFile();
		this.csvTableWriter.write(cachedInput.toFile(), table);

		CsvProfile csvProfile;
		try {
			csvProfile = this.csvProfilerService.profile(cachedInput.toFile());
		} catch (final Exception e) {
			LOGGER.warn("Profiling has failed!", e);

			csvProfile = null;
		}
		checkArgument(csvProfile == null || csvProfile.getColumns() == table.getWidth(),
				"The number of profiled columns does not match!");

		final Map<Integer, Type> typeHints = tryGetHints(csvProfile);

		final Locale locale = tryGetLocale(table.getMetadata());

		final TypedTable typedTable = this.tableAnalyzer.infer(table, locale, typeHints);
		checkArgument(typedTable.getHeight() >= 2, "Too few typed rows in the table.");

		final SlicedTable slicedTable = this.tableSlicer.slice(typedTable, typeHints);

		final Metadata metadata = slicedTable.getMetadata();
		
		final Map<Integer, DeclaredEntity> contextProperties;
		final Map<Integer, DeclaredEntity> contextClasses;
		
		if (contextCollected) {
			final ResultValue collectedContext = this.contextCollectionService.process(table, usedBases, primaryBase, this.random);
			
			contextProperties = getContextProperties(collectedContext, primaryBase);
			contextClasses = getContextClasses(collectedContext, primaryBase);
		} else {
			contextProperties = metadata.getCollectedProperties();
			contextClasses = metadata.getCollectedClasses();
		}		
		
		final AnnotationResult result = new AnnotationResult(table, this.annotator.annotate(graph, slicedTable, metadata.getDeclaredProperties(), metadata.getDeclaredClasses(), contextProperties, contextClasses, onlyDeclaredAsContext));

		return result;
	}

	private static Map<Integer, Type> tryGetHints(@Nullable final CsvProfile csvProfile) {
		if (csvProfile == null) {
			return ImmutableMap.of();
		}

		return getHints(csvProfile);
	}

	private static Map<Integer, Type> getHints(final CsvProfile csvProfile) {
		return Lists.toMap(csvProfile.getTypes());
	}

	private static Locale tryGetLocale(Metadata metadata) {
		return metadata.getLanguageTag() == null ? Locale.US : getLocale(metadata);
	}

	private static Locale getLocale(Metadata metadata) {
		return Locale.forLanguageTag(metadata.getLanguageTag());
	}

	@Override
	public void delete(final String name) {
		final BackgroundKnowledgeGraph removed = this.graphs.remove(name);
		checkArgument(removed != null, "No such graph present!");

		this.graphsPersistingService.delete(removed.getName());
	}

	@Override
	public SearchResult search(final String graphName, final String pattern, final Integer flags, final int limit) {
		checkNotNull(graphName);
		checkNotNull(pattern);
		checkArgument(limit >= 1, "The limit on the number of searched items must be at least one!");

		final Pattern compiledPattern;
		try {
			if (flags == null) {
				compiledPattern = Pattern.compile(pattern);
			} else {
				compiledPattern = Pattern.compile(pattern, flags);
			}
		} catch (final PatternSyntaxException e) {
			throw new IllegalArgumentException("Invalid search pattern!", e);
		}

		final BackgroundKnowledgeGraph graph = this.graphs.get(graphName);
		checkArgument(graph != null, "Unknown graph!");

		return new SearchResult(graph.getProperties().stream().filter(property -> {
			final URI uri = property.getUri();

			if (uri != null) {
				if (compiledPattern.matcher(uri.toString()).find()) {
					return true;
				}
			}

			return property.getInstances().stream().filter(tree -> {
				final Label label = tree.getRoot().getLabel();
				if (label == null) {
					return false;
				}

				return compiledPattern.matcher(label.getText()).find();
			}).findAny().isPresent();
		}).limit(limit).collect(ImmutableList.toImmutableList()));
	}
}
