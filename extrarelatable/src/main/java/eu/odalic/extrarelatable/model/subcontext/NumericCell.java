package eu.odalic.extrarelatable.model.subcontext;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.concurrent.Immutable;

import eu.odalic.extrarelatable.model.bag.NumberLikeValue;

@Immutable
public final class NumericCell {
	private final int rowIndex;
	private final NumberLikeValue value;
	
	public NumericCell(final int rowIndex, final NumberLikeValue value) {
		checkArgument(rowIndex >= 0);
		checkNotNull(value);
		
		this.rowIndex = rowIndex;
		this.value = value;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public NumberLikeValue getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rowIndex;
		result = prime * result + value.hashCode();
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
		final NumericCell other = (NumericCell) obj;
		if (rowIndex != other.rowIndex) {
			return false;
		}
		if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NumericCell [rowIndex=" + rowIndex + ", value=" + value + "]";
	}
}
