package eu.odalic.extrarelatable.algorithms.table;

import java.util.Map;

import eu.odalic.extrarelatable.model.bag.Type;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;

public interface TableSlicer {
	SlicedTable slice(double threshold, TypedTable table);

	SlicedTable slice(double threshold, TypedTable table, Map<? extends Integer, ? extends Type> columnTypeHints);
}
