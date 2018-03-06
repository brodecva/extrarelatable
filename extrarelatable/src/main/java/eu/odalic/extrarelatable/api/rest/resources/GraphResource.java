package eu.odalic.extrarelatable.api.rest.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import eu.odalic.extrarelatable.api.rest.responses.Message;
import eu.odalic.extrarelatable.api.rest.responses.Reply;
import eu.odalic.extrarelatable.api.rest.values.FormatValue;
import eu.odalic.extrarelatable.api.rest.values.GraphValue;
import eu.odalic.extrarelatable.model.annotation.AnnotationResult;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.csv.Format;
import eu.odalic.extrarelatable.services.GraphService;

/**
 * File resource definition.
 *
 * @author Václav Brodec
 */
@Component
@Path("/graphs")
public final class GraphResource {

	public static final String TEXT_CSV_MEDIA_TYPE = "text/csv";

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphResource.class);

	private final GraphService graphService;

	@Context
	private SecurityContext securityContext;

	@Context
	private UriInfo uriInfo;

	@Autowired
	public GraphResource(final GraphService graphService) {
		Preconditions.checkNotNull(graphService, "The graphService cannot be null!");

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
	public Response learn(final @PathParam("name") String name,
			final @FormDataParam("input") InputStream input,
			final @FormDataParam("format") FormatValue formatValue, final @FormDataParam("metadata") Metadata metadata)
			throws IOException {
		if (input == null) {
			throw new BadRequestException("No input provided!");
		}

		final Format format;
		if (formatValue == null) {
			format = null;
		} else {
			format = new Format(Charset.forName(formatValue.getCharset()), formatValue.getDelimiter(), formatValue.isEmptyLinesIgnored(),
					formatValue.getQuoteCharacter(), formatValue.getEscapeCharacter(), formatValue.getCommentMarker());
		}
		
		if (metadata == null) {
			throw new BadRequestException("Missing metadata!");
		}

		if (!this.graphService.exists(name)) {
			this.graphService.create(name);
		}
		
		try {
			this.graphService.learn(name, input, format, metadata);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}
		
		return Message.of("An input has been learnt for graph " + name + ".")
				.toResponse(Response.Status.OK, this.uriInfo);
	}
	
	@POST
	@Path("{name}/learnt")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response learn(final @PathParam("name") String name, final ParsedTable parsedTable)
			throws IOException {
		if (parsedTable == null) {
			throw new BadRequestException("No table provided!");
		}
		
		if (parsedTable.getMetadata() == null) {
			throw new BadRequestException("Missing metadata!");
		}

		if (!this.graphService.exists(name)) {
			this.graphService.create(name);
		}
		
		try {
			this.graphService.learn(name, parsedTable);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}
		
		return Message.of("A table has been learnt for graph " + name + ".")
				.toResponse(Response.Status.OK, this.uriInfo);
	}
	
	@POST
	@Path("{name}/annotated")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response annotate(final @PathParam("name") String name,
			final @FormDataParam("input") InputStream input,
			final @FormDataParam("format") FormatValue formatValue, final @FormDataParam("metadata") Metadata metadata,
			final @QueryParam("learn") Boolean learn)
			throws IOException {
		if (input == null) {
			throw new BadRequestException("No input provided!");
		}

		final Format format;
		if (formatValue == null) {
			format = null;
		} else {
			format = new Format(Charset.forName(formatValue.getCharset()), formatValue.getDelimiter(), formatValue.isEmptyLinesIgnored(),
					formatValue.getQuoteCharacter(), formatValue.getEscapeCharacter(), formatValue.getCommentMarker());
		}
		
		if (metadata == null) {
			throw new BadRequestException("Missing metadata!");
		}

		final AnnotationResult result;
		try {
			result = this.graphService.annotate(name, input, format, metadata);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}
		
		if (learn != null && learn) {
			learn(name, input, formatValue, metadata);
		}
		
		return Reply.data(Response.Status.OK, result, this.uriInfo).toResponse();
	}
	
	@POST
	@Path("{name}/annotated")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response annotate(final @PathParam("name") String name, final ParsedTable parsedTable,
			final @QueryParam("learn") Boolean learn) throws IOException {
		if (parsedTable == null) {
			throw new BadRequestException("No table provided!");
		}
		
		if (parsedTable.getMetadata() == null) {
			throw new BadRequestException("Missing metadata!");
		}

		final AnnotationResult result;
		try {
			result = this.graphService.annotate(name, parsedTable);
		} catch (final IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage(), e);
		}
		
		if (learn != null && learn) {
			learn(name, parsedTable);
		}
		
		return Reply.data(Response.Status.OK, result, this.uriInfo).toResponse();
	}
}
