package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

/**
 * Background knowledge graph groups all the individual present
 * {@link Property}s and facilitates merging of their instances in the learned
 * files ({@link PropertyTree}s) into them.
 * 
 * @author Václav Brodec
 *
 */
public final class BackgroundKnowledgeGraph implements Iterable<Property>, Serializable {

	private static final long serialVersionUID = 5880463102489448045L;

	private final String name;

	private final PropertyTreesMergingStrategy propertyTreesMergingStrategy;
	private final Set<Property> properties;

	BackgroundKnowledgeGraph(final String name, final PropertyTreesMergingStrategy propertyTreesMergingStrategy,
			final Set<Property> properties) {
		checkNotNull(name);
		checkNotNull(propertyTreesMergingStrategy);

		this.name = name;
		this.propertyTreesMergingStrategy = propertyTreesMergingStrategy;
		this.properties = properties;
	}

	/**
	 * Creates a new background knowledge graph.
	 * 
	 * @param name
	 *            name of the graph
	 * @param propertyTreesMergingStrategy
	 *            merging strategy
	 */
	public BackgroundKnowledgeGraph(final String name,
			final PropertyTreesMergingStrategy propertyTreesMergingStrategy) {
		this(name, propertyTreesMergingStrategy, new HashSet<>());
	}

	/**
	 * Creates a new background knowledge graph.
	 * 
	 * @param uuid
	 *            identifying UUID
	 * @param propertyTreesMergingStrategy
	 *            merging strategy
	 */
	public BackgroundKnowledgeGraph(final UUID uuid, PropertyTreesMergingStrategy propertyTreesMergingStrategy) {
		this(uuid.toString(), propertyTreesMergingStrategy);
	}

	/**
	 * Copies the background knowledge graph.
	 * 
	 * @param original
	 *            original graph
	 */
	public BackgroundKnowledgeGraph(final BackgroundKnowledgeGraph original) {
		this.name = original.name;
		this.propertyTreesMergingStrategy = original.propertyTreesMergingStrategy;
		this.properties = original.properties.stream().map(property -> new Property(property))
				.collect(Collectors.toCollection(HashSet::new));
	}

	/**
	 * @return the name of the graph
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the merging strategy
	 */
	public PropertyTreesMergingStrategy getPropertyTreesMergingStrategy() {
		return propertyTreesMergingStrategy;
	}

	/**
	 * Attempts to merge the added tree. If unsuccessful, the tree is simply added.
	 * 
	 * @param propertyTree
	 *            added property tree
	 */
	public synchronized void addPropertyTree(final PropertyTree propertyTree) {
		checkNotNull(propertyTree);

		final Property property = propertyTreesMergingStrategy.merge(propertyTree, properties);
		if (property != null) {
			properties.add(property);
		}
	}

	/**
	 * Executes {@link #addPropertyTree(PropertyTree)} in bulk.
	 * 
	 * @param propertyTrees
	 *            added property trees
	 */
	public synchronized void addPropertyTrees(final Collection<? extends PropertyTree> propertyTrees) {
		checkNotNull(propertyTrees);
		propertyTrees.forEach(propertyTree -> addPropertyTree(propertyTree));
	}

	/**
	 * Allows to add a {@link Property} to the knowledge base implemented by the
	 * background knowledge graph.
	 * 
	 * @param property
	 *            newly present property
	 */
	public synchronized void add(final Property property) {
		checkNotNull(property);

		properties.add(property);
	}

	/**
	 * Executes {@link #add(Property)} in bulk.
	 * 
	 * @param properties
	 *            collection of {@link Property} instances
	 */
	public synchronized void addAll(final Collection<? extends Property> properties) {
		checkNotNull(properties);
		properties.forEach(property -> checkNotNull(property));

		this.properties.addAll(properties);
	}

	/**
	 * @return the set of present properties
	 * 
	 * @see Property
	 */
	public synchronized Set<Property> getProperties() {
		return ImmutableSet.copyOf(properties);
	}

	@Override
	public synchronized Iterator<Property> iterator() {
		return ImmutableSet.copyOf(this.properties).iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		BackgroundKnowledgeGraph other = (BackgroundKnowledgeGraph) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BackgroundKnowledgeGraph [name=" + name + ", propertyTreesMergingStrategy="
				+ propertyTreesMergingStrategy + ", properties=" + properties + "]";
	}
}
