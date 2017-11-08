package eu.odalic.extrarelatable.model.bag;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class NumericValue extends AbstractValue {
	private final double figure;

	public static final NumericValue of(double figure) {
		return new NumericValue(figure);
	}
	
	private NumericValue(double figure) {
		this.figure = figure;
	}	
	
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
