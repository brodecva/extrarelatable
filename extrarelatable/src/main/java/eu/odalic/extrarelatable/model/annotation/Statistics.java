package eu.odalic.extrarelatable.model.annotation;

import java.io.Serializable;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import eu.odalic.extrarelatable.api.rest.adapters.StatisticsAdapter;

/**
 * Statistics taken from the algorithm run. Usually meant to be associated each
 * computed annotation. Contains various metrics which can be used to derive a
 * score for each returned annotation.
 * 
 * @author Václav Brodec
 *
 */
@XmlJavaTypeAdapter(StatisticsAdapter.class)
public final class Statistics implements Serializable {
	private static final long serialVersionUID = 3810017751616551396L;

	private final Double average;
	private final Double median;
	private final Integer occurrence;
	private final Double relativeOccurrence;

	/**
	 * Creates the statistics object.
	 * 
	 * @param average
	 *            average distance of the associated annotation
	 * @param median
	 *            median distance of the associated annotation
	 * @param occurrence
	 *            absolute occurrence of the annotation within the top N nodes
	 * @param relativeOccurence
	 *            relative occurrence (ratio of the absolute occurrence and N of the
	 *            top N closest nodes)
	 * @return the statistics object
	 */
	public static Statistics of(final Double average, final Double median, final Integer occurrence,
			final Double relativeOccurence) {
		return new Statistics(average, median, occurrence, relativeOccurence);
	}

	private Statistics() {
		this.average = null;
		this.median = null;
		this.occurrence = null;
		this.relativeOccurrence = null;
	}

	private Statistics(final Double average, final Double median, final Integer occurence,
			final Double relativeOccurence) {
		this.average = average;
		this.median = median;
		this.occurrence = occurence;
		this.relativeOccurrence = relativeOccurence;
	}

	/**
	 * @return average distance of the associated annotation
	 */
	public Double getAverage() {
		return average;
	}

	/**
	 * @return median distance of the associated annotation
	 */
	public Double getMedian() {
		return median;
	}

	/**
	 * @return absolute occurrence of the annotation within the top N closest nodes
	 */
	public Integer getOccurence() {
		return occurrence;
	}

	/**
	 * @return relative occurrence (ratio of the absolute occurrence and number N of
	 *         the top N closest nodes)
	 */
	public Double getRelativeOccurrence() {
		return relativeOccurrence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((average == null) ? 0 : average.hashCode());
		result = prime * result + ((median == null) ? 0 : median.hashCode());
		result = prime * result + ((occurrence == null) ? 0 : occurrence.hashCode());
		result = prime * result + ((relativeOccurrence == null) ? 0 : relativeOccurrence.hashCode());
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
		if (occurrence == null) {
			if (other.occurrence != null) {
				return false;
			}
		} else if (!occurrence.equals(other.occurrence)) {
			return false;
		}
		if (relativeOccurrence == null) {
			if (other.relativeOccurrence != null) {
				return false;
			}
		} else if (!relativeOccurrence.equals(other.relativeOccurrence)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Statistics [average=" + average + ", median=" + median + ", occurrence=" + occurrence
				+ ", relativeOccurrence=" + relativeOccurrence + "]";
	}
}
