package eu.odalic.extrarelatable.api.rest.values;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.webcohesion.enunciate.metadata.DocumentationExample;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import eu.odalic.extrarelatable.model.annotation.Statistics;

/**
 * Statistics taken from the algorithm run. Usually meant to be associated each
 * computed annotation. Contains various metrics which can be used to derive
 * a score for each returned annotation.
 * 
 * <p>
 * {@link Statistics} adapted for REST API.
 * </p>
 * 
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "statistics")
public final class StatisticsValue implements Serializable {

	private static final long serialVersionUID = -3767853193633012050L;

	private Double average;
	private Double median;
	private Integer occurence;
	private Double relativeOccurence;

	public StatisticsValue() {
	}

	public StatisticsValue(final Statistics adaptee) {
		this.average = adaptee.getAverage();
		this.median = adaptee.getMedian();
		this.occurence = adaptee.getOccurence();
		this.relativeOccurence = adaptee.getRelativeOccurrence();
	}

	/**
	 * the average distance
	 * 
	 * @return the average distance
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("0.323")
	@TypeHint(Double.class)
	public Double getAverage() {
		return average;
	}

	public void setAverage(@Nullable Double average) {
		this.average = average;
	}

	/**
	 * the median distance
	 * 
	 * @return the median distance
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("0.323")
	@TypeHint(Double.class)
	public Double getMedian() {
		return median;
	}

	public void setMedian(@Nullable Double median) {
		this.median = median;
	}

	/**
	 * absolute number of occurrences
	 * 
	 * @return absolute number of occurrences
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("2")
	@TypeHint(Integer.class)
	public Integer getOccurence() {
		return occurence;
	}

	public void setOccurence(@Nullable Integer occurence) {
		this.occurence = occurence;
	}

	/**
	 * ratio of number of occurrences to total of occurrences of all instances
	 * 
	 * @return ratio of number of occurrences to total of occurrences of all instances
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("0.25")
	@TypeHint(Double.class)
	public Double getRelativeOccurence() {
		return relativeOccurence;
	}

	public void setRelativeOccurence(@Nullable Double relativeOccurence) {
		this.relativeOccurence = relativeOccurence;
	}

	@Override
	public String toString() {
		return "StatisticsValue [average=" + average + ", median=" + median + ", occurence=" + occurence
				+ ", relativeOccurence=" + relativeOccurence + "]";
	}
}
