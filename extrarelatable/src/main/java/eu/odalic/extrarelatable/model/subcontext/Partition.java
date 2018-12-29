package eu.odalic.extrarelatable.model.subcontext;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;

import eu.odalic.extrarelatable.model.bag.NumberLikeValue;

/**
 * Collection of number-like cells from a table column associated with their row index.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
public final class Partition {
	private final Map<Integer, NumberLikeValue> cells;

	/**
	 * Creates a partition.
	 * 
	 * @param values number-like values from a single column
	 */
	public Partition(final List<? extends NumberLikeValue> values) {
		this(Streams.mapWithIndex(values.stream(), (e, i) -> new NumericCell((int) i, e)).collect(toImmutableMap(e -> e.getRowIndex(), e -> e.getValue())));
	}
	
	/**
	 * Creates a partition.
	 * 
	 * @param cells cells containing number-like values, from a single column
	 */
	public Partition(final Set<? extends NumericCell> cells) {
		checkNotNull(cells);
		
		this.cells = cells.stream().collect(toImmutableMap(k -> k.getRowIndex(), v -> v.getValue()));
	}
	
	/**
	 * Creates a partition.
	 * 
	 * @param rowsIndicesToValues map of row indices to values, from a single column 
	 */
	public Partition(final Map<? extends Integer, ? extends NumberLikeValue> rowsIndicesToValues) {
		checkNotNull(rowsIndicesToValues);
		rowsIndicesToValues.keySet().stream().forEach(e -> {
			checkNotNull(e);
			checkArgument(e >= 0);
		});

		this.cells = ImmutableMap.copyOf(rowsIndicesToValues);
	}

	/**
	 * Provides partition cells represented by a map of row indices to the values contained in them.
	 * 
	 * @return map of indices to cells
	 */
	public Map<Integer, NumberLikeValue> getCells() {
		return cells;
	}
	
	/**
	 * @return all the number-like values in a partition
	 */
	public Collection<NumberLikeValue> getValues() {
		return cells.values();
	}
	
	/**
	 * @return all the number-like values in a partition in double representation
	 */
	public double[] getDoubleValuesArray() {
		return getValues().stream().mapToDouble(e -> e.getFigure()).toArray();
	}

	/**
	 * @return size of the partition (determined by the number of captured number-like values)
	 */
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
