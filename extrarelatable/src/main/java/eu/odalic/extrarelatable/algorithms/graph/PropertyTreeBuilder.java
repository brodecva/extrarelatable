package eu.odalic.extrarelatable.algorithms.graph;

import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.table.SlicedTable;

public interface PropertyTreeBuilder {

	PropertyTree build(SlicedTable slicedTable, int columnIndex);

}