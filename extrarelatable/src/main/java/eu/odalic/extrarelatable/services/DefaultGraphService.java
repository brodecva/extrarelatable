package eu.odalic.extrarelatable.services;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.odalic.extrarelatable.algorithms.graph.Annotator;
import eu.odalic.extrarelatable.algorithms.graph.PropertyTreesBuilder;
import eu.odalic.extrarelatable.algorithms.table.TableAnalyzer;
import eu.odalic.extrarelatable.algorithms.table.TableSlicer;
import eu.odalic.extrarelatable.algorithms.table.csv.CsvTableParser;
import eu.odalic.extrarelatable.model.annotation.Annotation;
import eu.odalic.extrarelatable.model.bag.Type;
import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.graph.PropertyTreesMergingStrategy;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;
import eu.odalic.extrarelatable.model.table.csv.Format;
import eu.odalic.extrarelatable.services.csvengine.csvclean.CsvCleanService;
import eu.odalic.extrarelatable.services.csvengine.csvprofiler.CsvProfile;
import eu.odalic.extrarelatable.services.csvengine.csvprofiler.CsvProfilerService;
import eu.odalic.extrarelatable.util.Lists;

@Service
public class DefaultGraphService implements GraphService {

	private final PropertyTreesMergingStrategy propertyTreesMergingStrategy;
	private final FileCachingService fileCachingService;
	private final CsvProfilerService csvProfilerService;
	private final CsvCleanService csvCleanerService;
	private final CsvTableParser csvTableParser;
	private final TableAnalyzer tableAnalyzer;
	private final PropertyTreesBuilder propertyTreesBuilder;
	private final TableSlicer tableSlicer;
	private final Annotator annotator;
	
	private final Map<String, BackgroundKnowledgeGraph> graphs;
	
	DefaultGraphService(PropertyTreesMergingStrategy propertyTreesMergingStrategy,
			FileCachingService fileCachingService, CsvProfilerService csvProfilerService,
			CsvCleanService csvCleanerService, CsvTableParser csvTableParser, TableAnalyzer tableAnalyzer,
			PropertyTreesBuilder propertyTreesBuilder, TableSlicer tableSlicer, Annotator annotator,
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
		this.graphs = graphs;
	}
	
	@Inject
	public DefaultGraphService(@Qualifier("labelText") PropertyTreesMergingStrategy propertyTreesMergingStrategy,
			FileCachingService fileCachingService, CsvProfilerService csvProfilerService,
			CsvCleanService csvCleanerService, @Qualifier("automatic") CsvTableParser csvTableParser, TableAnalyzer tableAnalyzer,
			PropertyTreesBuilder propertyTreesBuilder, TableSlicer tableSlicer, Annotator annotator) {
		this(propertyTreesMergingStrategy, fileCachingService, csvProfilerService, csvCleanerService, csvTableParser, tableAnalyzer, propertyTreesBuilder, tableSlicer, annotator, new HashMap<>());
	}

	@Override
	public void create(final String name) {
		checkNotNull(name);
		
		this.graphs.put(name, new BackgroundKnowledgeGraph(this.propertyTreesMergingStrategy));
	}

	@Override
	public void learn(String graphName, final InputStream input, @Nullable Format format, Metadata metadata) throws IOException {
		checkNotNull(graphName);
		checkNotNull(input);
		checkNotNull(metadata);
		
		final BackgroundKnowledgeGraph graph = this.graphs.get(graphName);
		checkArgument(graph != null, "Unknown graph!");
		
		final Path cachedInput = this.fileCachingService.cache(input);
		
		final CsvProfile csvProfile = this.csvProfilerService.profile(cachedInput.toFile());

		final Format usedFormat = format == null ? getFormat(csvProfile) : format;
		
		final ParsedTable table;
		try (final InputStream cleanedInput = this.csvCleanerService.clean(cachedInput.toFile())) { 
				table = this.csvTableParser.parse(cleanedInput, usedFormat, metadata);
		}
		checkArgument(table.getHeight() >= 2, "Too few rows in " + input + ".");
		
		final Map<Integer, Type> typeHints = Lists.toMap(csvProfile.getTypes());
		
		final Locale locale = getLocale(metadata);
		
		final TypedTable typedTable = this.tableAnalyzer.infer(table, locale, typeHints);
		checkArgument(typedTable.getHeight() >= 2, "Too few typed rows in " + input + ".");

		final SlicedTable slicedTable = this.tableSlicer.slice(typedTable, typeHints);

		final Set<PropertyTree> trees = this.propertyTreesBuilder.build(slicedTable);
		
		graph.addPropertyTrees(trees);
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

	private static Locale getLocale(Metadata metadata) {
		return metadata.getLanguageTag() == null ? Locale.getDefault() : Locale.forLanguageTag(metadata.getLanguageTag());
	}
	
	@Override
	public Map<Integer, Annotation> annotate(String graphName, InputStream input, Format format, Metadata metadata) throws IOException {
		checkNotNull(graphName);
		checkNotNull(input);
		checkNotNull(metadata);
		
		final BackgroundKnowledgeGraph graph = this.graphs.get(graphName);
		checkArgument(graph != null, "Unknown graph!");
		
		final Path cachedInput = this.fileCachingService.cache(input);
		
		final CsvProfile csvProfile = this.csvProfilerService.profile(cachedInput.toFile());

		final Format usedFormat = format == null ? getFormat(csvProfile) : format;
		
		final ParsedTable table;
		try (final InputStream cleanedInput = this.csvCleanerService.clean(cachedInput.toFile())) { 
				table = this.csvTableParser.parse(cleanedInput, usedFormat, metadata);
		}
		checkArgument(table.getHeight() >= 2, "Too few rows in " + input + ".");
		
		final Map<Integer, Type> typeHints = Lists.toMap(csvProfile.getTypes());
		
		final Locale locale = getLocale(metadata);
		
		final TypedTable typedTable = this.tableAnalyzer.infer(table, locale, typeHints);
		checkArgument(typedTable.getHeight() >= 2, "Too few typed rows in " + input + ".");

		final SlicedTable slicedTable = this.tableSlicer.slice(typedTable, typeHints);
		
		return this.annotator.annotate(graph, slicedTable);
	}
}
