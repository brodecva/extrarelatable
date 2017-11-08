package eu.odalic.extrarelatable.algorithms.table;

import eu.odalic.extrarelatable.model.table.TypedTable;

public interface ColumnTypeAnalyzer {
	double isNumeric(int columnIndex, TypedTable table);
	
	double isTextual(int columnIndex, TypedTable table);
	
	double isInstant(int columnIndex, TypedTable table);
	
	double isEntity(int columnIndex, TypedTable table);
	
	double isId(int columnIndex, TypedTable table);
	
	double isUnit(int columnIndex, TypedTable table);
}
