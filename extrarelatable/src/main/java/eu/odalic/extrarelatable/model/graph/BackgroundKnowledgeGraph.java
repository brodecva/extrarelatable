package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public final class BackgroundKnowledgeGraph implements Iterable<Property> {
	
	private final PropertyTreesMergingStrategy propertyTreesMergingStrategy;
	private final Set<Property> properties;
	
	public BackgroundKnowledgeGraph(final PropertyTreesMergingStrategy propertyTreesMergingStrategy) {
		checkNotNull(propertyTreesMergingStrategy);
		
		this.propertyTreesMergingStrategy = propertyTreesMergingStrategy;
		this.properties = new HashSet<>();
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
	public synchronized String toString() {
		return "BackgroundKnowledgeGraph [properties=" + properties + "]";
	}
}
