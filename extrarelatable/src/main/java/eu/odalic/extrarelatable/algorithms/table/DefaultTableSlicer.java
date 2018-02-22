package eu.odalic.extrarelatable.algorithms.table;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.model.bag.Type;
import eu.odalic.extrarelatable.model.table.NestedListsSlicedTable;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;

@Component
public class DefaultTableSlicer implements TableSlicer {

	private static final double RECOMMENDED_DEFAULT_THRESHOLD = 0.6d;
	
	private final ColumnTypeAnalyzer columnTypeAnalyzer;
	
	private final double defaultThreshold;
	
	@Inject
	public DefaultTableSlicer(final ColumnTypeAnalyzer columnTypeAnalyzer,
			@Value("${eu.odalic.extrarelatable.relativeColumnTypeValuesOccurenceThreshold?:0.6}") final double defaultThreshold) {
		checkNotNull(columnTypeAnalyzer);
		checkArgument(defaultThreshold > 0);
		checkArgument(defaultThreshold <= 1);
		
		this.columnTypeAnalyzer = columnTypeAnalyzer;
		this.defaultThreshold = defaultThreshold;
	}
	
	public DefaultTableSlicer(final ColumnTypeAnalyzer columnTypeAnalyzer) {
		this(columnTypeAnalyzer, RECOMMENDED_DEFAULT_THRESHOLD);
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

	public double getDefaultThreshold() {
		return defaultThreshold;
	}

	@Override
	public SlicedTable slice(TypedTable table) {
		return slice(defaultThreshold, table);
	}

	@Override
	public SlicedTable slice(TypedTable table, Map<? extends Integer, ? extends Type> columnTypeHints) {
		return slice(defaultThreshold, table, columnTypeHints);
	}
}
