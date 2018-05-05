package eu.odalic.extrarelatable.algorithms.graph;

import java.util.Map;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;
import eu.odalic.extrarelatable.model.table.SlicedTable;

public interface PropertyTreeBuilder {

	PropertyTree build(SlicedTable slicedTable, int columnIndex);
	
	PropertyTree build(SlicedTable slicedTable, int columnIndex,
			Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			Map<? extends Integer, ? extends DeclaredEntity> declaredClasses, boolean onlyWithProperties);

}