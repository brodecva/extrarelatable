package eu.odalic.extrarelatable.algorithms.graph;

import java.util.Map;
import java.net.URI;

import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.table.SlicedTable;

public interface PropertyTreeBuilder {

	PropertyTree build(SlicedTable slicedTable, int columnIndex);
	
	PropertyTree build(SlicedTable slicedTable, int columnIndex,
			Map<? extends Integer, ? extends URI> declaredPropertyUris,
			Map<? extends Integer, ? extends URI> declaredClassUris, boolean onlyWithProperties);

}