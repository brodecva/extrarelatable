package eu.odalic.extrarelatable.model.bag;

import static com.google.common.base.Preconditions.checkArgument;

public final class ColumnHeader {
	private final int index;
	private final String text;

	public ColumnHeader(final int index, final String text) {
		checkArgument(index >=0);
		
		this.index = index;
		this.text = text;
	}

	public int getIndex() {
		return index;
	}

	public String getText() {
		return text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		final ColumnHeader other = (ColumnHeader) obj;
		if (index != other.index) {
			return false;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ColumnHeader [index=" + index + ", text=" + text + "]";
	}
}
