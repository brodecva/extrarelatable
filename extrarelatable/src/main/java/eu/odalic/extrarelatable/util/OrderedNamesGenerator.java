package eu.odalic.extrarelatable.util;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class OrderedNamesGenerator implements NamesGenerator {

	private final AtomicLong counter = new AtomicLong();
	
	@Override
	public String generate() {
		return String.valueOf(counter.incrementAndGet());
	}

}
