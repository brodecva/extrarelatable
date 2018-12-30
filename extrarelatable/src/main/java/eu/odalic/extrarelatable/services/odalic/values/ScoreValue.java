package eu.odalic.extrarelatable.services.odalic.values;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Preconditions;

/**
 * Odalic domain class adapted for REST API.
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "score")
public final class ScoreValue implements Comparable<ScoreValue>, Serializable {

	private static final long serialVersionUID = -901650058091668104L;

	private double value;

	public ScoreValue() {
		this.value = Double.MIN_VALUE;
	}

	/**
	 * @return the value (negative when not set)
	 */
	@XmlElement
	public double getValue() {
		return this.value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final double value) {
		Preconditions.checkArgument(value >= 0, "The score value must be nonnegative!");

		this.value = value;
	}

	@Override
	public int compareTo(final ScoreValue o) {
		return Double.compare(this.value, o.value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		ScoreValue other = (ScoreValue) obj;
		if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ScoreValue [value=" + this.value + "]";
	}
}
