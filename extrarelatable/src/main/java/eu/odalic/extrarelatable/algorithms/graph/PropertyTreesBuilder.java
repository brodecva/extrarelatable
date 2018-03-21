package eu.odalic.extrarelatable.algorithms.graph;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.table.SlicedTable;

public interface PropertyTreesBuilder {
	Set<PropertyTree> build(SlicedTable slicedTable);

	Set<PropertyTree> build(SlicedTable slicedTable, Map<? extends Integer, ? extends URI> declaredPropertyUris, boolean onlyWithProperties);
}
