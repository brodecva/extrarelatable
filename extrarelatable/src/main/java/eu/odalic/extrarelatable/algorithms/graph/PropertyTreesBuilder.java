package eu.odalic.extrarelatable.algorithms.graph;

import java.util.Map;
import java.util.Set;

import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;

/**
 * Builder of a set of {@link PropertyTree}s from a {@link SlicedTable}.
 * 
 * @author Václav Brodec
 *
 */
public interface PropertyTreesBuilder {
	/**
	 * Builds set of trees from a table without any provided context.
	 * 
	 * @param slicedTable
	 *            table sliced to numeric columns and columns providing row context
	 * @return the set property trees modeling the numeric columns from the table
	 */
	Set<PropertyTree> build(SlicedTable slicedTable);

	/**
	 * Builds set of trees from a table and the provided context.
	 * 
	 * @param slicedTable
	 *            table sliced to numeric columns and columns providing row context
	 * @param declaredProperties
	 *            manually curated properties assigned to the indexed columns
	 * @param declaredClasses
	 *            manually curated classes assigned to the indexed columns
	 * @param contextProperties
	 *            automatically collected properties for the indexed columns
	 * @param contextClasses
	 *            automatically collected classes assigned for the indexed columns
	 * @param onlyWithProperties
	 *            only the columns with manually assigned properties are used to
	 *            build the trees
	 * @param onlyDeclaredAsContext
	 *            only the declared properties and columns are used as additional
	 *            context
	 * @return the set of property trees modeling the numeric columns from the table
	 */
	Set<PropertyTree> build(SlicedTable slicedTable,
			Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			Map<? extends Integer, ? extends DeclaredEntity> declaredClasses,
			Map<? extends Integer, ? extends DeclaredEntity> contextProperties,
			Map<? extends Integer, ? extends DeclaredEntity> contextClasses, boolean onlyWithProperties,
			boolean onlyDeclaredAsContext);
}
