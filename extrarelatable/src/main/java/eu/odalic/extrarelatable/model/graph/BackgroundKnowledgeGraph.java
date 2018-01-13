package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class BackgroundKnowledgeGraph implements Iterable<Property> {
	
	private final PropertyTreesMergingStrategy propertyTreesMergingStrategy;
	private final Set<Property> properties;
	
	public BackgroundKnowledgeGraph(final PropertyTreesMergingStrategy propertyTreesMergingStrategy) {
		checkNotNull(propertyTreesMergingStrategy);
		
		this.propertyTreesMergingStrategy = propertyTreesMergingStrategy;
		this.properties = new HashSet<>();
	}

	public void addPropertyTree(final PropertyTree propertyTree) {
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
	
	public void addPropertyTrees(final Collection<? extends PropertyTree> propertyTrees) {
		checkNotNull(propertyTrees);
		propertyTrees.forEach(propertyTree -> addPropertyTree(propertyTree));
	}
	
	public void add(final Property property) {
		checkNotNull(property);
		
		properties.add(property);
	}
	
	public void addAll(final Collection<? extends Property> properties) {
		checkNotNull(properties);
		properties.forEach(property -> checkNotNull(property));
		
		this.properties.addAll(properties);
	}
	
	public Set<Property> getProperties() {
		return Collections.unmodifiableSet(properties);
	}

	@Override
	public Iterator<Property> iterator() {
		return properties.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + properties.hashCode();
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
		final BackgroundKnowledgeGraph other = (BackgroundKnowledgeGraph) obj;
		if (!properties.equals(other.properties)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BackgroundKnowledgeGraph [properties=" + properties + "]";
	}
}
