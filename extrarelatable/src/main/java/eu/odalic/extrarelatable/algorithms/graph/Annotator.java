package eu.odalic.extrarelatable.algorithms.graph;

import java.util.Map;

import eu.odalic.extrarelatable.model.annotation.Annotation;
import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;
import eu.odalic.extrarelatable.model.table.SlicedTable;

/**
 * Annotates numeric columns from input {@link SlicedTable}s, using also
 * provided context.
 * 
 * @author Václav Brodec
 *
 */
public interface Annotator {
	/**
	 * Annotates numeric columns from the input {@link SlicedTable}, using the
	 * provided context.
	 * 
	 * @param graph
	 *            background knowledge graph serving as the basis for the
	 *            annotations
	 * @param slicedTable
	 *            the input table
	 * @param declaredProperties
	 *            manually curated properties assigned to the indexed columns
	 * @param declaredClasses
	 *            manually curated classes assigned to the indexed columns
	 * @param contextProperties
	 *            automatically collected properties for the indexed columns
	 * @param contextClasses
	 *            automatically collected classes assigned for the indexed columns
	 * @param onlyDeclaredAsContext
	 *            only the declared properties and columns are used as additional
	 *            context
	 * @return the map of indices of numeric columns to annotations
	 */
	Map<Integer, Annotation> annotate(BackgroundKnowledgeGraph graph, SlicedTable slicedTable,
			Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			Map<? extends Integer, ? extends DeclaredEntity> declaredClasses,
			Map<? extends Integer, ? extends DeclaredEntity> contextProperties,
			Map<? extends Integer, ? extends DeclaredEntity> contextClasses, final boolean onlyDeclaredAsContext);

	/**
	 * Annotates numeric columns from the input {@link SlicedTable}, using the
	 * provided context.
	 * 
	 * @param graph
	 *            background knowledge graph serving as the basis for the
	 *            annotations
	 * @param slicedTable
	 *            the input table
	 * @param declaredProperties
	 *            manually curated properties assigned to the indexed columns
	 * @param declaredClasses
	 *            manually curated classes assigned to the indexed columns
	 * @param contextProperties
	 *            automatically collected properties for the indexed columns
	 * @param contextClasses
	 *            automatically collected classes assigned for the indexed columns
	 * @param onlyDeclaredAsContext
	 *            only the declared properties and columns are used as additional
	 *            context
	 * @param k
	 *            the maximum number of the top properties, labels or other parts of
	 *            each annotation returned in the result
	 * @return the map of indices of numeric columns to annotations
	 */
	Map<Integer, Annotation> annotate(BackgroundKnowledgeGraph graph, SlicedTable slicedTable,
			Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			Map<? extends Integer, ? extends DeclaredEntity> declaredClasses,
			Map<? extends Integer, ? extends DeclaredEntity> contextProperties,
			Map<? extends Integer, ? extends DeclaredEntity> contextClasses, final boolean onlyDeclaredAsContext,
			int k);
}
