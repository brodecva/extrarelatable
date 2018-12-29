package eu.odalic.extrarelatable.services.csvengine.csvprofiler;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.collect.ImmutableList;

/**
 * Deserializes the list of outliers from the JSON output of the CSV Profiler.
 * 
 * @author Václav Brodec
 *
 */
public final class OutliersValueListDeserializer
    extends JsonDeserializer<List<Double>> {

  @Override
  public List<Double> deserialize(final JsonParser parser,
      final DeserializationContext ctxt) throws IOException, JsonProcessingException {
    final Double[] array = ctxt.readValue(parser, Double[].class);

    return ImmutableList.copyOf(array);
  }
}