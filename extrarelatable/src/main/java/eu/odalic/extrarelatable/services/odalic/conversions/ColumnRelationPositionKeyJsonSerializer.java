package eu.odalic.extrarelatable.services.odalic.conversions;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import eu.odalic.extrarelatable.services.odalic.values.ColumnRelationPositionValue;

/**
 * Map key JSON serializer for {@link ColumnRelationPositionValue} instances.
 *
 * @author Václav Brodec
 *
 */
public final class ColumnRelationPositionKeyJsonSerializer extends JsonSerializer<ColumnRelationPositionValue> {

  public static final String DELIMITER = ";";

  @Override
  public void serialize(final ColumnRelationPositionValue value, final JsonGenerator jgen,
      final SerializerProvider provider) throws IOException, JsonProcessingException {
    jgen.writeFieldName(value.getFirst().getIndex() + DELIMITER + value.getSecond().getIndex());
  }

}
