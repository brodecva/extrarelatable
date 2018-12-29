package eu.odalic.extrarelatable.model.subcontext;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import eu.odalic.extrarelatable.model.bag.Attribute;
import eu.odalic.extrarelatable.model.bag.Value;

/**
 * Candidate set of {@link Partition}s for a column.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
public final class Subcontext {

	/**
	 * Subcontext builder.
	 * 
	 * @author Václav Brodec
	 *
	 */
	public static final class Builder {

		private Attribute attribute;
		private Integer columnIndex;
		private ImmutableSetMultimap.Builder<Value, NumericCell> partitionsBuilder = ImmutableSetMultimap.builder();

		/**
		 * @param attribute of the context column associated with the subcontext
		 * @return the builder
		 */
		public Builder setAttribute(final Attribute attribute) {
			checkNotNull(attribute);
			
			this.attribute = attribute;
			
			return this;
		}
		
		/**
		 * @param columnIndex index of the context column associated with the subcontext
		 * @return the builder
		 */
		public Builder setColumnIndex(final int columnIndex) {
			checkArgument(columnIndex >= 0);
			
			this.columnIndex = columnIndex;
			
			return this;
		}
		
		/**
		 * @param value 
		 * @param cell
		 * @return the builder
		 */
		public Builder put(final Value value, final NumericCell cell) {
			checkNotNull(value);
			checkNotNull(cell);

			partitionsBuilder.put(value, cell);

			return this;
		}

		/**
		 * Builds the subcontext.
		 * 
		 * @return the built subcontext
		 */
		public Subcontext build() {
			final ImmutableSetMultimap<Value, NumericCell> partitionsEntryMap = partitionsBuilder.build();

			final Map<Value, Partition> partitions = partitionsEntryMap.asMap().entrySet().stream().collect(
					ImmutableMap.toImmutableMap(e -> e.getKey(), e -> new Partition((Set<NumericCell>) e.getValue())));

			return new Subcontext(attribute, columnIndex, partitions);
		}
	}

	private final Map<Value, Partition> partitions;
	private final Attribute attribute;
	private final int columnIndex;

	/**
	 * @return the subcontext builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Creates a candidate subcontext.
	 * 
	 * @param attribute attribute of the column that is associated with this candidate subcontext
	 * @param columnIndex index of the column associated with this candidate subcontext
	 * @param partitions map of values from the context column to partitions from the partitioned column
	 */
	public Subcontext(final Attribute attribute, final int columnIndex, final Map<? extends Value, ? extends Partition> partitions) {
		checkNotNull(attribute);
		checkArgument(columnIndex >= 0);
		checkNotNull(partitions);
		checkArgument(!partitions.isEmpty());

		this.attribute = attribute;
		this.columnIndex = columnIndex;
		this.partitions = ImmutableMap.copyOf(partitions);
	}

	/**
	 * @return attribute belonging to the column that is associated with this candidate subcontext
	 */
	public Attribute getAttribute() {
		return attribute;
	}

	/**
	 * @return the index of the context column that is associated with this candidate subcontext
	 */
	public int getColumnIndex() {
		return columnIndex;
	}
	
	/**
	 * @return map of context values to the formed partitions
	 */
	public Map<Value, Partition> getPartitions() {
		return partitions;
	}

	/**
	 * @return the size of the smallest partition in the candidate subcontext
	 */
	public int getSmallestPartitionSize() {
		return partitions.values().stream().reduce((p, q) -> {
			if (p.size() <= q.size()) {
				return p;
			} else {
				return q;
			}
		}).get().size();
	}
	
	/**
	 * @return the size of the largest partition in the candidate subcontext
	 */
	public int getLargestPartitionSize() {
		return partitions.values().stream().reduce((p, q) -> {
			if (p.size() >= q.size()) {
				return p;
			} else {
				return q;
			}
		}).get().size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + attribute.hashCode();
		result = prime * result + columnIndex;
		result = prime * result + partitions.hashCode();
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
		final Subcontext other = (Subcontext) obj;
		if (!attribute.equals(other.attribute)) {
			return false;
		}
		if (columnIndex != other.columnIndex) {
			return false;
		}
		if (!partitions.equals(other.partitions)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Subcontext [partitions=" + partitions + ", attribute=" + attribute + ", columnIndex=" + columnIndex
				+ "]";
	}
}
