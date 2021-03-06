package eu.odalic.extrarelatable.api.rest.values;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import eu.odalic.extrarelatable.model.annotation.Annotation;
import eu.odalic.extrarelatable.model.annotation.Statistics;
import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.graph.Property;

/**
 * <p>
 * Annotation of a numeric column and in turn descriptor of the relation it
 * forms with the subject column.
 * </p>
 * 
 * <p>
 * {@link Annotation} adapted for REST API.
 * </p>
 * 
 * @author Václav Brodec
 *
 */
@Immutable
@XmlRootElement(name = "annotation")
public final class AnnotationValue implements Serializable {

	private static final long serialVersionUID = -8971845897146434865L;

	private List<Property> properties;
	private List<Label> labels;
	private List<Set<AttributeValuePair>> attributeValuePairs;

	private List<Statistics> propertiesStatistics;
	private List<Statistics> labelsStatistics;
	private List<Statistics> pairsStatistics;

	public AnnotationValue() {
		this.properties = ImmutableList.of();
		this.labels = ImmutableList.of();
		this.attributeValuePairs = ImmutableList.of();

		this.propertiesStatistics = ImmutableList.of();
		this.labelsStatistics = ImmutableList.of();
		this.pairsStatistics = ImmutableList.of();
	}

	public AnnotationValue(final Annotation annotation) {
		this.properties = annotation.getProperties();
		this.labels = annotation.getLabels();
		this.attributeValuePairs = annotation.getAttributeValuePairs();
		this.propertiesStatistics = this.properties.stream()
				.map(property -> annotation.getPropertiesStatistics().get(property))
				.collect(ImmutableList.toImmutableList());
		this.labelsStatistics = this.labels.stream().map(label -> annotation.getLabelsStatistics().get(label))
				.collect(ImmutableList.toImmutableList());
		this.pairsStatistics = this.attributeValuePairs.stream().map(pair -> annotation.getPairsStatistics().get(pair))
				.collect(ImmutableList.toImmutableList());
	}

	/**
	 * Provides list of assigned {@link PropertyValue}s ordered by fitness
	 * 
	 * @return list of assigned {@link PropertyValue}s ordered by fitness
	 */
	@XmlElement
	@TypeHint(PropertyValue[].class)
	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<? extends Property> properties) {
		this.properties = ImmutableList.copyOf(properties);
	}

	/**
	 * Provides list of assigned {@link LabelValue}s ordered by fitness
	 * 
	 * @return list of assigned {@link LabelValue}s ordered by fitness
	 */
	@XmlElement
	@TypeHint(LabelValue[].class)
	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<? extends Label> labels) {
		this.labels = ImmutableList.copyOf(labels);
	}

	/**
	 * Provides list of assigned {@link AttributeValuePairValue}s ordered by fitness
	 * 
	 * @return list of assigned {@link AttributeValuePairValue}s ordered by fitness
	 */
	@XmlElement
	@TypeHint(AttributeValuePairValue[].class)
	public List<Set<AttributeValuePair>> getAttributeValuePairs() {
		return attributeValuePairs;
	}

	public void setAttributeValuePairs(List<? extends Set<? extends AttributeValuePair>> attributeValuePairs) {
		this.attributeValuePairs = attributeValuePairs.stream().map(set -> ImmutableSet.copyOf(set))
				.collect(ImmutableList.toImmutableList());
	}

	/**
	 * Provides list of {@link StatisticsValue}s associated with the properties.
	 * 
	 * @return list of {@link StatisticsValue}s associated with the properties
	 */
	@XmlElement
	@TypeHint(StatisticsValue[].class)
	public List<Statistics> getPropertiesStatistics() {
		return propertiesStatistics;
	}

	public void setPropertiesStatistics(List<? extends Statistics> propertiesStatistics) {
		this.propertiesStatistics = ImmutableList.copyOf(propertiesStatistics);
	}

	/**
	 * Provides list of {@link StatisticsValue}s associated with the labels.
	 * 
	 * @return list of {@link StatisticsValue}s associated with the labels
	 */
	@XmlElement
	@TypeHint(StatisticsValue[].class)
	public List<Statistics> getLabelsStatistics() {
		return labelsStatistics;
	}

	public void setLabelsStatistics(List<? extends Statistics> labelsStatistics) {
		this.labelsStatistics = ImmutableList.copyOf(labelsStatistics);
	}

	/**
	 * Provides list of {@link StatisticsValue}s associated with the pairs.
	 * 
	 * @return list of {@link StatisticsValue}s associated with the pairs
	 */
	@XmlElement
	@TypeHint(StatisticsValue[].class)
	public List<Statistics> getPairsStatistics() {
		return pairsStatistics;
	}

	public void setPairsStatistics(List<? extends Statistics> pairsStatistics) {
		this.pairsStatistics = ImmutableList.copyOf(pairsStatistics);
	}

	@Override
	public String toString() {
		return "AnnotationValue [properties=" + properties + ", labels=" + labels + ", attributeValuePairs="
				+ attributeValuePairs + ", propertiesStatistics=" + propertiesStatistics + ", labelsStatistics="
				+ labelsStatistics + ", pairsStatistics=" + pairsStatistics + "]";
	}
}
