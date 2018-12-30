package eu.odalic.extrarelatable.services.graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Pattern;

import eu.odalic.extrarelatable.model.annotation.AnnotationResult;
import eu.odalic.extrarelatable.model.graph.SearchResult;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.csv.Format;

/**
 * Service allowing to manipulate the background knowledge graphs used by ERT to
 * model the learned knowledg and to annotate the inputs.
 * 
 * @author Václav Brodec
 *
 */
public interface GraphService {
	/**
	 * Creates new graph with the set name.
	 * 
	 * @param name
	 *            new graph name
	 */
	void create(String name);

	/**
	 * Verifies existence of the named graph.
	 * 
	 * @param name
	 *            graph name
	 * @return true if a graph of that name exists, otherwise false
	 */
	boolean exists(String name);

	/**
	 * Deletes the named graph (with all the learned background knowledge)
	 * 
	 * @param name
	 *            existing graph name
	 */
	void delete(String name);

	/**
	 * Makes the content from the parsed table part of the background knowledge
	 * graph. Depending on the setting of the boolean flags it either accepts only
	 * the provided declared properties and classes in the meta-data as context, or
	 * it accepts even the collected context from the meta-data, or it collects the
	 * context ex post by querying associated Odalic instance.
	 * 
	 * @param graphName
	 *            name of the graph
	 * @param table
	 *            parsed table
	 * @param onlyWithProperties
	 *            determines whether only the numeric columns with associated
	 *            declared property in the meta-data are learned
	 * @param contextCollected
	 *            indicates whether to collect context from associated Odalic
	 *            instance to annotate the table, if false, then the context
	 *            provided as part of the table meta-data is taken into account
	 *            instead (unless turned off by setting
	 *            {@code onlyDeclaredAsContext} to {@code true})
	 * @param onlyDeclaredAsContext
	 *            indicates whether to use only the declared context classes and
	 *            properties from the table meta-data, not the collected one
	 * @param usedBases
	 *            identifiers of known linked data knowledge bases within the
	 *            associated Odalic instance
	 * @param primaryBase
	 *            name of one of the used bases, which takes precedence
	 * @throws IOException
	 *             whenever I/O exception occurs
	 */
	void learn(String graphName, ParsedTable table, boolean onlyWithProperties, boolean contextCollected,
			boolean onlyDeclaredAsContext, Set<? extends String> usedBases, String primaryBase) throws IOException;

	/**
	 * Makes the content from the table, which his provided as CSV input stream,
	 * part of the background knowledge graph, after parsing it according to the
	 * provided format. Depending on the setting of the boolean flags it either
	 * accepts only the provided declared properties and classes in the meta-data as
	 * context, or it accepts even the collected context from the meta-data, or it
	 * collects the context ex post by querying associated Odalic instance.
	 * 
	 * @param graphName
	 *            name of the graph
	 * @param input
	 *            input stream used as the source of the CSV data to parse
	 * @param format
	 *            format of the CSV file to parse the input stream with
	 * @param metadata
	 *            meta-data accompanying the table
	 * @param onlyWithProperties
	 *            determines whether only the numeric columns with associated
	 *            declared property in the meta-data are learned
	 * @param contextCollected
	 *            indicates whether to collect context from associated Odalic
	 *            instance to annotate the table, if false, then the context
	 *            provided as part of the table meta-data is taken into account
	 *            instead (unless turned off by setting
	 *            {@code onlyDeclaredAsContext} to {@code true})
	 * @param onlyDeclaredAsContext
	 *            indicates whether to use only the declared context classes and
	 *            properties from the table meta-data, not the collected one
	 * @param usedBases
	 *            identifiers of known linked data knowledge bases within the
	 *            associated Odalic instance
	 * @param primaryBase
	 *            name of one of the used bases, which takes precedence
	 * @throws IOException
	 *             whenever I/O exception occurs
	 */
	void learn(String graphName, InputStream input, Format format, Metadata metadata, boolean onlyWithProperties,
			boolean contextCollected, boolean onlyDeclaredAsContext, Set<? extends String> usedBases,
			String primaryBase) throws IOException;

	/**
	 * Annotates the parsed table. Depending on the setting of the boolean flags it
	 * either accepts only the provided declared properties and classes in the
	 * meta-data as context, or it accepts even the collected context from the
	 * meta-data, or it collects the context ex post by querying associated Odalic
	 * instance.
	 * 
	 * @param graphName
	 *            name of the graph
	 * @param table
	 *            parsed table
	 * @param contextCollected
	 *            indicates whether to collect context from associated Odalic
	 *            instance to annotate the table, if false, then the context
	 *            provided as part of the table meta-data is taken into account
	 *            instead (unless turned off by setting
	 *            {@code onlyDeclaredAsContext} to {@code true})
	 * @param onlyDeclaredAsContext
	 *            indicates whether to use only the declared context classes and
	 *            properties from the table meta-data, not the collected one
	 * @param usedBases
	 *            identifiers of known linked data knowledge bases within the
	 *            associated Odalic instance
	 * @param primaryBase
	 *            name of one of the used bases, which takes precedence
	 * @return result of the processing of the table by ERT
	 * @throws IOException
	 *             whenever I/O exception occurs
	 */
	AnnotationResult annotate(String graphName, ParsedTable table, boolean contextCollected,
			boolean onlyDeclaredAsContext, Set<? extends String> usedBases, String primaryBase) throws IOException;

	/**
	 * Annotates the table provided as CSV input stream after parsing it according
	 * to the provided format. Depending on the setting of the boolean flags it
	 * either accepts only the provided declared properties and classes in meta-data
	 * as context, or it accepts even the collected context from the meta-data, or
	 * it collects the context ex post by querying associated Odalic instance.
	 * 
	 * @param graphName
	 *            name of the graph
	 * @param input
	 *            input stream used as the source of the CSV data to parse
	 * @param format
	 *            format of the CSV file to parse the input stream with
	 * @param metadata
	 *            meta-data accompanying the table
	 * @param contextCollected
	 *            indicates whether to collect context from associated Odalic
	 *            instance to annotate the table, if false, then the context
	 *            provided as part of the table meta-data is taken into account
	 *            instead (unless turned off by setting
	 *            {@code onlyDeclaredAsContext} to {@code true})
	 * @param onlyDeclaredAsContext
	 *            indicates whether to use only the declared context classes and
	 *            properties from the table meta-data, not the collected one
	 * @param usedBases
	 *            identifiers of known linked data knowledge bases within the
	 *            associated Odalic instance
	 * @param primaryBase
	 *            name of one of the used bases, which takes precedence
	 * @return result of the processing of the table by ERT
	 * @throws IOException
	 *             whenever I/O exception occurs
	 */
	AnnotationResult annotate(String graphName, InputStream input, Format format, Metadata metadata,
			boolean contextCollected, boolean onlyDeclaredAsContext, Set<? extends String> usedBases,
			String primaryBase) throws IOException;

	/**
	 * Searches for contained properties that match the pattern. For now only
	 * matching of the URI is supported.
	 * 
	 * @param graphName
	 *            name of the searched graph
	 * @param pattern
	 *            search pattern (conforms to format of {@link Pattern})
	 * @param flags
	 *            {@link Pattern} flags
	 * @param limit
	 *            maximum number of returned results
	 * @return encapsulated matching results
	 */
	SearchResult search(String graphName, String pattern, Integer flags, int limit);
}
