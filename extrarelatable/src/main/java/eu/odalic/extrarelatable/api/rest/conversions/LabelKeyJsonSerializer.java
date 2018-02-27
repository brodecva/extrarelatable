package eu.odalic.extrarelatable.api.rest.conversions;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import eu.odalic.extrarelatable.model.bag.Label;

/**
 * Map key JSON serializer for {@link Label} instances.
 *
 * @author Václav Brodec
 *
 */
public final class LabelKeyJsonSerializer extends JsonSerializer<Label> {

  @Override
  public void serialize(final Label value, final JsonGenerator jgen,
      final SerializerProvider provider) throws IOException, JsonProcessingException {
    jgen.writeFieldName(value.getText());
  }

}
