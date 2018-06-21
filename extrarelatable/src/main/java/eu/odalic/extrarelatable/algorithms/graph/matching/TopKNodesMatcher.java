package eu.odalic.extrarelatable.algorithms.graph.matching;

import java.util.Collection;
import java.util.SortedSet;

import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.graph.PropertyTree.Node;
import eu.odalic.extrarelatable.model.annotation.MeasuredNode;
import eu.odalic.extrarelatable.model.bag.NumericValue;

public interface TopKNodesMatcher {
	SortedSet<MeasuredNode> match(BackgroundKnowledgeGraph graph, final Node matchedNode, final double valuesWeight, double propertiesWeight, double classesWeight, int k);
	
	SortedSet<MeasuredNode> match(BackgroundKnowledgeGraph graph, final Node matchedNode);
	
	SortedSet<MeasuredNode> match(BackgroundKnowledgeGraph graph, Collection<? extends NumericValue> values, int k);
	
	SortedSet<MeasuredNode> match(BackgroundKnowledgeGraph graph, Collection<? extends NumericValue> values);
}
