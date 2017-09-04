package eu.odalic.extrarelatable.model.bag;

import static com.google.common.base.Preconditions.checkNotNull;

public final class AttributeValuePair {
	private final Attribute attribute;
	private final TextValue value;
	
	public AttributeValuePair(Attribute attribute, TextValue value) {
		checkNotNull(attribute);
		checkNotNull(value);
		
		this.attribute = attribute;
		this.value = value;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public TextValue getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + attribute.hashCode();
		result = prime * result + value.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AttributeValuePair other = (AttributeValuePair) obj;
		if (!attribute.equals(other.attribute)) {
			return false;
		}
		if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AttributeValuePair [attribute=" + attribute + ", value=" + value + "]";
	}
}
