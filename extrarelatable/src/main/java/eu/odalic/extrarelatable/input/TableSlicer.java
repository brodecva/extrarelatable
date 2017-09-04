package eu.odalic.extrarelatable.input;

import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;

public interface TableSlicer {
	SlicedTable slice(double threshold, TypedTable table);
}
