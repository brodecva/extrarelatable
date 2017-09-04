package eu.odalic.extrarelatable.algorithms.types;

import eu.odalic.extrarelatable.model.table.TypedTable;

public interface ColumnTypeAnalyzer {
	double isNumeric(int columnIndex, TypedTable table);
	
	double isTextual(int columnIndex, TypedTable table);
}
