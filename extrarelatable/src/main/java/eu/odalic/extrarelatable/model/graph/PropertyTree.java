package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.NumericValue;

@Immutable
public final class PropertyTree {
	
	public static abstract class Node {
		private final Multiset<NumericValue> values;
		private final Set<CommonNode> children;
		
		public Node(final Multiset<? extends NumericValue> values, final Set<? extends CommonNode> children) {
			checkNotNull(values);
			checkNotNull(children);
			
			this.values = ImmutableMultiset.copyOf(values);
			this.children = ImmutableSet.copyOf(children);
		}

		public Multiset<NumericValue> getValues() {
			return values;
		}

		public Set<CommonNode> getChildren() {
			return children;
		}
	}
	
	public static abstract class CommonNode extends Node {
		public CommonNode(final Multiset<? extends NumericValue> values, final Set<? extends CommonNode> children) {
			super(values, children);
		}
	}
	
	@Immutable
	public static final class RootNode extends Node {
		private final Label label;
		
		public RootNode(final Label label, final Multiset<? extends NumericValue> values, final Set<? extends CommonNode> children) {
			super(values, children);
			
			checkNotNull(label);
			
			this.label = label;
		}

		public Label getLabel() {
			return label;
		}
	}
	
	@Immutable
	public static final class SharedPairNode extends CommonNode {
		private final AttributeValuePair pair;
		
		public SharedPairNode(final AttributeValuePair pair, final Multiset<? extends NumericValue> values, final Set<? extends CommonNode> children) {
			super(values, children);
			
			checkNotNull(pair);
			
			this.pair = pair;
		}

		public AttributeValuePair getPair() {
			return pair;
		}
	}
	
	private final RootNode root;

	public PropertyTree(RootNode root) {
		checkNotNull(root);
		
		this.root = root;
	}

	public RootNode getRoot() {
		return root;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + root.hashCode();
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
		final PropertyTree other = (PropertyTree) obj;
		if (!root.equals(other.root)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PropertyTree [root=" + root + "]";
	}
}
