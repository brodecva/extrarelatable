package eu.odalic.extrarelatable.model.graph;

import java.util.Set;

public interface PropertyTreesMergingStrategy {
	Property find(PropertyTree propertyTree, Set<? extends Property> properties);
}
