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
//import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataParam;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.odalic.extrarelatable.api.rest.responses.Message;
import eu.odalic.extrarelatable.api.rest.responses.Reply;
import eu.odalic.extrarelatable.api.rest.values.FormatValue;
import eu.odalic.extrarelatable.api.rest.values.GraphValue;
import eu.odalic.extrarelatable.api.rest.values.ParsedTableValue;
import eu.odalic.extrarelatable.model.annotation.AnnotationResult;
import eu.odalic.extrarelatable.model.graph.SearchResult;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.NestedListsParsedTable;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.csv.Format;
import eu.odalic.extrarelatable.services.graph.GraphService;
import jersey.repackaged.com.google.common.collect.ImmutableSet;

/**
 * File resource definition.
 *
 * @author Václav Brodec
 */
@Component
@Path("/graphs")
public final class GraphResource {

	public static final String TEXT_CSV_MEDIA_TYPE = "text/csv";

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(GraphResource.class);

	private final GraphService graphService;

	/*
	 * @Context private SecurityContext securityContext;
	 */

	@Context
	private UriInfo uriInfo;

	@Autowired
	public GraphResource(final GraphService graphService) {
		checkNotNull(graphService, "The graphService cannot be null!");

		this.graphService = graphService;
	}

	@PUT
	@Path("{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response put(final @PathParam("name") String name, final GraphValue graphValue)
			throws MalformedURLException, IllegalStateException, IllegalArgumentException {
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

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(final GraphValue graphValue)
			throws MalformedURLException, IllegalStateException, IllegalArgumentException {
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

	@DELETE
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(final @PathParam("name") String name) {
		try {
			this.graphService.delete(name);
		} catch (final IllegalArgumentException e) {
			throw new NotFoundException("The graph does not exist!", e);
		}

		return Message.of("Graph deleted.").toResponse(Response.Status.OK, this.uriInfo);
	}

	@POST
	@Path("{name}/learnt")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response learn(final @PathParam("name") String name, final @FormDataParam("input") InputStream input,
			final @FormDataParam("format") FormatValue formatValue, final @FormDataParam("metadata") Metadata metadata,
			final @QueryParam("onlyWithProperties") Boolean onlyWithProperties,
			final @QueryParam("collectContext") Boolean contextCollected,
			final @QueryParam("onlyDeclaredAsContext") Boolean onlyDeclaredAsContext,
			final @QueryParam("usedContextBases") Set<String> usedBases,
			final @QueryParam("primaryContextBase") String primaryBase) throws IOException {
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
					onlyWithProperties == null ? true : onlyWithProperties, contextCollected == null ? false : contextCollected, onlyDeclaredAsContext == null ? true : onlyDeclaredAsContext, usedBases == null ? ImmutableSet.of() : usedBases, primaryBase);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}

		return Message.of("An input has been learnt for graph " + name + ".").toResponse(Response.Status.OK,
				this.uriInfo);
	}

	@POST
	@Path("{name}/learnt")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response learn(final @PathParam("name") String name, final ParsedTableValue parsedTableValue,
			final @QueryParam("onlyWithProperties") Boolean onlyWithProperties,
			final @QueryParam("collectContext") Boolean contextCollected,
			final @QueryParam("onlyDeclaredAsContext") Boolean onlyDeclaredAsContext,
			final @QueryParam("usedContextBases") Set<String> usedBases,
			final @QueryParam("primaryContextBase") String primaryBase) throws IOException {
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
			this.graphService.learn(name, parsedTable, onlyWithProperties == null ? true : onlyWithProperties, contextCollected == null ? false : contextCollected, onlyDeclaredAsContext == null ? true : onlyDeclaredAsContext, usedBases == null ? ImmutableSet.of() : usedBases, primaryBase);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}

		return Message.of("A table has been learnt for graph " + name + ".").toResponse(Response.Status.OK,
				this.uriInfo);
	}

	@POST
	@Path("{name}/annotated")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response annotate(final @PathParam("name") String name, final @FormDataParam("input") InputStream input,
			final @FormDataParam("format") FormatValue formatValue, final @FormDataParam("metadata") Metadata metadata,
			final @QueryParam("learn") Boolean learn,
			final @QueryParam("onlyWithProperties") Boolean onlyWithProperties,
			final @QueryParam("collectContext") Boolean contextCollected,
			final @QueryParam("onlyDeclaredAsContext") Boolean onlyDeclaredAsContext,
			final @QueryParam("usedContextBases") Set<String> usedBases,
			final @QueryParam("primaryContextBase") String primaryBase) throws IOException {
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
			learn(name, input, formatValue, metadata, onlyWithProperties, contextCollected, onlyDeclaredAsContext, usedBases, primaryBase);
		}

		final AnnotationResult result;
		try {
			result = this.graphService.annotate(name, input, format, metadata, contextCollected == null ? false : contextCollected, onlyDeclaredAsContext == null ? false : onlyDeclaredAsContext, usedBases == null ? ImmutableSet.of() : usedBases, primaryBase);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}

		return Reply.data(Response.Status.OK, result, this.uriInfo).toResponse();
	}

	@POST
	@Path("{name}/annotated")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response annotate(final @PathParam("name") String name, final ParsedTableValue parsedTableValue,
			final @QueryParam("learn") Boolean learn,
			final @QueryParam("onlyWithProperties") Boolean onlyWithProperties,
			final @QueryParam("collectContext") Boolean contextCollected,
			final @QueryParam("onlyDeclaredAsContext") Boolean onlyDeclaredAsContext,
			final @QueryParam("usedContextBases") Set<String> usedBases,
			final @QueryParam("primaryContextBase") String primaryBase) throws IOException {
		if (parsedTableValue == null) {
			throw new BadRequestException("No table provided!");
		}

		if (parsedTableValue.getMetadata() == null) {
			throw new BadRequestException("Missing metadata!");
		}

		final ParsedTable parsedTable = NestedListsParsedTable.fromRows(parsedTableValue.getHeaders(),
				parsedTableValue.getRows(), parsedTableValue.getMetadata());

		if (learn != null && learn) {
			learn(name, parsedTableValue, onlyWithProperties, contextCollected, onlyDeclaredAsContext, usedBases, primaryBase);
		}
		
		final AnnotationResult result;
		try {
			result = this.graphService.annotate(name, parsedTable, contextCollected == null ? false : contextCollected, onlyDeclaredAsContext == null ? false : onlyDeclaredAsContext, usedBases == null ? ImmutableSet.of() : usedBases, primaryBase);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}

		return Reply.data(Response.Status.OK, result, this.uriInfo).toResponse();
	}

	@GET
	@Path("{name}/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(final @PathParam("name") String name, final @QueryParam("pattern") String pattern,
			final @QueryParam("flags") Integer flags, final @QueryParam("limit") Integer limit) throws IOException {
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
