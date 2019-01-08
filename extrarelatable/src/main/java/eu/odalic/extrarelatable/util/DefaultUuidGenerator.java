package eu.odalic.extrarelatable.util;

import java.io.Serializable;
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
public class DefaultUuidGenerator implements UuidGenerator, Serializable {

	private static final long serialVersionUID = 7222712883642162828L;

	@Override
	public UUID generate() {
		return UUID.randomUUID();
	}

}
