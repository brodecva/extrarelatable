package eu.odalic.extrarelatable.api.rest.conversions;

import java.io.IOException;

import javax.ws.rs.core.Response.StatusType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * <p>
 * Serializer of {@link StatusType} to integer status code.
 * </p>
 * 
 * <p>
 * Adapted from Odalic with permission.
 * </p>
 *
 * @author Václav Brodec
 *
 */
public final class StatusTypeJsonSerializer extends JsonSerializer<StatusType> {

	@Override
	public void serialize(final StatusType value, final JsonGenerator jgen, final SerializerProvider provider)
			throws IOException, JsonProcessingException {
		jgen.writeNumber(value.getStatusCode());
	}

}
