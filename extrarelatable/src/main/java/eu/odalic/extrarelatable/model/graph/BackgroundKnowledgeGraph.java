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

public final class BackgroundKnowledgeGraph implements Iterable<Property>, Serializable {
	
	private static final long serialVersionUID = 5880463102489448045L;
	
	private final String name;
	
	private final PropertyTreesMergingStrategy propertyTreesMergingStrategy;
	private final Set<Property> properties;
	
	BackgroundKnowledgeGraph(final String name, final PropertyTreesMergingStrategy propertyTreesMergingStrategy, final Set<Property> properties) {
		checkNotNull(name);
		checkNotNull(propertyTreesMergingStrategy);
		
		this.name = name;
		this.propertyTreesMergingStrategy = propertyTreesMergingStrategy;
		this.properties = properties;
	}
	
	public BackgroundKnowledgeGraph(final String name, final PropertyTreesMergingStrategy propertyTreesMergingStrategy) {
		this(name, propertyTreesMergingStrategy, new HashSet<>());
	}
	
	public BackgroundKnowledgeGraph(final PropertyTreesMergingStrategy propertyTreesMergingStrategy) {
		this(UUID.randomUUID().toString(), propertyTreesMergingStrategy);
	}
	
	public BackgroundKnowledgeGraph(final BackgroundKnowledgeGraph original) {
		this.name = original.name;
		this.propertyTreesMergingStrategy = original.propertyTreesMergingStrategy;
		this.properties = original.properties.stream().map(property -> new Property(property)).collect(Collectors.toCollection(HashSet::new));
	}

	public String getName() {
		return name;
	}

	public PropertyTreesMergingStrategy getPropertyTreesMergingStrategy() {
		return propertyTreesMergingStrategy;
	}
	
	public synchronized void addPropertyTree(final PropertyTree propertyTree) {
		checkNotNull(propertyTree);
		
		final Property property = propertyTreesMergingStrategy.find(propertyTree, properties);
		if (property == null) {
			final Property newProperty = new Property();
			newProperty.add(propertyTree);
			newProperty.setUri(propertyTree.getContext().getDeclaredPropertyUri());
			
			properties.add(newProperty);
		} else {
			property.add(propertyTree);
		}
	}
	
	public synchronized void addPropertyTrees(final Collection<? extends PropertyTree> propertyTrees) {
		checkNotNull(propertyTrees);
		propertyTrees.forEach(propertyTree -> addPropertyTree(propertyTree));
	}
	
	public synchronized void add(final Property property) {
		checkNotNull(property);
		
		properties.add(property);
	}
	
	public synchronized  void addAll(final Collection<? extends Property> properties) {
		checkNotNull(properties);
		properties.forEach(property -> checkNotNull(property));
		
		this.properties.addAll(properties);
	}
	
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
