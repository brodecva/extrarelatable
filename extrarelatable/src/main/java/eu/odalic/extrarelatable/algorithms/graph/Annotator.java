package eu.odalic.extrarelatable.algorithms.graph;

import java.util.Map;

import eu.odalic.extrarelatable.model.annotation.Annotation;
import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;
import eu.odalic.extrarelatable.model.table.SlicedTable;

public interface Annotator {
	Map<Integer, Annotation> annotate(BackgroundKnowledgeGraph backgroundKnowledgeGraph, SlicedTable slicedTable, Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			Map<? extends Integer, ? extends DeclaredEntity> declaredClasses,
			Map<? extends Integer, ? extends DeclaredEntity> contextProperties,
			Map<? extends Integer, ? extends DeclaredEntity> contextClasses,
			final boolean onlyDeclaredAsContext);

	Map<Integer, Annotation> annotate(BackgroundKnowledgeGraph graph, SlicedTable slicedTable, Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			Map<? extends Integer, ? extends DeclaredEntity> declaredClasses,
			Map<? extends Integer, ? extends DeclaredEntity> contextProperties,
			Map<? extends Integer, ? extends DeclaredEntity> contextClasses, final boolean onlyDeclaredAsContext, int k);
}
