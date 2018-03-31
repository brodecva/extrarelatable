package eu.odalic.extrarelatable.model.graph;

import java.util.Set;

public interface PropertyTreesMergingStrategy {
	Property merge(PropertyTree propertyTree, Set<? extends Property> properties);
}
