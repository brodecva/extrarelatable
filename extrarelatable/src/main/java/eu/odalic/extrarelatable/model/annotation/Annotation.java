package eu.odalic.extrarelatable.model.annotation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;

import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Label;

public final class Annotation {
	private final List<Label> labels;
	private final List<AttributeValuePair> attributeValuePairs;
	
	public Annotation(final List<? extends Label> labels, List<? extends AttributeValuePair> attributeValuePairs) {
		checkNotNull(labels);
		
		this.labels = ImmutableList.copyOf(labels);
		this.attributeValuePairs = ImmutableList.copyOf(attributeValuePairs);
	}

	public List<Label> getLabel() {
		return labels;
	}

	public List<AttributeValuePair> getAttributeValuePair() {
		return attributeValuePairs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeValuePairs == null) ? 0 : attributeValuePairs.hashCode());
		result = prime * result + labels.hashCode();
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
		Annotation other = (Annotation) obj;
		if (attributeValuePairs == null) {
			if (other.attributeValuePairs != null) {
				return false;
			}
		} else if (!attributeValuePairs.equals(other.attributeValuePairs)) {
			return false;
		}
		if (!labels.equals(other.labels)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Annotation [labels=" + labels + ", attributeValuePairs=" + attributeValuePairs + "]";
	}	
}
