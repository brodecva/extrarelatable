package eu.odalic.extrarelatable.model.graph;

import java.util.Set;

public interface PropertyTreesMergingStrategy {
	Property find(final PropertyTree propertyTree, final Set<? extends Property> properties);
}
