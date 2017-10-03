package eu.odalic.extrarelatable.model.bag;

import java.time.Instant;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class InstantValue implements Value {
	private final Instant instant;

	public static final InstantValue of(Instant instant) {
		return new InstantValue(instant);
	}
	
	private InstantValue(Instant figure) {
		this.instant = figure;
	}	
	
	public Instant getinstant() {
		return instant;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isNumeric() {
		return false;
	}

	@Override
	public boolean isTextual() {
		return false;
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
