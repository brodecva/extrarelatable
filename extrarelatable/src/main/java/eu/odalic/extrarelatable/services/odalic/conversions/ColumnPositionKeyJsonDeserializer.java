package eu.odalic.extrarelatable.services.odalic.conversions;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import eu.odalic.extrarelatable.services.odalic.values.ColumnPositionValue;

/**
 * Map key JSON deserializer for {@link ColumnPositionValue} instances.
 *
 * @author Václav Brodec
 *
 */
public final class ColumnPositionKeyJsonDeserializer extends KeyDeserializer {

  @Override
  public Object deserializeKey(final String key, final DeserializationContext ctxt) {
    return new ColumnPositionValue(Integer.parseInt(key));
  }
}
