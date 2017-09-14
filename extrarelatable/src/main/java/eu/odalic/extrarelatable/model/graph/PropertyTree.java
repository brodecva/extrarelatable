package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Context;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.NumericValue;

@Immutable
public final class PropertyTree implements Iterable<PropertyTree.Node> {
	
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
		
		public abstract Label getLabel();
		
		public abstract AttributeValuePair getPair();
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

		@Override
		public Label getLabel() {
			return label;
		}

		@Override
		public AttributeValuePair getPair() {
			return null;
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

		@Override
		public Label getLabel() {
			return null;
		}
		
		@Override
		public AttributeValuePair getPair() {
			return pair;
		}
	}
	
	private final RootNode root;
	private final Context context;

	public PropertyTree(RootNode root, final Context context) {
		checkNotNull(root);
		checkNotNull(context);
		
		this.root = root;
		this.context = context;
	}

	public RootNode getRoot() {
		return root;
	}

	public Context getContext() {
		return context;
	}
	
	@Override
	public Iterator<Node> iterator() {
		return new Iterator<Node>() {

			private final Deque<Node> deque = new LinkedList<>();
			
			{
				deque.push(root);
			}
			
			@Override
			public boolean hasNext() {
				return !deque.isEmpty();
			}

			@Override
			public Node next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				
				final Node next = deque.pop();
				next.getChildren().stream().forEach(e -> deque.push(e));
				
				return next;
			}
			
		};
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((root == null) ? 0 : root.hashCode());
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
		PropertyTree other = (PropertyTree) obj;
		if (context == null) {
			if (other.context != null) {
				return false;
			}
		} else if (!context.equals(other.context)) {
			return false;
		}
		if (root == null) {
			if (other.root != null) {
				return false;
			}
		} else if (!root.equals(other.root)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "PropertyTree [root=" + root + ", context=" + context + "]";
	}
}
