package eu.odalic.extrarelatable.model.bag;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class UnitValue extends AbstractValue {
	private final String unit;

	public static UnitValue of(final String unit) {
		return new UnitValue(unit);
	}
	
	private UnitValue(final String unit) {
		this.unit = unit;
	}	

	@Override
	public String getText() {
		return unit;
	}

	@Override
	public boolean isUnit() {
		return true;
	}
	
	public String getUnit() {
		return unit;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final UnitValue other = (UnitValue) obj;
		if (unit == null) {
			if (other.unit != null) {
				return false;
			}
		} else if (!unit.equals(other.unit)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "UnitValue [unit=" + unit + "]";
	}
}
