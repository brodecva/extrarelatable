package eu.odalic.extrarelatable.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("nameBased")
public class NameBasedUuidGenerator implements UuidGenerator {

	private final NamesGenerator namesGenerator;
	
	@Autowired
	public NameBasedUuidGenerator(final NamesGenerator namesGenerator) {
		checkNotNull(namesGenerator);
		
		this.namesGenerator = namesGenerator;
	}
	
	@Override
	public UUID generate() {
		return UUID.nameUUIDFromBytes(namesGenerator.generate().getBytes());
	}

}