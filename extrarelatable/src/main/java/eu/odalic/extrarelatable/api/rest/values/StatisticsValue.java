package eu.odalic.extrarelatable.api.rest.values;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.odalic.extrarelatable.model.annotation.Statistics;

@XmlRootElement(name = "statistics")
public final class StatisticsValue {
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
		this.relativeOccurence = adaptee.getRelativeOccurence();
	}

	@XmlElement
	@Nullable
	public Double getAverage() {
		return average;
	}

	public void setAverage(@Nullable Double average) {
		this.average = average;
	}

	@XmlElement
	@Nullable
	public Double getMedian() {
		return median;
	}

	public void setMedian(@Nullable Double median) {
		this.median = median;
	}

	@XmlElement
	@Nullable
	public Integer getOccurence() {
		return occurence;
	}

	public void setOccurence(@Nullable Integer occurence) {
		this.occurence = occurence;
	}

	@XmlElement
	@Nullable
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