package eu.odalic.extrarelatable.model.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;

import eu.odalic.extrarelatable.model.bag.NumericValue;

public final class Partition {
	private final Map<Integer, NumericValue> cells;

	public Partition(final List<? extends NumericValue> cells) {
		this(Streams.mapWithIndex(cells.stream(), (e, i) -> new NumericCell((int) i, e)).collect(toImmutableMap(e -> e.getRowIndex(), e -> e.getValue())));
	}
	
	public Partition(final Set<? extends NumericCell> cells) {
		checkNotNull(cells);
		
		this.cells = cells.stream().collect(toImmutableMap(k -> k.getRowIndex(), v -> v.getValue()));
	}
	
	public Partition(final Map<? extends Integer, ? extends NumericValue> cells) {
		checkNotNull(cells);
		cells.keySet().stream().forEach(e -> {
			checkNotNull(e);
			checkArgument(e >= 0);
		});

		this.cells = ImmutableMap.copyOf(cells);
	}

	public Map<Integer, NumericValue> getCells() {
		return cells;
	}
	
	public Collection<NumericValue> getValues() {
		return cells.values();
	}
	
	public double[] getDoubleValuesArray() {
		return getValues().stream().mapToDouble(e -> e.getFigure()).toArray();
	}

	public int size() {
		return getValues().size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cells.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Partition other = (Partition) obj;
		if (!cells.equals(other.cells)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Partition [cells=" + cells + "]";
	}
}
