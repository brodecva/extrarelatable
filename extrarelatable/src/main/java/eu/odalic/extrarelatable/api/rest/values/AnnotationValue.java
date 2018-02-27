package eu.odalic.extrarelatable.api.rest.values;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.api.rest.conversions.AttributeValuePairSetKeyJsonSerializer;
import eu.odalic.extrarelatable.api.rest.conversions.LabelKeyJsonSerializer;
import eu.odalic.extrarelatable.api.rest.conversions.PropertyKeyJsonSerializer;
import eu.odalic.extrarelatable.model.annotation.Annotation;
import eu.odalic.extrarelatable.model.annotation.Statistics;
import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.graph.Property;

@Immutable
@XmlRootElement(name = "annotation")
public final class AnnotationValue implements Serializable {
	
	private static final long serialVersionUID = -8971845897146434865L;
	
	private List<Property> properties;
	private List<Label> labels;
	private List<Set<AttributeValuePair>> attributeValuePairs;

	private Map<Property, Statistics> propertiesStatistics;
	private Map<Label, Statistics> labelsStatistics;
	private Map<Set<AttributeValuePair>, Statistics> pairsStatistics;

	public AnnotationValue() {
		this.properties = ImmutableList.of();
		this.labels = ImmutableList.of();
		this.attributeValuePairs = ImmutableList.of();
		
		this.propertiesStatistics = ImmutableMap.of();
		this.labelsStatistics = ImmutableMap.of();
		this.pairsStatistics = ImmutableMap.of();
	}
	
	public AnnotationValue(final Annotation annotation) {
		this.properties = annotation.getProperties();
		this.labels = annotation.getLabels();
		this.attributeValuePairs = annotation.getAttributeValuePairs();
		this.propertiesStatistics = annotation.getPropertiesStatistics();
		this.labelsStatistics = annotation.getLabelsStatistics();
		this.pairsStatistics = annotation.getPairsStatistics();
	}

	@XmlElement
	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<? extends Property> properties) {
		this.properties = ImmutableList.copyOf(properties);
	}

	@XmlElement
	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<? extends Label> labels) {
		this.labels = ImmutableList.copyOf(labels);
	}

	@XmlElement
	public List<Set<AttributeValuePair>> getAttributeValuePairs() {
		return attributeValuePairs;
	}

	public void setAttributeValuePairs(List<? extends Set<? extends AttributeValuePair>> attributeValuePairs) {
		this.attributeValuePairs = attributeValuePairs.stream().map(set -> ImmutableSet.copyOf(set)).collect(ImmutableList.toImmutableList());
	}

	@XmlElement
	@JsonSerialize(keyUsing = PropertyKeyJsonSerializer.class)
	public Map<Property, Statistics> getPropertiesStatistics() {
		return propertiesStatistics;
	}

	public void setPropertiesStatistics(Map<? extends Property, ? extends Statistics> propertiesStatistics) {
		this.propertiesStatistics = ImmutableMap.copyOf(propertiesStatistics);
	}

	@XmlElement
	@JsonSerialize(keyUsing = LabelKeyJsonSerializer.class)
	public Map<Label, Statistics> getLabelsStatistics() {
		return labelsStatistics;
	}

	public void setLabelsStatistics(Map<? extends Label, ? extends Statistics> labelsStatistics) {
		this.labelsStatistics = ImmutableMap.copyOf(labelsStatistics);
	}

	@XmlElement
	@JsonSerialize(keyUsing = AttributeValuePairSetKeyJsonSerializer.class)
	public Map<Set<AttributeValuePair>, Statistics> getPairsStatistics() {
		return pairsStatistics;
	}

	public void setPairsStatistics(Map<? extends Set<? extends AttributeValuePair>, ? extends Statistics> pairsStatistics) {
		this.pairsStatistics = pairsStatistics.entrySet().stream().collect(ImmutableMap.toImmutableMap(e -> ImmutableSet.copyOf(e.getKey()), e -> e.getValue()));
	}

	@Override
	public String toString() {
		return "AnnotationValue [properties=" + properties + ", labels=" + labels + ", attributeValuePairs="
				+ attributeValuePairs + ", propertiesStatistics=" + propertiesStatistics + ", labelsStatistics="
				+ labelsStatistics + ", pairsStatistics=" + pairsStatistics + "]";
	}
}
