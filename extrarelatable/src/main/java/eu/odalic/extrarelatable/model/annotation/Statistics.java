package eu.odalic.extrarelatable.model.annotation;

import java.io.Serializable;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import eu.odalic.extrarelatable.api.rest.adapters.StatisticsAdapter;

@XmlJavaTypeAdapter(StatisticsAdapter.class)
public final class Statistics implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3810017751616551396L;
	
	private final Double average;
	private final Double median;
	private final Integer occurence;
	private final Double relativeOccurence;
	
	public static Statistics of(final Double average, final Double median, final Integer occurence, final Double relativeOccurence) {
		return new Statistics(average, median, occurence, relativeOccurence);
	}
	
	private Statistics() {
		this.average = null;
		this.median = null;
		this.occurence = null;
		this.relativeOccurence = null;
	}
	
	private Statistics(final Double average, final Double median, final Integer occurence, final Double relativeOccurence) {
		this.average = average;
		this.median = median;
		this.occurence = occurence;
		this.relativeOccurence = relativeOccurence;
	}

	public Double getAverage() {
		return average;
	}

	public Double getMedian() {
		return median;
	}

	public Integer getOccurence() {
		return occurence;
	}

	public Double getRelativeOccurence() {
		return relativeOccurence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((average == null) ? 0 : average.hashCode());
		result = prime * result + ((median == null) ? 0 : median.hashCode());
		result = prime * result + ((occurence == null) ? 0 : occurence.hashCode());
		result = prime * result + ((relativeOccurence == null) ? 0 : relativeOccurence.hashCode());
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
		Statistics other = (Statistics) obj;
		if (average == null) {
			if (other.average != null) {
				return false;
			}
		} else if (!average.equals(other.average)) {
			return false;
		}
		if (median == null) {
			if (other.median != null) {
				return false;
			}
		} else if (!median.equals(other.median)) {
			return false;
		}
		if (occurence == null) {
			if (other.occurence != null) {
				return false;
			}
		} else if (!occurence.equals(other.occurence)) {
			return false;
		}
		if (relativeOccurence == null) {
			if (other.relativeOccurence != null) {
				return false;
			}
		} else if (!relativeOccurence.equals(other.relativeOccurence)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Statistics [average=" + average + ", median=" + median + ", occurence=" + occurence
				+ ", relativeOccurence=" + relativeOccurence + "]";
	}
}
