package eu.odalic.extrarelatable.model.annotation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.graph.Property;

@Immutable
public final class Annotation {
	private final List<Property> properties;
	private final List<Label> labels;
	private final List<Set<AttributeValuePair>> attributeValuePairs;

	private final Map<Property, Statistics> propertiesStatistics;
	private final Map<Label, Statistics> labelsStatistics;
	private final Map<Set<AttributeValuePair>, Statistics> pairsStatistics;

	public static Annotation of(final List<? extends Property> properties, final List<? extends Label> labels,
			List<? extends Set<? extends AttributeValuePair>> attributeValuePairs,
			final Map<? extends Property, ? extends Statistics> propertiesStatistics,
			final Map<? extends Label, ? extends Statistics> labelsStatistics,
			final Map<? extends Set<? extends AttributeValuePair>, ? extends Statistics> pairsStatistics) {
		return new Annotation(properties, labels, attributeValuePairs, propertiesStatistics, labelsStatistics, pairsStatistics);
	}
	
	public static Annotation of(final List<? extends Property> properties, final List<? extends Label> labels,
			List<? extends Set<? extends AttributeValuePair>> attributeValuePairs) {
		return new Annotation(properties, labels, attributeValuePairs, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of());
	}
	
	private Annotation(final List<? extends Property> properties, final List<? extends Label> labels,
			List<? extends Set<? extends AttributeValuePair>> attributeValuePairs,
			final Map<? extends Property, ? extends Statistics> propertiesStatistics,
			final Map<? extends Label, ? extends Statistics> labelsStatistics,
			final Map<? extends Set<? extends AttributeValuePair>, ? extends Statistics> pairsStatistics) {
		checkNotNull(properties);
		checkNotNull(labels);
		checkNotNull(attributeValuePairs);

		this.properties = ImmutableList.copyOf(properties);
		this.labels = ImmutableList.copyOf(labels);
		this.attributeValuePairs = attributeValuePairs.stream().map(e -> ImmutableSet.<AttributeValuePair> copyOf(e))
				.collect(ImmutableList.toImmutableList());
		this.propertiesStatistics = ImmutableMap.copyOf(propertiesStatistics);
		this.labelsStatistics = ImmutableMap.copyOf(labelsStatistics);
		this.pairsStatistics = pairsStatistics.entrySet().stream().collect(
			ImmutableMap.toImmutableMap(e -> ImmutableSet.copyOf(e.getKey()), e -> e.getValue())
		);
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

	public Map<Property, Statistics> getPropertiesStatistics() {
		return propertiesStatistics;
	}

	public Map<Label, Statistics> getLabelsStatistics() {
		return labelsStatistics;
	}

	public Map<Set<AttributeValuePair>, Statistics> getPairsStatistics() {
		return pairsStatistics;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeValuePairs == null) ? 0 : attributeValuePairs.hashCode());
		result = prime * result + ((labels == null) ? 0 : labels.hashCode());
		result = prime * result + ((labelsStatistics == null) ? 0 : labelsStatistics.hashCode());
		result = prime * result + ((pairsStatistics == null) ? 0 : pairsStatistics.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((propertiesStatistics == null) ? 0 : propertiesStatistics.hashCode());
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
		if (labels == null) {
			if (other.labels != null) {
				return false;
			}
		} else if (!labels.equals(other.labels)) {
			return false;
		}
		if (labelsStatistics == null) {
			if (other.labelsStatistics != null) {
				return false;
			}
		} else if (!labelsStatistics.equals(other.labelsStatistics)) {
			return false;
		}
		if (pairsStatistics == null) {
			if (other.pairsStatistics != null) {
				return false;
			}
		} else if (!pairsStatistics.equals(other.pairsStatistics)) {
			return false;
		}
		if (properties == null) {
			if (other.properties != null) {
				return false;
			}
		} else if (!properties.equals(other.properties)) {
			return false;
		}
		if (propertiesStatistics == null) {
			if (other.propertiesStatistics != null) {
				return false;
			}
		} else if (!propertiesStatistics.equals(other.propertiesStatistics)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Annotation [properties=" + properties + ", labels=" + labels + ", attributeValuePairs="
				+ attributeValuePairs + ", propertiesStatistics=" + propertiesStatistics + ", labelsStatistics="
				+ labelsStatistics + ", pairsStatistics=" + pairsStatistics + "]";
	}
}
