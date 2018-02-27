package eu.odalic.extrarelatable.api.rest.conversions;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import eu.odalic.extrarelatable.model.graph.Property;

/**
 * Map key JSON serializer for {@link Property} instances.
 *
 * @author Václav Brodec
 *
 */
public final class PropertyKeyJsonSerializer extends JsonSerializer<Property> {

  @Override
  public void serialize(final Property value, final JsonGenerator jgen,
      final SerializerProvider provider) throws IOException, JsonProcessingException {
    jgen.writeFieldName(value.getUri().toString());
  }

}
