package eu.odalic.extrarelatable.model.bag;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

/**
 * Numeric-typed value.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
public final class NumericValue extends NumberLikeValue implements Serializable {

	private static final long serialVersionUID = -25440715468458309L;

	private final double figure;

	public static final NumericValue of(double figure) {
		return new NumericValue(figure);
	}

	private NumericValue(double figure) {
		this.figure = figure;
	}

	@Override
	public double getFigure() {
		return figure;
	}

	@Override
	public String getText() {
		return Double.toString(figure);
	}

	@Override
	public boolean isNumeric() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(figure);
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
		final NumericValue other = (NumericValue) obj;
		if (Double.doubleToLongBits(figure) != Double.doubleToLongBits(other.figure)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NumericValue [figure=" + figure + "]";
	}
}
