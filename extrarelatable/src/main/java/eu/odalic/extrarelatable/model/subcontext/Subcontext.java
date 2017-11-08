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

@Immutable
public final class Subcontext {

	public static final class Builder {

		private Attribute attribute;
		private Integer columnIndex;
		private ImmutableSetMultimap.Builder<Value, NumericCell> partitionsBuilder = ImmutableSetMultimap.builder();

		public Builder setAttribute(final Attribute attribute) {
			checkNotNull(attribute);
			
			this.attribute = attribute;
			
			return this;
		}
		
		public Builder setColumnIndex(final int columnIndex) {
			checkArgument(columnIndex >= 0);
			
			this.columnIndex = columnIndex;
			
			return this;
		}
		
		public Builder put(final Value value, final NumericCell cell) {
			checkNotNull(value);
			checkNotNull(cell);

			partitionsBuilder.put(value, cell);

			return this;
		}

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

	public static Builder builder() {
		return new Builder();
	}

	public Subcontext(final Attribute attribute, final int columnIndex, final Map<? extends Value, ? extends Partition> partitions) {
		checkNotNull(attribute);
		checkArgument(columnIndex >= 0);
		checkNotNull(partitions);
		checkArgument(!partitions.isEmpty());

		this.attribute = attribute;
		this.columnIndex = columnIndex;
		this.partitions = ImmutableMap.copyOf(partitions);
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public int getColumnIndex() {
		return columnIndex;
	}
	
	public Map<Value, Partition> getPartitions() {
		return partitions;
	}

	public int getSmallestPartitionSize() {
		return partitions.values().stream().reduce((p, q) -> {
			if (p.size() <= q.size()) {
				return p;
			} else {
				return q;
			}
		}).get().size();
	}
	
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
