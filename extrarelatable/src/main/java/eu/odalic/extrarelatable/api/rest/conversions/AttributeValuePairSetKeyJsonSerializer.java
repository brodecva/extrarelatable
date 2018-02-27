package eu.odalic.extrarelatable.api.rest.conversions;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import jersey.repackaged.com.google.common.base.Joiner;

/**
 * Map key JSON serializer for {@link Set<AttributeValuePair>} instances.
 *
 * @author Václav Brodec
 *
 */
public final class AttributeValuePairSetKeyJsonSerializer extends JsonSerializer<Set<? extends AttributeValuePair>> {

  private static final String RESERVED_CHARACTERS_REGEX = "[:|]";
private static final String FIELDS_SEPARATOR = ":";
  private static final String PAIRS_SEPARATOR = "|";

@Override
  public void serialize(final Set<? extends AttributeValuePair> value, final JsonGenerator jgen,
      final SerializerProvider provider) throws IOException, JsonProcessingException {
    jgen.writeFieldName(
    	Joiner.on(PAIRS_SEPARATOR).join(
			value.stream().map(
				pair -> pair.getAttribute().getName().replaceAll(RESERVED_CHARACTERS_REGEX, "") + FIELDS_SEPARATOR +
					pair.getValue().toString().replaceAll(RESERVED_CHARACTERS_REGEX, "")
			).iterator()
    	)
    );
  }

}
