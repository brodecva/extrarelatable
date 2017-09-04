package eu.odalic.extrarelatable.model.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import eu.odalic.extrarelatable.model.bag.Attribute;
import eu.odalic.extrarelatable.model.bag.TextValue;

public final class Subcontext {

	public static final class Builder {

		private Attribute attribute;
		private Integer columnIndex;
		private ImmutableMultimap.Builder<TextValue, NumericCell> partitionsBuilder = ImmutableMultimap.builder();

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
		
		public Builder put(final TextValue value, final NumericCell cell) {
			checkNotNull(value);
			checkNotNull(cell);

			partitionsBuilder.put(value, cell);

			return this;
		}

		public Subcontext build() {
			final Multimap<TextValue, NumericCell> partitionsEntryMap = partitionsBuilder.build();

			final Map<TextValue, Partition> partitions = partitionsEntryMap.asMap().entrySet().stream().collect(
					ImmutableMap.toImmutableMap(e -> e.getKey(), e -> new Partition((Set<NumericCell>) e.getValue())));

			return new Subcontext(attribute, columnIndex, partitions);
		}
	}

	private final Map<TextValue, Partition> partitions;
	private Attribute attribute;
	private int columnIndex;

	public static Builder builder() {
		return new Builder();
	}

	public Subcontext(final Attribute attribute, final int columnIndex, final Map<? extends TextValue, ? extends Partition> partitions) {
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
	
	public Map<TextValue, Partition> getPartitions() {
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
