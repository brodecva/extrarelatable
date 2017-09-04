package eu.odalic.extrarelatable.input;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.algorithms.types.ColumnTypeAnalyzer;
import eu.odalic.extrarelatable.model.table.NestedListsSlicedTable;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;

public class DefaultTableSlicer implements TableSlicer {

	private final ColumnTypeAnalyzer columnTypeAnalyzer;
	
	public DefaultTableSlicer(final ColumnTypeAnalyzer columnTypeAnalyzer) {
		checkNotNull(columnTypeAnalyzer);
		
		this.columnTypeAnalyzer = columnTypeAnalyzer;
	}

	@Override
	public SlicedTable slice(final double threshold, final TypedTable table) {
		final ImmutableSet.Builder<Integer> numericColumnsIndicesBuilder = ImmutableSet.builder();
		final ImmutableSet.Builder<Integer> textualColumnsIndices = ImmutableSet.builder();
		
		final int width = table.getWidth();
		
		for (int index = 0; index < width; index++) {
			if (columnTypeAnalyzer.isNumeric(index, table) >= threshold) {
				numericColumnsIndicesBuilder.add(index);
			}
			
			if (columnTypeAnalyzer.isTextual(index, table) >= threshold) {
				textualColumnsIndices.add(index);
			}
		}
		
		return NestedListsSlicedTable.of(table, numericColumnsIndicesBuilder.build(), textualColumnsIndices.build());
	}

}
