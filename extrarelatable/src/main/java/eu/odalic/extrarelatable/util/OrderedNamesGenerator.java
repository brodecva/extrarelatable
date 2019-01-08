package eu.odalic.extrarelatable.util;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

/**
 * Deterministic generator of unique strings.
 * 
 * @author Václav Brodec
 *
 * @see NamesGenerator
 */
@Service
public class OrderedNamesGenerator implements NamesGenerator, Serializable {

	private static final long serialVersionUID = 5643820338716484507L;
	
	private final AtomicLong counter = new AtomicLong();

	@Override
	public String generate() {
		return String.valueOf(counter.incrementAndGet());
	}

}
