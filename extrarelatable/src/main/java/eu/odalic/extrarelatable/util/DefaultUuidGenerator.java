package eu.odalic.extrarelatable.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * UUID generator the generates random UUIDs as returned by
 * {@link UUID#randomUUID()}.
 * 
 * @author Václav Brodec
 * 
 * @see UuidGenerator
 */
@Component("default")
public class DefaultUuidGenerator implements UuidGenerator {

	@Override
	public UUID generate() {
		return UUID.randomUUID();
	}

}
