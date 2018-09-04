package eu.odalic.extrarelatable.model.annotation;

import eu.odalic.extrarelatable.model.graph.PropertyTree.Node;

public interface MeasuredNodeFactory {

	MeasuredNode create(Node node, double distance);

}
