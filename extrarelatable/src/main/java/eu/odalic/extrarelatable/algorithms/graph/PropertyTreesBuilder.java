package eu.odalic.extrarelatable.algorithms.graph;

import java.util.Set;

import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.table.SlicedTable;

public interface PropertyTreesBuilder {
	Set<PropertyTree> build(SlicedTable slicedTable);
}
