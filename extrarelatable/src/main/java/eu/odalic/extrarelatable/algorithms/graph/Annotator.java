package eu.odalic.extrarelatable.algorithms.graph;

import java.util.Map;

import eu.odalic.extrarelatable.model.annotation.Annotation;
import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.table.SlicedTable;

public interface Annotator {
	Map<Integer, Annotation> annotate(BackgroundKnowledgeGraph backgroundKnowledgeGraph, SlicedTable slicedTable);

	Map<Integer, Annotation> annotate(BackgroundKnowledgeGraph graph, SlicedTable slicedTable, int k);
}
