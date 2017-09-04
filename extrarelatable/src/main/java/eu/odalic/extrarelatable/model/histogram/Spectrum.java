package eu.odalic.extrarelatable.model.histogram;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public final class Spectrum {
	private final Map<Integer, Subcontext> columnsToHistograms;

	public Spectrum(final Map<? extends Integer, ? extends Subcontext> columnsToHistograms) {
		checkNotNull(columnsToHistograms);
		
		this.columnsToHistograms = ImmutableMap.copyOf(columnsToHistograms);
	}

	public Map<Integer, Subcontext> getColumnsToHistograms() {
		return columnsToHistograms;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + columnsToHistograms.hashCode();
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
		final Spectrum other = (Spectrum) obj;
		if (!columnsToHistograms.equals(other.columnsToHistograms)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Spectrum [columnsToHistograms=" + columnsToHistograms + "]";
	}
}
