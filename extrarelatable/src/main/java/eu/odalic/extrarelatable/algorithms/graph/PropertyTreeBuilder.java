package eu.odalic.extrarelatable.algorithms.graph;

import java.util.Map;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;
import eu.odalic.extrarelatable.model.table.SlicedTable;

/**
 * Builder of {@link PropertyTree} from a {@link SlicedTable}.
 * 
 * @author Václav Brodec
 *
 */
public interface PropertyTreeBuilder {

	/**
	 * Builds a {@link PropertyTree} from an indexed numeric column in a
	 * {@link SlicedTable} without considering any additional context.
	 * 
	 * @param slicedTable
	 *            the source table
	 * @param columnIndex
	 *            index of the source column
	 * @return the property tree built from the column
	 */
	PropertyTree build(SlicedTable slicedTable, int columnIndex);

	/**
	 * Builds a {@link PropertyTree} from an indexed numeric column in a
	 * {@link SlicedTable} and the provided context.
	 * 
	 * @param slicedTable
	 *            the source table
	 * @param columnIndex
	 *            index of the source column
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
	 * @return the property tree built from the column
	 */
	PropertyTree build(SlicedTable slicedTable, int columnIndex,
			Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			Map<? extends Integer, ? extends DeclaredEntity> declaredClasses,
			Map<? extends Integer, ? extends DeclaredEntity> contextProperties,
			Map<? extends Integer, ? extends DeclaredEntity> contextClasses, boolean onlyWithProperties,
			boolean onlyDeclaredAsContext);

}