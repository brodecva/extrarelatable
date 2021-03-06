package eu.odalic.extrarelatable.api.rest.responses;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * <p>
 * Catch-all throwable mapper.
 * </p>
 * 
 * <p>
 * Adapted from Odalic with permission.
 * </p>
 *
 * @author Václav Brodec
 *
 */
public final class ThrowableMapper implements ExceptionMapper<Throwable> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThrowableMapper.class);

	@Context
	private HttpHeaders headers;

	@Context
	private UriInfo uriInfo;

	/**
	 * Tests the throwable against JSON mapping exception to detect
	 * {@link JsonMappingException} mappable to {@link BadRequestException}.
	 *
	 * Defaults to internal server error in case the exception is not instance of
	 * {@link WebApplicationException}.
	 *
	 * @param throwable
	 *            throwable instance
	 * @return HTTP status
	 */
	private StatusType getHttpStatus(final Throwable throwable) {
		if (throwable instanceof WebApplicationException) {
			return ((WebApplicationException) throwable).getResponse().getStatusInfo();
		} else if (throwable instanceof JsonMappingException) {
			return Status.BAD_REQUEST;
		} else {
			return Response.Status.INTERNAL_SERVER_ERROR;
		}
	}

	@Override
	public Response toResponse(final Throwable throwable) {
		final StatusType statusType = getHttpStatus(throwable);

		final String text = throwable.getMessage();

		LOGGER.warn("Mapping of throwable " + text, throwable);

		final StringWriter trace = new StringWriter();
		throwable.printStackTrace(new PrintWriter(trace));
		final String debugContent = trace.toString();

		final List<MediaType> acceptable = this.headers.getAcceptableMediaTypes();

		// Send the default message only if acceptable by the client.
		if (acceptable.contains(MediaType.WILDCARD_TYPE) || acceptable.contains(MediaType.APPLICATION_JSON_TYPE)) {
			return Message.of(text, debugContent).toResponse(statusType, this.uriInfo);
		} else {
			return Response.status(statusType).type(acceptable.get(0)).build();
		}
	}
}
