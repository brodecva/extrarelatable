package eu.odalic.extrarelatable.model.table;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.Value;

public final class NestedListsSlicedTable implements SlicedTable {
	
	private final TypedTable typedTable;
	private final Map<Integer, List<Value>> numericColumns;
	private final Map<Integer, List<Value>> textualColumns;
	
	public static SlicedTable of(final TypedTable typedTable, Set<? extends Integer> numericColumnsIndices,
			Set<? extends Integer> textualColumnsIndices) {
		checkNotNull(typedTable);
		checkNotNull(numericColumnsIndices);
		checkNotNull(textualColumnsIndices);
		
		return new NestedListsSlicedTable(
			typedTable,
			numericColumnsIndices.stream().collect(ImmutableMap.toImmutableMap(Function.identity(), e -> ImmutableList.copyOf(typedTable.getColumn(e)))),
			textualColumnsIndices.stream().collect(ImmutableMap.toImmutableMap(e -> e, e -> ImmutableList.copyOf(typedTable.getColumn(e))))
		);
	}
	
	private NestedListsSlicedTable(final TypedTable typedTable, Map<Integer, List<Value>> numericColumns, Map<Integer, List<Value>> textualColumns) {
		assert typedTable != null;
		assert numericColumns != null;
		assert textualColumns != null;
		
		this.typedTable = typedTable;
		this.numericColumns = numericColumns;
		this.textualColumns = textualColumns;
	}
	
	public List<Label> getHeaders() {
		return typedTable.getHeaders();
	}
	
	public List<List<Value>> getRows() {
		return typedTable.getRows();
	}
	
	public List<List<Value>> getColumns() {
		return typedTable.getColumns();
	}
	
	public Metadata getMetadata() {
		return typedTable.getMetadata();
	}

	public int getWidth() {
		return typedTable.getWidth();
	}
	
	public int getHeight() {
		return typedTable.getHeight();
	}
	
	public List<Value> getRow(final int index) {
		return typedTable.getRow(index);
	}
	
	public List<Value> getColumn(final int index) {
		return typedTable.getColumn(index);
	}

	@Override
	public Map<Integer, List<Value>> getNumericColumns() {
		return numericColumns;
	}

	@Override
	public Map<Integer, List<Value>> getTextualColumns() {
		return textualColumns;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numericColumns.hashCode();
		result = prime * result + textualColumns.hashCode();
		result = prime * result + typedTable.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final NestedListsSlicedTable other = (NestedListsSlicedTable) obj;
		if (!numericColumns.equals(other.numericColumns)) {
			return false;
		}
		if (!textualColumns.equals(other.textualColumns)) {
			return false;
		}
		if (!typedTable.equals(other.typedTable)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NestedListsSlicedTable [typedTable=" + typedTable + ", numericColumns=" + numericColumns
				+ ", textualColumns=" + textualColumns + "]";
	}
}
