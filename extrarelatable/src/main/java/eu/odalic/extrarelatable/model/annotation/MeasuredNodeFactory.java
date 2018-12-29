package eu.odalic.extrarelatable.model.annotation;

import eu.odalic.extrarelatable.model.graph.PropertyTree.Node;

/**
 * Factory for the {@link MeasuredNode}s.
 * 
 * @author Václav Brodec
 *
 */
public interface MeasuredNodeFactory {

	/**
	 * Associates the node with the measured distance.
	 * 
	 * @param node node under measurement
	 * @param distance measured distance
	 * @return the measured node
	 */
	MeasuredNode create(Node node, double distance);

}
