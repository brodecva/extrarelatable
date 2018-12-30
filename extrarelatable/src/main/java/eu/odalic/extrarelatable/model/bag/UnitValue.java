package eu.odalic.extrarelatable.model.bag;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

/**
 * Unit value (centimeters, kilograms, ...).
 * 
 * @author Václav Brodec
 *
 */
@Immutable
public final class UnitValue extends NumberLikeValue implements Serializable {

	private static final long serialVersionUID = -25440715468458309L;

	private final double figure;
	private final String text;

	public static final UnitValue of(double figure, String text) {
		return new UnitValue(figure, text);
	}

	private UnitValue(double figure, String text) {
		this.figure = figure;
		this.text = text;
	}

	@Override
	public double getFigure() {
		return figure;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public boolean isUnit() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		UnitValue other = (UnitValue) obj;
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "UnitValue [figure=" + figure + ", text=" + text + "]";
	}
}
