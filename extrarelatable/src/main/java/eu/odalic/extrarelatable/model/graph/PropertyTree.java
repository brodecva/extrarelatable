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

/**
 * Property tree is an instance of a
 * {@link eu.odalic.extrarelatable.model.graph.Property} in some learned file.
 * It keeps track of all the numeric values in the column annotated with that
 * property, divided into hierarchy of {@link Node}s according to the row
 * context in which they appeared.
 * 
 * @author Václav Brodec
 *
 */
public final class PropertyTree implements Iterable<PropertyTree.Node>, Serializable {

	private static final long serialVersionUID = 6392176530261226410L;

	/**
	 * A node of the tree. Every nodes can hold several numeric values which where
	 * placed into it by the process of partitioning of the original column. It also
	 * has references on its children.
	 * 
	 * @author Václav Brodec
	 *
	 */
	public static abstract class Node implements Serializable {

		private static final long serialVersionUID = -4572082835480257596L;

		private final Multiset<NumberLikeValue> values;

		private final Set<CommonNode> children;

		/**
		 * Initializes the shared attributes of a node.
		 * 
		 * @param values held values
		 */
		public Node(final Multiset<? extends NumberLikeValue> values) {
			checkNotNull(values);

			this.values = ImmutableMultiset.copyOf(values);
			this.children = new HashSet<>();
		}

		/**
		 * @return held values
		 */
		public Multiset<NumberLikeValue> getValues() {
			return values;
		}

		/**
		 * Adds a child to the node.
		 * 
		 * @param child added child node
		 */
		public void addChild(final CommonNode child) {
			checkNotNull(child);

			child.setParent(this);
			this.children.add(child);
		}

		/**
		 * Executes {@link #addChild(CommonNode)} in bulk.
		 * 
		 * @param children added children nodes
		 */
		public void addChildren(final Set<? extends CommonNode> children) {
			checkNotNull(children);
			children.forEach(child -> checkNotNull(child));

			children.forEach(child -> child.setParent(this));
			this.children.addAll(children);
		}

		/**
		 * @return the children nodes
		 */
		public Set<CommonNode> getChildren() {
			return Collections.unmodifiableSet(children);
		}

		/**
		 * @return label originating as the header of the column from which the property tree came from
		 */
		public abstract Label getLabel();

		/**
		 * @return shared attribute-value pair
		 */
		public abstract AttributeValuePair getPair();

		/**
		 * @return shared attribute-value pairs
		 */
		public abstract Set<AttributeValuePair> getPairs();

		/**
		 * @return parental node
		 */
		public abstract Node getParent();

		/**
		 * @return the owning property tree
		 */
		public abstract PropertyTree getPropertyTree();

		/**
		 * @return the property to which the owning property tree belongs to
		 */
		public abstract Property getProperty();
	}

	/**
	 * All common nodes have a parental node.
	 * 
	 * @author Václav Brodec
	 *
	 */
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

	/**
	 * The root node has no parent and keeps reference to the owning property tree and the label coming from the header of the original table column.
	 * 
	 * @author Václav Brodec
	 *
	 */
	public static final class RootNode extends Node implements Serializable {

		private static final long serialVersionUID = -4354850307464730896L;

		private final Label label;

		private PropertyTree propertyTree;

		/**
		 * Creates a root node.
		 * 
		 * @param label property tree label
		 * @param values contained values
		 */
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

	/**
	 * This kind of nodes keeps track of the shared attribute-value pairs.
	 * 
	 * @author Václav Brodec
	 *
	 */
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

	/**
	 * Creates a property tree.
	 * 
	 * @param root root node
	 * @param context property tree context
	 */
	public PropertyTree(final RootNode root, final Context context) {
		checkNotNull(root);
		checkNotNull(context);

		this.root = root;
		this.context = context;
	}

	/**
	 * @return the root node
	 */
	public RootNode getRoot() {
		return root;
	}

	/**
	 * @return the tree context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @return the associated {@link Property}
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @param property sets the property of the tree
	 */
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
