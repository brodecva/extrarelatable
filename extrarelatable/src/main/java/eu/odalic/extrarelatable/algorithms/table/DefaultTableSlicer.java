package eu.odalic.extrarelatable.algorithms.table;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.model.bag.Type;
import eu.odalic.extrarelatable.model.table.NestedListsSlicedTable;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;

@Immutable
@Component
public class DefaultTableSlicer implements TableSlicer {

	private final ColumnTypeAnalyzer columnTypeAnalyzer;
	
	public DefaultTableSlicer(final ColumnTypeAnalyzer columnTypeAnalyzer) {
		checkNotNull(columnTypeAnalyzer);
		
		this.columnTypeAnalyzer = columnTypeAnalyzer;
	}

	@Override
	public SlicedTable slice(final double threshold, final TypedTable table) {
		return slice(threshold, table, ImmutableMap.of());
	}
	
	@Override
	public SlicedTable slice(final double threshold, final TypedTable table, final Map<? extends Integer, ? extends Type> columnTypeHints) {
		checkArgument(threshold >= 0);
		checkArgument(threshold <= 1);
		checkNotNull(table);
		checkNotNull(columnTypeHints);
		
		final ImmutableSet.Builder<Integer> dataColumnsIndicesBuilder = ImmutableSet.builder();
		final ImmutableSet.Builder<Integer> contextColumnsIndices = ImmutableSet.builder();
		
		final int width = table.getWidth();
		
		for (int index = 0; index < width; index++) {
			final Type hint = columnTypeHints.get(index);
			if (hint == null) {
				/*if (columnTypeAnalyzer.isId(index, table) >= threshold) {
					contextColumnsIndices.add(index);
				}
				
				if (columnTypeAnalyzer.isInstant(index, table) >= threshold) {
					dataColumnsIndicesBuilder.add(index);
				}*/
				
				if (columnTypeAnalyzer.isNumeric(index, table) >= threshold) {
					dataColumnsIndicesBuilder.add(index);
				}
				
				/*
				if (columnTypeAnalyzer.isEntity(index, table) >= threshold) {
					contextColumnsIndices.add(index);
				}
				
				if (columnTypeAnalyzer.isUnit(index, table) >= threshold) {
					contextColumnsIndices.add(index);
				}*/
				
				if (columnTypeAnalyzer.isTextual(index, table) >= threshold) {
					contextColumnsIndices.add(index);
				}
			} else {
				switch (hint) {
				case DATE:
					dataColumnsIndicesBuilder.add(index);
					break;
				case ENTITY:
					contextColumnsIndices.add(index);
					break;
				case ID:
					contextColumnsIndices.add(index);
					break;
				case NUMERIC:
					dataColumnsIndicesBuilder.add(index);
					break;
				case UNIT:
					contextColumnsIndices.add(index);
					break;
				case TEXT:
					contextColumnsIndices.add(index);
					break;
				}
			}
		}
		
		return NestedListsSlicedTable.of(table, dataColumnsIndicesBuilder.build(), contextColumnsIndices.build());
	}

}
