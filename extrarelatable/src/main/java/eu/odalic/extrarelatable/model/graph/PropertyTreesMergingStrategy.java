package eu.odalic.extrarelatable.model.graph;

import java.util.Set;

/**
 * Strategy applied to an added tree and the already present properties which
 * attempts to include the tree as an instance of one of the properties. If
 * unsuccessful, a new property with the tree as its first instance is created.
 * 
 * @author Václav Brodec
 *
 */
public interface PropertyTreesMergingStrategy {
	/**
	 * Attempts to merge the tree into one of the properties.
	 * 
	 * @param propertyTree property tree
	 * @param properties existing properties
	 * @return a newly created property holding the tree, {@code null} if merge was successful
	 */
	Property merge(PropertyTree propertyTree, Set<? extends Property> properties);
}
