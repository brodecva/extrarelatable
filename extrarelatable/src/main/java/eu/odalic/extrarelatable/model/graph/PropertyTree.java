package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

import eu.odalic.extrarelatable.model.bag.AttributeValuePair;
import eu.odalic.extrarelatable.model.bag.Context;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.NumberLikeValue;

public final class PropertyTree implements Iterable<PropertyTree.Node>, Serializable {
	
	private static final long serialVersionUID = 6392176530261226410L;

	public static abstract class Node implements Serializable {
		
		private static final long serialVersionUID = -4572082835480257596L;

		private final Multiset<NumberLikeValue> values;
		
		private final Set<CommonNode> children;
		
		public Node(final Multiset<? extends NumberLikeValue> values) {
			checkNotNull(values);
			
			this.values = ImmutableMultiset.copyOf(values);
			this.children = new HashSet<>();
		}

		public Multiset<NumberLikeValue> getValues() {
			return values;
		}

		public void addChild(final CommonNode child) {
			checkNotNull(child);
			
			child.setParent(this);
			this.children.add(child);
		}
		
		public void addChildren(final Set<? extends CommonNode> children) {
			checkNotNull(children);
			children.forEach(child -> checkNotNull(child));
			
			children.forEach(child -> child.setParent(this));
			this.children.addAll(children);
		}
		 
		public Set<CommonNode> getChildren() {
			return Collections.unmodifiableSet(children);
		}
		
		public abstract Label getLabel();
		
		public abstract AttributeValuePair getPair();
		
		public abstract Set<AttributeValuePair> getPairs();
		
		public abstract Node getParent();

		public abstract PropertyTree getPropertyTree();
		
		public abstract Property getProperty();
	}
	
	public static abstract class CommonNode extends Node implements Serializable {
		
		private static final long serialVersionUID = -1380171855192858385L;
		
		private Node parent;
		
		public CommonNode(final Multiset<? extends NumberLikeValue> values) {
			super(values);
		}

		@Override
		public Label getLabel() {
			return parent.getLabel();
		}
		
		@Override
		public Node getParent() {
			return parent;
		}

		public void setParent(final Node parent) {
			this.parent = parent;
		}
		
		@Override
		public PropertyTree getPropertyTree() {
			return parent.getPropertyTree();
		}
		
		@Override
		public Property getProperty() {
			return parent.getProperty();
		}
	}
	
	public static final class RootNode extends Node implements Serializable {
		
		private static final long serialVersionUID = -4354850307464730896L;

		private final Label label;
		
		private PropertyTree propertyTree;
		
		public RootNode(final Label label, final Multiset<? extends NumberLikeValue> values) {
			super(values);
			
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
		
		@Override
		public Set<AttributeValuePair> getPairs() {
			return ImmutableSet.of();
		}
		
		@Override
		public PropertyTree getPropertyTree() {
			return propertyTree;
		}
		
		public void setPropertyTree(PropertyTree propertyTree) {
			this.propertyTree = propertyTree;
		}

		@Override
		public Node getParent() {
			return null;
		}

		@Override
		public Property getProperty() {
			return propertyTree.getProperty();
		}

		@Override
		public String toString() {
			return "RootNode [label=" + label + "]";
		}
	}
	
	public static final class SharedPairNode extends CommonNode implements Serializable {
		
		private static final long serialVersionUID = 3169369860503260161L;
		
		private final AttributeValuePair pair;
		
		public SharedPairNode(final AttributeValuePair pair, final Multiset<? extends NumberLikeValue> values) {
			super(values);
			
			checkNotNull(pair);
			
			this.pair = pair;
		}

		@Override
		public AttributeValuePair getPair() {
			return pair;
		}

		@Override
		public Set<AttributeValuePair> getPairs() {
			final ImmutableSet.Builder<AttributeValuePair> builder = ImmutableSet.builder();
			
			Node current = this; 
			while (current != null) {
				final AttributeValuePair pair = current.getPair();
				if (pair != null) {
					builder.add(pair);
				}
				
				current = current.getParent();
			}
			
			return builder.build();
		}

		@Override
		public String toString() {
			return "SharedPairNode [pair=" + pair + "]";
		}
	}
	
	private final RootNode root;
	private final Context context;
	private Property property;

	public PropertyTree(final RootNode root, final Context context) {
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
	
	public Property getProperty() {
		return property;
	}

	public void setProperty(final Property property) {
		this.property = property;
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
	public String toString() {
		return "PropertyTree [root=" + root + ", context=" + context + "]";
	}
}
