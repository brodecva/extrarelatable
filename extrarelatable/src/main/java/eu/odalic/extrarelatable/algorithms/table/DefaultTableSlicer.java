package eu.odalic.extrarelatable.algorithms.table;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.model.bag.Type;
import eu.odalic.extrarelatable.model.table.NestedListsSlicedTable;
import eu.odalic.extrarelatable.model.table.SlicedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;

/**
 * Default implementation of {@link TableSlicer}.
 * 
 * @author Václav Brodec
 *
 */
@Component
public class DefaultTableSlicer implements TableSlicer {

	private static final double RECOMMENDED_DEFAULT_THRESHOLD = 0.6d;

	private final ColumnTypeAnalyzer columnTypeAnalyzer;

	private final double defaultThreshold;

	/**
	 * Instantiates the slicer.
	 * 
	 * @param columnTypeAnalyzer
	 *            analyzer of the overall column data types
	 * @param defaultThreshold
	 *            default threshold used to determine the predominant column type
	 *            from the ratio of the cells with the same type within the column
	 */
	@Autowired
	public DefaultTableSlicer(final ColumnTypeAnalyzer columnTypeAnalyzer,
			@Value("${eu.odalic.extrarelatable.relativeColumnTypeValuesOccurenceThreshold:0.6}") final double defaultThreshold) {
		checkNotNull(columnTypeAnalyzer);
		checkArgument(defaultThreshold > 0);
		checkArgument(defaultThreshold <= 1);

		this.columnTypeAnalyzer = columnTypeAnalyzer;
		this.defaultThreshold = defaultThreshold;
	}

	/**
	 * Instantiates the slicer with default threshold.
	 * 
	 * @param columnTypeAnalyzer
	 *            analyzer of the overall column data types
	 */
	public DefaultTableSlicer(final ColumnTypeAnalyzer columnTypeAnalyzer) {
		this(columnTypeAnalyzer, RECOMMENDED_DEFAULT_THRESHOLD);
	}

	@Override
	public SlicedTable slice(final double threshold, final TypedTable table) {
		return slice(threshold, table, ImmutableMap.of());
	}

	@Override
	public SlicedTable slice(final double threshold, final TypedTable table,
			final Map<? extends Integer, ? extends Type> columnTypeHints) {
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
				if (columnTypeAnalyzer.isId(index, table) >= threshold) {
					contextColumnsIndices.add(index);
				} else if (columnTypeAnalyzer.isInstant(index, table) >= threshold) {
					dataColumnsIndicesBuilder.add(index);
				} else if (columnTypeAnalyzer.isNumeric(index, table) >= threshold) {
					dataColumnsIndicesBuilder.add(index);
				} else if (columnTypeAnalyzer.isEntity(index, table) >= threshold) {
					contextColumnsIndices.add(index);
				} else if (columnTypeAnalyzer.isUnit(index, table) >= threshold) {
					dataColumnsIndicesBuilder.add(index);
				} else if (columnTypeAnalyzer.isTextual(index, table) >= threshold) {
					contextColumnsIndices.add(index);
				} else {
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
					dataColumnsIndicesBuilder.add(index);
					break;
				case TEXT:
					contextColumnsIndices.add(index);
					break;
				}
			}
		}

		return NestedListsSlicedTable.of(table, dataColumnsIndicesBuilder.build(), contextColumnsIndices.build());
	}

	/**
	 * @return the default threshold
	 */
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
