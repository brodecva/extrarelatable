package eu.odalic.extrarelatable.util;

import java.util.UUID;

/**
 * Provides UUIDs. The actual implementation of the UUID generation may vary.
 * 
 * @author Václav Brodec
 * 
 * @see UUID
 *
 */
public interface UuidGenerator {
	/**
	 * Generates a new UUID.
	 * 
	 * @return newly generated UUID
	 */
	UUID generate();
}
