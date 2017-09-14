package eu.odalic.extrarelatable.model.annotation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.model.bag.Label;

public final class LabelAggregation {
	final Label label;
	
	final Set<MeasuredNode> nodes;

	public LabelAggregation(final Label label, final Set<? extends MeasuredNode> nodes) {
		checkNotNull(label);
		checkNotNull(nodes);
		
		this.label = label;
		this.nodes = ImmutableSet.copyOf(nodes);
	}

	public Label getLabel() {
		return label;
	}

	public Set<MeasuredNode> getNodes() {
		return nodes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + label.hashCode();
		result = prime * result + nodes.hashCode();
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
		final LabelAggregation other = (LabelAggregation) obj;
		if (!label.equals(other.label)) {
			return false;
		}
		if (!nodes.equals(other.nodes)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "LabelAggregation [label=" + label + ", nodes=" + nodes + "]";
	}
}
