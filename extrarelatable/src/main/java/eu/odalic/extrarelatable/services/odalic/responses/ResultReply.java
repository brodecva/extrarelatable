/**
 *
 */
package eu.odalic.extrarelatable.services.odalic.responses;

import javax.annotation.Nullable;
import javax.ws.rs.core.Response.StatusType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.odalic.extrarelatable.services.odalic.conversions.StatusTypeJsonDeserializer;
import eu.odalic.extrarelatable.services.odalic.values.ResultValue;

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
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "reply")
public final class ResultReply {

	/**
	 * Name of the URI query parameter that hold the optional string sent by a
	 * client that is sent back to it as a part of the response.
	 */
	public static final String STAMP_QUERY_PARAMETER_NAME = "stamp";

	private StatusType status;

	private ReplyType type;

	private ResultValue payload;

	private String stamp;

	/**
	 * @return the payload
	 */
	@XmlElement
	public ResultValue getPayload() {
		return this.payload;
	}

	/**
	 * @return the stamp
	 */
	@XmlElement
	@Nullable
	public String getStamp() {
		return this.stamp;
	}

	/**
	 * @return the status
	 */
	@XmlElement
	@JsonDeserialize(using = StatusTypeJsonDeserializer.class)
	public StatusType getStatus() {
		return this.status;
	}

	/**
	 * @return the type
	 */
	@XmlElement
	public ReplyType getType() {
		return this.type;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}

	public void setType(ReplyType type) {
		this.type = type;
	}

	public void setPayload(ResultValue payload) {
		this.payload = payload;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	@Override
	public String toString() {
		return "AnnotationReply [status=" + status + ", type=" + type + ", payload=" + payload + ", stamp=" + stamp
				+ "]";
	}
}
