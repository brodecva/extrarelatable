/**
 *
 */
package eu.odalic.extrarelatable.api.rest.responses;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import com.webcohesion.enunciate.metadata.json.JsonSeeAlso;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import eu.odalic.extrarelatable.api.rest.conversions.StatusTypeJsonSerializer;
import eu.odalic.extrarelatable.api.rest.values.AnnotationResultValue;
import eu.odalic.extrarelatable.api.rest.values.SearchResultValue;
import eu.odalic.extrarelatable.util.URL;

/**
 * <p>
 * A wrapper that either contains the actual data returned by the API
 * implementation or any kind of alternative content, typically a
 * {@link Message}.
 * </p>
 *
 * <p>
 * It helps the receiver to determine the correct processing workflow by
 * providing a type of the payload in the type attribute.
 * </p>
 *
 * <p>
 * Adapted from Odalic with permission.
 * </p>
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "reply")
@JsonSeeAlso({ Message.class, AnnotationResultValue.class, SearchResultValue.class })
public final class Reply {

	/**
	 * Name of the URI query parameter that hold the optional string sent by a
	 * client that is sent back to it as a part of the response.
	 */
	public static final String STAMP_QUERY_PARAMETER_NAME = "stamp";

	@XmlTransient
	public static Reply data(final StatusType status, final Object data, final UriInfo uriInfo) {
		return new Reply(status, ReplyType.DATA, data, URL.getStamp(uriInfo, STAMP_QUERY_PARAMETER_NAME));
	}

	@XmlTransient
	public static Reply message(final StatusType status, final Message message, final UriInfo uriInfo) {
		return new Reply(status, ReplyType.MESSAGE, message, URL.getStamp(uriInfo, STAMP_QUERY_PARAMETER_NAME));
	}

	@XmlTransient
	public static Reply of(final StatusType status, final ReplyType type, final Object payload,
			@Nullable final String stamp) {
		return new Reply(status, type, payload, stamp);
	}

	private final StatusType status;

	private final ReplyType type;

	private final Object payload;

	private final String stamp;

	/**
	 * Creates a REST API response.
	 *
	 * @param status
	 *            HTTP status code
	 * @param type
	 *            response type
	 * @param payload
	 *            payload containing the kind of response indicated by the
	 *            {@link ReplyType}
	 * @param stamp
	 *            a client-set string received in the request that originated this
	 *            reply
	 */
	public Reply(final StatusType status, final ReplyType type, final Object payload, @Nullable final String stamp) {
		Preconditions.checkNotNull(type, "The type cannot be null!");
		Preconditions.checkNotNull(payload, "The payload cannot be null!");

		Preconditions.checkArgument(
				Boolean.logicalXor((type == ReplyType.MESSAGE) && (payload instanceof Message),
						(type != ReplyType.MESSAGE) && !(payload instanceof Message)),
				"Invalid type of reply content!");

		this.status = status;
		this.type = type;
		this.payload = payload;
		this.stamp = stamp;
	}

	/**
	 * @return the payload
	 */
	@XmlElement
	@DocumentationExample("...")
	public Object getPayload() {
		return this.payload;
	}

	/**
	 * @return the stamp
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("102324")
	@TypeHint(String.class)
	public Object getStamp() {
		return this.stamp;
	}

	/**
	 * @return the status
	 */
	@XmlElement
	@JsonSerialize(using = StatusTypeJsonSerializer.class)
	@DocumentationExample("200")
	@TypeHint(Integer.class)
	public StatusType getStatus() {
		return this.status;
	}

	/**
	 * @return the type
	 */
	@XmlElement
	@DocumentationExample("<either DATA or MESSAGE, depending on the type of payload>")
	@TypeHint(String.class)
	public ReplyType getType() {
		return this.type;
	}

	@XmlTransient
	public Response toResponse() {
		return toResponseBuilder().build();
	}

	@XmlTransient
	public ResponseBuilder toResponseBuilder() {
		return Response.status(this.status).entity(this).type(MediaType.APPLICATION_JSON);
	}

	@Override
	public String toString() {
		return "Reply [status=" + this.status + ", type=" + this.type + ", payload=" + this.payload + ", stamp="
				+ this.stamp + "]";
	}
}
