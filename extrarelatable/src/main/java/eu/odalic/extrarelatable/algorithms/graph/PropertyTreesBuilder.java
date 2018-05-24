package eu.odalic.extrarelatable.algorithms.graph;

import java.util.Map;
import java.util.Set;

import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;

public interface PropertyTreesBuilder {
	Set<PropertyTree> build(SlicedTable slicedTable);

	Set<PropertyTree> build(SlicedTable slicedTable, Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			Map<? extends Integer, ? extends DeclaredEntity> declaredClasses,
			Map<? extends Integer, ? extends DeclaredEntity> contextProperties,
			Map<? extends Integer, ? extends DeclaredEntity> contextClasses, boolean onlyWithProperties, boolean onlyDeclaredAsContext);
}
