package eu.odalic.extrarelatable.model.bag;

import java.io.Serializable;
import java.time.Instant;

import javax.annotation.concurrent.Immutable;

/**
 * Value representing an instant in time.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
public final class InstantValue extends AbstractValue implements Serializable {
	private static final long serialVersionUID = -8198368132975821586L;
	
	private final Instant instant;

	/**
	 * Creates the value.
	 * 
	 * @param instant instant in time
	 * @return the value
	 */
	public static final InstantValue of(Instant instant) {
		return new InstantValue(instant);
	}
	
	private InstantValue(Instant figure) {
		this.instant = figure;
	}	
	
	/**
	 * @return the encapsualted instant in time
	 */
	public Instant getInstant() {
		return instant;
	}

	@Override
	public String getText() {
		return instant.toString();
	}
	
	@Override
	public double getFigure() {
		return (double) instant.toEpochMilli();
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((instant == null) ? 0 : instant.hashCode());
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
		InstantValue other = (InstantValue) obj;
		if (instant == null) {
			if (other.instant != null) {
				return false;
			}
		} else if (!instant.equals(other.instant)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "InstantValue [instant=" + instant + "]";
	}
}
