package eu.odalic.extrarelatable.api.rest.resources;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.webcohesion.enunciate.metadata.DocumentationExample;
import com.webcohesion.enunciate.metadata.rs.ResourceLabel;
import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import eu.odalic.extrarelatable.api.rest.responses.Message;
import eu.odalic.extrarelatable.api.rest.responses.Reply;
import eu.odalic.extrarelatable.api.rest.values.AnnotationResultValue;
import eu.odalic.extrarelatable.api.rest.values.FormatValue;
import eu.odalic.extrarelatable.api.rest.values.GraphValue;
import eu.odalic.extrarelatable.api.rest.values.MetadataValue;
import eu.odalic.extrarelatable.api.rest.values.ParsedTableValue;
import eu.odalic.extrarelatable.api.rest.values.SearchResultValue;
import eu.odalic.extrarelatable.model.annotation.AnnotationResult;
import eu.odalic.extrarelatable.model.graph.SearchResult;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.NestedListsParsedTable;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.csv.Format;
import eu.odalic.extrarelatable.services.graph.GraphService;
import jersey.repackaged.com.google.common.collect.ImmutableSet;

/**
 * <p>
 * Background knowledge graphs resource definition.
 * </p>
 * 
 * @author Václav Brodec
 * 
 */
@Component
@ResourceLabel("Background knowledge graph resource")
@Path("/graphs")
public final class GraphResource {

	public static final String TEXT_CSV_MEDIA_TYPE = "text/csv";

	private final GraphService graphService;

	@Context
	private UriInfo uriInfo;

	@Autowired
	public GraphResource(final GraphService graphService) {
		checkNotNull(graphService, "The graphService cannot be null!");

		this.graphService = graphService;
	}

	/**
	 * Creates a new graph addressable by the path.
	 * 
	 * @param name
	 *            name of the graph
	 * @param graphValue
	 *            graph-representing object {@link GraphValue}
	 * @return a {@link Reply} containing {@link Message} in {@code payload} attribute and "MESSAGE" in {@code type} attribute
	 * @throws MalformedURLException
	 *             when the combination of the base URL and the provided graph name
	 *             forms invalid URL
	 * 
	 */
	@PUT
	@Path("{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@StatusCodes({ @ResponseCode(code = 400, condition = "The name field value does not match the path name."),
			@ResponseCode(code = 200, condition = "A new graph has been created.") })
	@TypeHint(Reply.class)
	public Response put(final @PathParam("name") @DocumentationExample("example_dataset__en-us") String name,
			final GraphValue graphValue) throws MalformedURLException {
		if (graphValue.getName() != null || graphValue.getName() != name) {
			throw new BadRequestException("The name field value does not match the path name!");
		}

		try {
			this.graphService.create(name);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}

		return Message.of("A new graph has been created.").toResponse(Response.Status.CREATED,
				this.uriInfo.getAbsolutePath().toURL(), this.uriInfo);
	}

	/**
	 * Creates a new graph.
	 * 
	 * @param graphValue
	 *            graph-representing object {@link GraphValue}
	 * @return a {@link Reply} containing {@link Message} in {@code payload} attribute and "MESSAGE" in {@code type} attribute
	 * @throws MalformedURLException
	 *             when the combination of the base URL and the provided graph name
	 *             forms invalid URL
	 * @throws IllegalStateException
	 *             when called outside a scope of a request
	 * 
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@StatusCodes({ @ResponseCode(code = 400, condition = "The name is missing or invalid."),
			@ResponseCode(code = 200, condition = "A new graph has been created.") })
	@TypeHint(Reply.class)
	public Response post(final GraphValue graphValue) throws MalformedURLException, IllegalStateException {
		if (graphValue.getName() == null) {
			throw new BadRequestException("The name is missing!");
		}

		final String name = graphValue.getName();

		final URL location;
		try {
			location = eu.odalic.extrarelatable.util.URL.getSubResourceAbsolutePath(this.uriInfo, name);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException("The name is invalid!", e);
		}

		try {
			this.graphService.create(name);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}

		return Message.of("A new graph has been created.").toResponse(Response.Status.CREATED, location, this.uriInfo);
	}

	/**
	 * Deletes a graph and any contained data.
	 * 
	 * @param name
	 *            name of the graph
	 * @return a {@link Reply} containing {@link Message} in {@code payload} attribute and "MESSAGE" in {@code type} attribute
	 * 
	 */
	@DELETE
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	@StatusCodes({ @ResponseCode(code = 400, condition = "The graph does not exist."),
			@ResponseCode(code = 200, condition = "Graph deleted.") })
	@TypeHint(Reply.class)
	public Response delete(final @DocumentationExample("example_dataset__en-us") @PathParam("name") String name) {
		try {
			this.graphService.delete(name);
		} catch (final IllegalArgumentException e) {
			throw new NotFoundException("The graph does not exist!", e);
		}

		return Message.of("Graph deleted.").toResponse(Response.Status.OK, this.uriInfo);
	}

	/**
	 * Makes the content from the table, which his provided as CSV input stream,
	 * part of the background knowledge graph, after parsing it according to the
	 * provided format. Depending on the setting of the boolean flags it either
	 * accepts only the provided declared properties and classes in the meta-data as
	 * context, or it accepts even the collected context from the meta-data, or it
	 * collects the context ex post by querying associated Odalic instance.
	 * 
	 * @param name
	 *            name of the graph
	 * @param input
	 *            input stream used as the source of the CSV data to parse (form multi-part segment named "input")
	 * @param formatValue
	 *            format of the CSV file ({@link FormatValue}) to parse the input stream with (form multi-part segment named "format")
	 * @param metadata
	 *            meta-data ({@link MetadataValue}) accompanying the table (form multi-part segment named "metadata")
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
	 * @return a {@link Reply} containing {@link Message} in {@code payload} attribute and "MESSAGE" in {@code type} attribute
	 * @throws IOException
	 *             whenever I/O exception occurs
	 * 
	 */
	@POST
	@Path("{name}/learnt")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@StatusCodes({ @ResponseCode(code = 400, condition = "No input provided or missing metadata."),
			@ResponseCode(code = 200, condition = "The input has been learnt for the graph.") })
	@TypeHint(Reply.class)
	public Response learn(final @DocumentationExample("example_dataset__en-us") @PathParam("name") String name,
			final @FormDataParam("input") InputStream input,
			final @DocumentationExample(type=@TypeHint(FormatValue.class)) @TypeHint(FormatValue.class) @FormDataParam("format") FormatValue formatValue,
			final @DocumentationExample(type=@TypeHint(Metadata.class)) @TypeHint(Metadata.class) @FormDataParam("metadata") Metadata metadata,
			final @QueryParam("onlyWithProperties") Boolean onlyWithProperties,
			final @QueryParam("collectContext") Boolean contextCollected,
			final @QueryParam("onlyDeclaredAsContext") Boolean onlyDeclaredAsContext,
			final @DocumentationExample(value = "DBpediaLocal", value2 = "GermanDBpedia") @QueryParam("usedContextBases") Set<String> usedBases,
			final @DocumentationExample("DBpediaLocal") @QueryParam("primaryContextBase") String primaryBase)
			throws IOException {
		if (input == null) {
			throw new BadRequestException("No input provided!");
		}

		final Format format;
		if (formatValue == null) {
			format = null;
		} else {
			format = new Format(Charset.forName(formatValue.getCharset()), formatValue.getDelimiter(),
					formatValue.isEmptyLinesIgnored(), formatValue.getQuoteCharacter(),
					formatValue.getEscapeCharacter(), formatValue.getCommentMarker());
		}

		if (metadata == null) {
			throw new BadRequestException("Missing metadata!");
		}

		if (!this.graphService.exists(name)) {
			this.graphService.create(name);
		}

		try {
			this.graphService.learn(name, input, format, metadata,
					onlyWithProperties == null ? true : onlyWithProperties,
					contextCollected == null ? false : contextCollected,
					onlyDeclaredAsContext == null ? true : onlyDeclaredAsContext,
					usedBases == null ? ImmutableSet.of() : usedBases, primaryBase);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}

		return Message.of("The input has been learnt for graph " + name + ".").toResponse(Response.Status.OK,
				this.uriInfo);
	}

	/**
	 * Makes the content from the parsed table part of the background knowledge
	 * graph. Depending on the setting of the boolean flags it either accepts only
	 * the provided declared properties and classes in the meta-data as context, or
	 * it accepts even the collected context from the meta-data, or it collects the
	 * context ex post by querying associated Odalic instance.
	 * 
	 * @param name
	 *            name of the graph
	 * @param parsedTableValue
	 *            parsed table ({@link ParsedTableValue})
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
	 * @return a {@link Reply} containing {@link Message} in {@code payload} attribute and "MESSAGE" in {@code type} attribute
	 * @throws IOException
	 *             whenever I/O exception occurs
	 * 
	 */
	@POST
	@Path("{name}/learnt")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@StatusCodes({ @ResponseCode(code = 400, condition = "No table provided or missing metadata."),
			@ResponseCode(code = 200, condition = "The input has been learnt for the graph.") })
	@TypeHint(Reply.class)
	public Response learn(final @DocumentationExample("example_dataset__en-us") @PathParam("name") String name,
			final ParsedTableValue parsedTableValue, final @QueryParam("onlyWithProperties") Boolean onlyWithProperties,
			final @QueryParam("collectContext") Boolean contextCollected,
			final @QueryParam("onlyDeclaredAsContext") Boolean onlyDeclaredAsContext,
			final @DocumentationExample(value = "DBpediaLocal", value2 = "GermanDBpedia") @QueryParam("usedContextBases") Set<String> usedBases,
			final @DocumentationExample("DBpediaLocal") @QueryParam("primaryContextBase") String primaryBase)
			throws IOException {
		if (parsedTableValue == null) {
			throw new BadRequestException("No table provided!");
		}

		if (parsedTableValue.getMetadata() == null) {
			throw new BadRequestException("Missing metadata!");
		}

		final ParsedTable parsedTable = NestedListsParsedTable.fromRows(parsedTableValue.getHeaders(),
				parsedTableValue.getRows(), parsedTableValue.getMetadata());

		if (!this.graphService.exists(name)) {
			this.graphService.create(name);
		}

		try {
			this.graphService.learn(name, parsedTable, onlyWithProperties == null ? true : onlyWithProperties,
					contextCollected == null ? false : contextCollected,
					onlyDeclaredAsContext == null ? true : onlyDeclaredAsContext,
					usedBases == null ? ImmutableSet.of() : usedBases, primaryBase);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}

		return Message.of("A table has been learnt for graph " + name + ".").toResponse(Response.Status.OK,
				this.uriInfo);
	}

	/**
	 * <p>
	 * Annotates the table provided as CSV input stream after parsing it according
	 * to the provided format. Depending on the setting of the boolean flags it
	 * either accepts only the provided declared properties and classes in meta-data
	 * as context, or it accepts even the collected context from the meta-data, or
	 * it collects the context ex post by querying associated Odalic instance.
	 * </p>
	 * 
	 * <p>
	 * When the {@code learn} is set to {@code true}, the input is also learned.
	 * </p>
	 * 
	 * @param name
	 *            name of the graph
	 * @param input
	 *            input stream used as the source of the CSV data to parse (form multi-part segment named "input")
	 * @param formatValue
	 *            format of the CSV file ({@link FormatValue}) to parse the input stream with (form multi-part segment named "format")
	 * @param metadata
	 *            meta-data ({@link MetadataValue}) accompanying the table (form multi-part segment named "metadata")
	 * @param learn
	 *            whether to also learn the annotated input
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
	 * @return a {@link Reply} containing {@link AnnotationResultValue} in {@code payload} attribute and "DATA" in {@code type} attribute
	 * @throws IOException
	 *             whenever I/O exception occurs
	 * 
	 */
	@POST
	@Path("{name}/annotated")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@StatusCodes({ @ResponseCode(code = 400, condition = "No input provided or missing metadata."),
			@ResponseCode(code = 200, condition = "The input has been annotated.") })
	@TypeHint(Reply.class)
	public Response annotate(final @DocumentationExample("example_dataset__en-us") @PathParam("name") String name,
			final @FormDataParam("input") InputStream input,
			final @DocumentationExample(type=@TypeHint(FormatValue.class)) @TypeHint(FormatValue.class) @FormDataParam("format") FormatValue formatValue,
			final @DocumentationExample(type=@TypeHint(Metadata.class)) @TypeHint(Metadata.class) @FormDataParam("metadata") Metadata metadata,
			final @QueryParam("learn") Boolean learn,
			final @QueryParam("onlyWithProperties") Boolean onlyWithProperties,
			final @QueryParam("collectContext") Boolean contextCollected,
			final @QueryParam("onlyDeclaredAsContext") Boolean onlyDeclaredAsContext,
			final @DocumentationExample(value = "DBpediaLocal", value2 = "GermanDBpedia") @QueryParam("usedContextBases") Set<String> usedBases,
			final @DocumentationExample("DBpediaLocal") @QueryParam("primaryContextBase") String primaryBase)
			throws IOException {
		if (input == null) {
			throw new BadRequestException("No input provided!");
		}

		final Format format;
		if (formatValue == null) {
			format = null;
		} else {
			format = new Format(Charset.forName(formatValue.getCharset()), formatValue.getDelimiter(),
					formatValue.isEmptyLinesIgnored(), formatValue.getQuoteCharacter(),
					formatValue.getEscapeCharacter(), formatValue.getCommentMarker());
		}

		if (metadata == null) {
			throw new BadRequestException("Missing metadata!");
		}

		if (learn != null && learn) {
			learn(name, input, formatValue, metadata, onlyWithProperties, contextCollected, onlyDeclaredAsContext,
					usedBases, primaryBase);
		}

		final AnnotationResult result;
		try {
			result = this.graphService.annotate(name, input, format, metadata,
					contextCollected == null ? false : contextCollected,
					onlyDeclaredAsContext == null ? false : onlyDeclaredAsContext,
					usedBases == null ? ImmutableSet.of() : usedBases, primaryBase);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}

		return Reply.data(Response.Status.OK, result, this.uriInfo).toResponse();
	}

	/**
	 * <p>
	 * Annotates the parsed table. Depending on the setting of the boolean flags it
	 * either accepts only the provided declared properties and classes in the
	 * meta-data as context, or it accepts even the collected context from the
	 * meta-data, or it collects the context ex post by querying associated Odalic
	 * instance.
	 * </p>
	 * 
	 * <p>
	 * When the {@code learn} is set to {@code true}, the input is also learned.
	 * </p>
	 * 
	 * @param name
	 *            name of the used graph
	 * @param parsedTableValue
	 *            parsed table ({@link ParsedTableValue})
	 * @param learn
	 *            whether to also learn the annotated input
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
	 * @return a {@link Reply} containing {@link AnnotationResultValue} in {@code payload} attribute and "DATA" in {@code type} attribute
	 * @throws IOException
	 *             whenever I/O exception occurs
	 *             
	 */
	@POST
	@Path("{name}/annotated")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@StatusCodes({ @ResponseCode(code = 400, condition = "No table provided or missing metadata."),
			@ResponseCode(code = 200, condition = "The table has been annotated.") })
	@TypeHint(Reply.class)
	public Response annotate(final @DocumentationExample("example_dataset__en-us") @PathParam("name") String name,
			final ParsedTableValue parsedTableValue, final @QueryParam("learn") Boolean learn,
			final @QueryParam("onlyWithProperties") Boolean onlyWithProperties,
			final @QueryParam("collectContext") Boolean contextCollected,
			final @QueryParam("onlyDeclaredAsContext") Boolean onlyDeclaredAsContext,
			final @DocumentationExample(value = "DBpediaLocal", value2 = "GermanDBpedia") @QueryParam("usedContextBases") Set<String> usedBases,
			final @DocumentationExample("DBpediaLocal") @QueryParam("primaryContextBase") String primaryBase)
			throws IOException {
		if (parsedTableValue == null) {
			throw new BadRequestException("No table provided!");
		}

		if (parsedTableValue.getMetadata() == null) {
			throw new BadRequestException("Missing metadata!");
		}

		final ParsedTable parsedTable = NestedListsParsedTable.fromRows(parsedTableValue.getHeaders(),
				parsedTableValue.getRows(), parsedTableValue.getMetadata());

		if (learn != null && learn) {
			learn(name, parsedTableValue, onlyWithProperties, contextCollected, onlyDeclaredAsContext, usedBases,
					primaryBase);
		}

		final AnnotationResult result;
		try {
			result = this.graphService.annotate(name, parsedTable, contextCollected == null ? false : contextCollected,
					onlyDeclaredAsContext == null ? false : onlyDeclaredAsContext,
					usedBases == null ? ImmutableSet.of() : usedBases, primaryBase);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}

		return Reply.data(Response.Status.OK, result, this.uriInfo).toResponse();
	}

	/**
	 * Searches for contained properties that match the pattern. For now only
	 * matching of the URI is supported.
	 * 
	 * @param name
	 *            name of the searched graph
	 * @param pattern
	 *            search pattern (conforms to format of Java regex pattern)
	 * @param flags
	 *            Java regex pattern flags
	 * @param limit
	 *            maximum number of returned results
	 * @return a {@link Reply} containing {@link SearchResultValue} in {@code payload} attribute and "DATA" in {@code type} attribute
	 * 
	 */
	@GET
	@Path("{name}/search")
	@Produces(MediaType.APPLICATION_JSON)
	@StatusCodes({ @ResponseCode(code = 400, condition = "No pattern provided."),
			@ResponseCode(code = 200, condition = "The search was executed.") })
	@TypeHint(Reply.class)
	public Response search(final @DocumentationExample("example_dataset__en-us") @PathParam("name") String name,
			final @DocumentationExample("http://dbpedia\\.org/ontology/population.*") @QueryParam("pattern") String pattern,
			final @DocumentationExample("0") @QueryParam("flags") Integer flags,
			final @DocumentationExample("3") @QueryParam("limit") Integer limit) {
		if (pattern == null) {
			throw new BadRequestException("No pattern provided!");
		}

		final SearchResult result;
		try {
			result = this.graphService.search(name, pattern, flags, limit == null ? Integer.MAX_VALUE : limit);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}

		return Reply.data(Status.OK, result, this.uriInfo).toResponse();
	}
}
