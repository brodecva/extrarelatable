package eu.odalic.extrarelatable.model.annotation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.graph.Property;

@Immutable
public final class Annotation {
	private final List<Property> properties;
	private final List<Label> labels;
	private final List<Set<AttributeValuePair>> attributeValuePairs;
	
	public Annotation(final List<? extends Property> properties, final List<? extends Label> labels, List<? extends Set<? extends AttributeValuePair>> attributeValuePairs) {
		checkNotNull(properties);
		checkNotNull(labels);
		checkNotNull(attributeValuePairs);
		
		this.properties = ImmutableList.copyOf(properties);
		this.labels = ImmutableList.copyOf(labels);
		this.attributeValuePairs = attributeValuePairs.stream().map(e -> ImmutableSet.<AttributeValuePair>copyOf(e)).collect(ImmutableList.toImmutableList());
	}

	public List<Property> getProperties() {
		return properties;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public List<Set<AttributeValuePair>> getAttributeValuePairs() {
		return attributeValuePairs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + attributeValuePairs.hashCode();
		result = prime * result + labels.hashCode();
		result = prime * result + properties.hashCode();
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
		if (!attributeValuePairs.equals(other.attributeValuePairs)) {
			return false;
		}
		if (!labels.equals(other.labels)) {
			return false;
		}
		if (!properties.equals(other.properties)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Annotation [properties=" + properties + ", labels=" + labels + ", attributeValuePairs="
				+ attributeValuePairs + "]";
	}
}
