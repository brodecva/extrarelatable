package eu.odalic.extrarelatable.services.odalic.conversions;

import java.io.IOException;
import java.util.NavigableSet;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.collect.ImmutableSortedSet;

import eu.odalic.extrarelatable.services.odalic.values.EntityCandidateValue;

/**
 * A custom JSON deserializer of a navigable set with entity candidates.
 *
 * @author Václav Brodec
 *
 */
public final class EntityCandidateValueNavigableSetDeserializer
    extends JsonDeserializer<NavigableSet<EntityCandidateValue>> {

  @Override
  public NavigableSet<EntityCandidateValue> deserialize(final JsonParser parser,
      final DeserializationContext ctxt) throws IOException, JsonProcessingException {
    final EntityCandidateValue[] array = ctxt.readValue(parser, EntityCandidateValue[].class);

    return ImmutableSortedSet.copyOf(array);
  }
}
