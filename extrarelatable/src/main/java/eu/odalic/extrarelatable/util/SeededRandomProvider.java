package eu.odalic.extrarelatable.util;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SeededRandomProvider implements RandomProvider {

	private final Random random;
	
	@Autowired
	public SeededRandomProvider(@Value("${eu.odalic.extrarelatable.seed}") final Long seed) {
		if (seed == null) {
			this.random = new Random(System.currentTimeMillis());
		} else {
			this.random = new Random(seed);
		}
	}
	
	@Override
	public Random getRandom() {
		return this.random;
	}
}
