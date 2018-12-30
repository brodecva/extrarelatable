package eu.odalic.extrarelatable.services.odalic.conversions;

import java.util.List;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.google.common.base.Splitter;

import eu.odalic.extrarelatable.services.odalic.values.ColumnRelationPositionValue;
import jersey.repackaged.com.google.common.base.Preconditions;

/**
 * Map key JSON deserializer for {@link ColumnRelationPositionValue} instances.
 *
 * @author Václav Brodec
 *
 */
public final class ColumnRelationPositionKeyJsonDeserializer extends KeyDeserializer {

	private static final int NUMBER_OF_INDICES = 2;

	@Override
	public Object deserializeKey(final String key, final DeserializationContext ctxt) {
		final List<String> indices = Splitter.on(ColumnRelationPositionKeyJsonSerializer.DELIMITER).splitToList(key);
		Preconditions.checkArgument(indices.size() == NUMBER_OF_INDICES,
				"Invalid column relation position key format!");

		try {
			return new ColumnRelationPositionValue(Integer.valueOf(indices.get(0)), Integer.valueOf(indices.get(1)));
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException("Invalid column relation position key format!", e);
		}
	}
}
