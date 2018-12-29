package eu.odalic.extrarelatable.algorithms.graph.matching;

import java.util.Collection;
import java.util.SortedSet;

import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.graph.PropertyTree.Node;
import eu.odalic.extrarelatable.model.annotation.MeasuredNode;
import eu.odalic.extrarelatable.model.bag.NumberLikeValue;

/**
 * 
 * 
 * @author Václav Brodec
 *
 */
public interface TopKNodesMatcher {
	/**
	 * Retrieves the best matching nodes from the graph for the input node.
	 * 
	 * @param graph
	 *            the source background knowledge graph
	 * @param matchedNode
	 *            input node for which the best matches are retrieved
	 * @param valuesWeight
	 *            weight of contribution to the overall distance assigned to the
	 *            distance of numeric values
	 * @param propertiesWeight
	 *            weight of contribution to the overall distance assigned to
	 *            distance of property contexts
	 * @param classesWeight
	 *            weight of contribution to the overall distance assigned to
	 *            distance of class contexts
	 * @param k
	 *            the maximum number of returned nodes
	 * @return the best-matching nodes
	 */
	SortedSet<MeasuredNode> match(BackgroundKnowledgeGraph graph, final Node matchedNode, final double valuesWeight,
			double propertiesWeight, double classesWeight, int k);

	/**
	 * Retrieves the best matching nodes from the graph for the input node. Uses
	 * default weights and value of K.
	 * 
	 * @param graph
	 *            the source background knowledge graph
	 * @param matchedNode
	 *            input node for which the best matches are retrieved
	 * @return the best-matching nodes
	 */
	SortedSet<MeasuredNode> match(BackgroundKnowledgeGraph graph, final Node matchedNode);

	/**
	 * Retrieves the best matching nodes from the graph for the input values. Uses
	 * only the distance of the values.
	 * 
	 * @param graph
	 *            the source background knowledge graph
	 * @param values
	 *            values for which the best matches are retrieved
	 * @param k
	 *            the maximum number of returned nodes
	 * @return the best-matching nodes
	 */
	SortedSet<MeasuredNode> match(BackgroundKnowledgeGraph graph, Collection<? extends NumberLikeValue> values, int k);

	/**
	 * Retrieves the best matching nodes from the graph for the input values. Uses
	 * default value of K and only the distance of the values.
	 * 
	 * @param graph
	 *            the source background knowledge graph
	 * @param values
	 *            values for which the best matches are retrieved
	 * @return the best-matching nodes
	 */
	SortedSet<MeasuredNode> match(BackgroundKnowledgeGraph graph, Collection<? extends NumberLikeValue> values);
}
