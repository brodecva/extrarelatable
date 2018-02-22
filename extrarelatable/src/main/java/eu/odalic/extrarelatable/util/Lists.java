package eu.odalic.extrarelatable.util;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableMap;

public final class Lists {
	public static final <T> Map<Integer, T> toMap(final List<? extends T> list) {
		return IntStream.range(0, list.size()).mapToObj(i -> Integer.valueOf(i))
				.collect(ImmutableMap.toImmutableMap(i -> i, i -> list.get(i)));
	}
	
	private Lists() {}
}
