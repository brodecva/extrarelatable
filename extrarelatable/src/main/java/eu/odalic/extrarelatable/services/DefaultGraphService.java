package eu.odalic.extrarelatable.services;

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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import eu.odalic.extrarelatable.model.bag.Type;
import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.graph.PropertyTreesMergingStrategy;
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
import eu.odalic.extrarelatable.util.Lists;
import eu.odalic.extrarelatable.util.Matrix;
import webreduce.data.Dataset;
import webreduce.data.HeaderPosition;

@Service
public class DefaultGraphService implements GraphService {

	private static final String DOT_LEADING_FAILED_RESULT_FILE_SUFFIX = ".fail";
	private static final String DOT_LEADING_CSV_SUFFIX = ".csv";
	private static final String CHARACTERS_TO_SANITIZE_REGEX = "[.]";
	private static final String SANITIZE_WITH = "_";
	
	private static final Pattern LOCALE_LANGUAGE_TAG_IN_GRAPH_NAME_PATTERN = Pattern.compile("_([^_]+)$");
	private static final Locale DEFAULT_LOCALE = Locale.getDefault();
	
	private static final Path TABLES_SUBPATH = Paths.get("tables");
	private static final Path DECLARED_PROPERTIES_SUBPATH = Paths.get("property");
	private static final Path PARSED_TABLES_CSVS_SUBPATH = Paths.get("csvs");
	private static final Path PROFILES_SUBPATH = Paths.get("profiles");
	private static final Path CLEANED_CSVS_SUBPATH = Paths.get("cleaned");
	

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGraphService.class);
	
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
	
	private final Map<String, BackgroundKnowledgeGraph> graphs;

	
	DefaultGraphService(PropertyTreesMergingStrategy propertyTreesMergingStrategy,
			FileCachingService fileCachingService, CsvProfilerService csvProfilerService,
			CsvCleanService csvCleanerService, CsvTableParser csvTableParser, TableAnalyzer tableAnalyzer,
			PropertyTreesBuilder propertyTreesBuilder, TableSlicer tableSlicer, Annotator annotator,
			CsvTableWriter csvTableWriter, DwtcToCsvService dwtcToCsvService,
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
		checkNotNull(graphs);
		
		
		graphs.entrySet().stream().forEach(
			e -> {
				checkNotNull(e.getKey());
				checkNotNull(e.getValue());
			}
		);
		
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
		this.graphs = graphs;
	}
	
	@Inject
	public DefaultGraphService(@Qualifier("labelText") PropertyTreesMergingStrategy propertyTreesMergingStrategy,
			FileCachingService fileCachingService, CsvProfilerService csvProfilerService,
			CsvCleanService csvCleanerService, @Qualifier("automatic") CsvTableParser csvTableParser, TableAnalyzer tableAnalyzer,
			PropertyTreesBuilder propertyTreesBuilder, TableSlicer tableSlicer, Annotator annotator, CsvTableWriter csvTableWriter,
			DwtcToCsvService dwtcToCsvService, final @Nullable @Value("${eu.odalic.extrarelatable.graphsPath}") Path graphsPath, final @Nullable @Value("${eu.odalic.extrarelatable.onlyWithProperties}") Boolean onlyWithProperties) {
		this(propertyTreesMergingStrategy, fileCachingService, csvProfilerService, csvCleanerService, csvTableParser, tableAnalyzer, propertyTreesBuilder, tableSlicer, annotator, csvTableWriter, dwtcToCsvService, new HashMap<>());
		
		if (graphsPath != null) {
			this.graphs.putAll(loadGraphs(graphsPath, onlyWithProperties == null ? false : onlyWithProperties).stream().collect(ImmutableMap.toImmutableMap(graph -> graph.getName(), graph -> graph)));
		}
	}
	
	private Set<BackgroundKnowledgeGraph> loadGraphs(final Path graphsPath, boolean onlyWithProperties) {
		return Streams.stream(graphsPath.iterator()).filter(path -> {
			if (!path.toFile().isDirectory()) {
				LOGGER.warn(path + " is not a directory.");
				
				return false;
			} else {
				return true;
			}
		})
		.map(path -> loadGraph(path, onlyWithProperties))
		.collect(ImmutableSet.toImmutableSet());
	}
	
	private BackgroundKnowledgeGraph loadGraph(final Path graphPath, boolean onlyWithProperties) {
		checkArgument(graphPath.toFile().exists(), "The " + graphPath + " does not exist!");
		checkArgument(graphPath.toFile().isDirectory(), "The " + graphPath + " is not a directory!");
		
		final String graphName = graphPath.getFileName().toString();
		
		final Locale locale = tryGetLocaleFromGraphName(graphName);
				
		final Path tablesPath = graphPath.resolve(TABLES_SUBPATH);
		
		if (tablesPath.toFile().exists() && tablesPath.toFile().isDirectory()) {
			return loadDwtcGraph(graphName, tablesPath, tablesPath.resolve(PROFILES_SUBPATH), tablesPath.resolve(PARSED_TABLES_CSVS_SUBPATH), graphPath.resolve(DECLARED_PROPERTIES_SUBPATH), onlyWithProperties, locale);
		} else {
			return loadBagOfTablesGraph(graphName, graphPath, graphPath.resolve(PROFILES_SUBPATH), graphPath.resolve(CLEANED_CSVS_SUBPATH), locale);
		}
		
	}
	
	private Locale tryGetLocaleFromGraphName(String graphName) {
		final Locale locale = getLocaleFromGraphName(graphName);
		if (locale == null) {
			return DEFAULT_LOCALE;
		}
		
		return locale;		
	}

	private BackgroundKnowledgeGraph loadBagOfTablesGraph(final String graphName, final Path graphPath, final Path profilesPath, final Path cleanedCsvsPath, Locale locale) {
		checkArgument(graphPath.toFile().exists() && graphPath.toFile().isDirectory(), "The directory for " + graphName + " is missing!");
		checkArgument(profilesPath.toFile().exists() && profilesPath.toFile().isDirectory(), "The profiles directory for " + graphName + " is missing!");
		checkArgument(cleanedCsvsPath.toFile().exists() && cleanedCsvsPath.toFile().isDirectory(), "The cleaned directory for " + graphName + " is missing!");
		
		final BackgroundKnowledgeGraph graph = new BackgroundKnowledgeGraph(this.propertyTreesMergingStrategy);
		
		graphPath.forEach(tablePath -> graph.addPropertyTrees(learnTable(tablePath, cleanedCsvsPath, profilesPath, locale)));
		
		return graph;
	}
	
	private BackgroundKnowledgeGraph loadDwtcGraph(final String graphName, final Path tablesPath, final Path profilesPath, final Path csvsPath, final Path declaredPropertiesPath, boolean onlyWithProperties, Locale locale) {
		checkArgument(tablesPath.toFile().exists() && tablesPath.toFile().isDirectory(), "The directory for " + graphName + " is missing!");
		checkArgument(profilesPath.toFile().exists() && profilesPath.toFile().isDirectory(), "The profiles directory for " + graphName + " is missing!");
		checkArgument(csvsPath.toFile().exists() && csvsPath.toFile().isDirectory(), "The csvs directory for " + graphName + " is missing!");
		checkArgument(declaredPropertiesPath.toFile().exists() && declaredPropertiesPath.toFile().isDirectory(), "The property directory for " + graphName + " is missing!");
		
		final BackgroundKnowledgeGraph graph = new BackgroundKnowledgeGraph(this.propertyTreesMergingStrategy);
		
		tablesPath.forEach(tablePath -> graph.addPropertyTrees(learnDwtcTable(tablePath, csvsPath, profilesPath, declaredPropertiesPath, onlyWithProperties, locale)));
		
		return graph;
	}
	
	private Set<PropertyTree> learnTable(Path tablePath, Path cleanedCsvsPath, Path profilesPath, final Locale locale) {
		final Path cleanedInput = getCleanedInput(tablePath, cleanedCsvsPath);
		
		final CsvProfile csvProfile = getProfile(cleanedInput, profilesPath);
		
		final Format format = getFormat(csvProfile);
		
		final ParsedTable table = parse(tablePath, cleanedInput, format);
		if (table.getHeight() < 2) {
			LOGGER.warn("Too few rows in " + tablePath + ". Skipping.");
			return ImmutableSet.of();
		}

		final Map<Integer, Type> typeHints = tryGetHints(csvProfile);
		
		final TypedTable typedTable = tableAnalyzer.infer(table, locale, typeHints);
		if (typedTable.getHeight() < 2) {
			LOGGER.warn("Too few typed rows in " + tablePath + ". Skipping.");
			return ImmutableSet.of();
		}

		final SlicedTable slicedTable = tableSlicer.slice(typedTable, typeHints);

		return this.propertyTreesBuilder.build(slicedTable);
	}
	
	private Locale getLocaleFromGraphName(final String graphName) {
		final Matcher matcher = LOCALE_LANGUAGE_TAG_IN_GRAPH_NAME_PATTERN.matcher(graphName);
		
		if (!matcher.find()) {
			return null;
		}
		
		return Locale.forLanguageTag(matcher.group(0));
	}

	private Path getCleanedInput(final Path input, final Path cleanedInputFilesDirectory) {
		Path cleanedInput = cleanedInputFilesDirectory.resolve(input.getFileName());
		final Path failedCleanNotice = cleanedInputFilesDirectory.resolve(input.getFileName() + DOT_LEADING_FAILED_RESULT_FILE_SUFFIX);
		if (cleanedInput.toFile().exists()) {
			System.out.println("File " + input + " already cleaned.");
		} else if (failedCleanNotice.toFile().exists()) {
			System.out.println("Previously failed cleaning attempt for + " + input + ". Using original instead.");
			
			cleanedInput = input;
		} else {
			try (final InputStream cleanedInputStream = this.csvCleanerService.clean(input.toFile())) {
				Files.copy(cleanedInputStream, cleanedInput);
			} catch (final IllegalStateException e) {
				System.out.println("Failed clean attempt for + " + input + "!");
				
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
		Files.createFile(cleanedInputFilesDirectory.resolve(input.getFileName() + DOT_LEADING_FAILED_RESULT_FILE_SUFFIX));
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
	
	private static Format getFormat(final CsvProfile csvProfile) {
		final Format format;
		if (csvProfile == null) {
			format = null;
		} else {
			format = new Format(Charset.forName(csvProfile.getEncoding()), csvProfile.getDelimiter() == null ? null : csvProfile.getDelimiter().charAt(0), true, csvProfile.getQuotechar() == null ? null : csvProfile.getQuotechar().charAt(0), null, null);
		}
		return format;
	}

	private Set<PropertyTree> learnDwtcTable(Path tablePath, Path csvsPath, Path profilesPath,
			Path declaredPropertiesPath, boolean onlyWithProperties, Locale locale) {
		final Path convertedInput = convertDwtcToCsv(tablePath, csvsPath);

		final CsvProfile csvProfile = getProfile(convertedInput, profilesPath);

		final Dataset dataset = parseDwtc(tablePath);
		final HeaderPosition headerPosition = dataset.getHeaderPosition();
		if (headerPosition != HeaderPosition.FIRST_ROW) {
			LOGGER.warn("File " + tablePath + " has no regular header!");
			return ImmutableSet.of();
		}

		final ParsedTable table = toParsedTable(dataset, tablePath.getFileName().toString());
		if (table.getHeight() < 2) {
			LOGGER.warn("Too few rows in " + tablePath + ". Skipping.");
			return ImmutableSet.of();
		}

		final Map<Integer, Type> typeHints = tryGetHints(csvProfile);

		final TypedTable typedTable = tableAnalyzer.infer(table, locale, typeHints);
		if (typedTable.getHeight() < 2) {
			LOGGER.warn("Too few typed rows in " + tablePath + ". Skipping.");
			return ImmutableSet.of();
		}

		final SlicedTable slicedTable = this.tableSlicer.slice(typedTable, typeHints);

		final Map<Integer, URI> declaredPropertyUris = getDeclaredPropertyUris(declaredPropertiesPath,
				tablePath.getFileName().toString());

		return this.propertyTreesBuilder.build(slicedTable, declaredPropertyUris, onlyWithProperties);
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

		final String propertiesFileName = sanitizedFileName + DOT_LEADING_CSV_SUFFIX;

		final Path propertiesPath = declaredPropertiesPath.resolve(propertiesFileName);
		if (!propertiesPath.toFile().exists()) {
			return null;
		}
		
		return propertiesPath;
	}
	
	private static ParsedTable toParsedTable(final Dataset dataset, final String fileName) {
		final ParsedTable table = NestedListsParsedTable.fromColumns(Matrix.fromArray(dataset.getRelation()),
				new Metadata(fileName, dataset.getUrl(), null));

		return table;
	}
	
	private Path convertDwtcToCsv(final Path dwtcTablePath, final Path csvsPath) {
		final Path csvTablePath = csvsPath.resolve(dwtcTablePath.getFileName() + DOT_LEADING_CSV_SUFFIX);
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
	
	private CsvProfile getProfile(final Path input, final Path profilesDirectory) {
		CsvProfile csvProfile = null;
		final Path profileInput = profilesDirectory.resolve(input.getFileName());
		final Path failedProfileNotice = profilesDirectory.resolve(input.getFileName() + DOT_LEADING_FAILED_RESULT_FILE_SUFFIX);
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

	@Override
	public void create(final String name) {
		checkNotNull(name);
		
		this.graphs.put(name, new BackgroundKnowledgeGraph(this.propertyTreesMergingStrategy));
	}
	
	@Override
	public boolean exists(final String name) {
		checkNotNull(name);
		
		return this.graphs.containsKey(name);
	}

	@Override
	public void learn(String graphName, final InputStream input, @Nullable Format format, Metadata metadata) throws IOException {
		checkNotNull(graphName);
		checkNotNull(input);
		checkNotNull(metadata);
		
		final BackgroundKnowledgeGraph graph = this.graphs.get(graphName);
		checkArgument(graph != null, "Unknown graph!");
		
		final Path cachedInput = this.fileCachingService.cache(input);
		
		learn(graph, cachedInput, format, metadata);
	}
	
	private void learn(final BackgroundKnowledgeGraph graph, final Path input, @Nullable Format format, Metadata metadata) throws IOException {
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
		checkArgument(csvProfile == null || csvProfile.getColumns() == table.getWidth(), "The number of profiled columns does not match!");
		
		final Map<Integer, Type> typeHints = tryGetHints(csvProfile);
		
		final Locale locale = tryGetLocale(metadata);
		
		final TypedTable typedTable = this.tableAnalyzer.infer(table, locale, typeHints);
		checkArgument(typedTable.getHeight() >= 2, "Too few typed rows in " + input + ".");

		final SlicedTable slicedTable = this.tableSlicer.slice(typedTable, typeHints);

		final Set<PropertyTree> trees = this.propertyTreesBuilder.build(slicedTable);
		
		graph.addPropertyTrees(trees);
	}
	
	@Override
	public void learn(String graphName, final ParsedTable table) throws IOException {
		checkNotNull(graphName);
		checkNotNull(table);
		
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
		checkArgument(csvProfile == null || csvProfile.getColumns() == table.getWidth(), "The number of profiled columns does not match!");
		
		final Map<Integer, Type> typeHints = tryGetHints(csvProfile);
		
		final Locale locale = tryGetLocale(table.getMetadata());
		
		final TypedTable typedTable = this.tableAnalyzer.infer(table, locale, typeHints);
		checkArgument(typedTable.getHeight() >= 2, "Too few typed rows in the table.");

		final SlicedTable slicedTable = this.tableSlicer.slice(typedTable, typeHints);

		final Set<PropertyTree> trees = this.propertyTreesBuilder.build(slicedTable);
		
		graph.addPropertyTrees(trees);
	}
	
	private static Format getFormat(final CsvProfile csvProfile, final Format forcedFormat) {
		if (forcedFormat != null) {
			return forcedFormat;
		}
		
		if (csvProfile == null) {
			return null;
		} else {
			return new Format(Charset.forName(csvProfile.getEncoding()), csvProfile.getDelimiter() == null ? null : csvProfile.getDelimiter().charAt(0), true, csvProfile.getQuotechar() == null ? null : csvProfile.getQuotechar().charAt(0), null, null);
		}
	}

	@Override
	public AnnotationResult annotate(String graphName, InputStream input, Format format, Metadata metadata) throws IOException {
		checkNotNull(graphName);
		checkNotNull(input);
		checkNotNull(metadata);
		
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
		checkArgument(csvProfile == null || csvProfile.getColumns() == table.getWidth(), "The number of profiled columns does not match!");
		
		final Map<Integer, Type> typeHints = tryGetHints(csvProfile);
		
		final Locale locale = tryGetLocale(metadata);
		
		final TypedTable typedTable = this.tableAnalyzer.infer(table, locale, typeHints);
		checkArgument(typedTable.getHeight() >= 2, "Too few typed rows in " + input + ".");

		final SlicedTable slicedTable = this.tableSlicer.slice(typedTable, typeHints);
		
		return new AnnotationResult(table, this.annotator.annotate(graph, slicedTable));
	}
	
	@Override
	public AnnotationResult annotate(final String graphName, final ParsedTable table) throws IOException {
		checkNotNull(graphName);
		checkNotNull(table);
		
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
		checkArgument(csvProfile == null || csvProfile.getColumns() == table.getWidth(), "The number of profiled columns does not match!");
		
		final Map<Integer, Type> typeHints = tryGetHints(csvProfile);
		
		final Locale locale = tryGetLocale(table.getMetadata());
		
		final TypedTable typedTable = this.tableAnalyzer.infer(table, locale, typeHints);
		checkArgument(typedTable.getHeight() >= 2, "Too few typed rows in the table.");

		final SlicedTable slicedTable = this.tableSlicer.slice(typedTable, typeHints);
		
		return new AnnotationResult(table, this.annotator.annotate(graph, slicedTable));
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
	}
}
