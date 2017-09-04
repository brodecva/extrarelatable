package eu.odalic.extrarelatable.model.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

public final class NumericTriple {
	private final Label label;
	private final Multiset<NumericValue> values;
	private final Context context;
	
	public NumericTriple(final Label label, final Multiset<? extends NumericValue> values, final Context context) {
		checkNotNull(label);
		checkNotNull(values);
		checkNotNull(context);
		
		this.label = label;
		this.values = ImmutableMultiset.copyOf(values);
		this.context = context;
	}

	public Label getLabel() {
		return label;
	}

	public Multiset<NumericValue> getValues() {
		return values;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + context.hashCode();
		result = prime * result + label.hashCode();
		result = prime * result + values.hashCode();
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
		final NumericTriple other = (NumericTriple) obj;
		if (!label.equals(other.label)) {
			return false;
		}
		if (!context.equals(other.context)) {
			return false;
		}
		if (!values.equals(other.values)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NumericTriple [label=" + label + ", values=" + values + ", context=" + context + "]";
	}
}
