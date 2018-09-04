package eu.odalic.extrarelatable.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component("default")
public class DefaultUuidGenerator implements UuidGenerator {

	@Override
	public UUID generate() {
		return UUID.randomUUID();
	}

}
