package eu.odalic.extrarelatable.model.table;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.Value;
import eu.odalic.extrarelatable.util.Matrix;

/**
 * Implementation of {@link TypedTable} based on immutable list of immutable
 * lists.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
public final class NestedListsTypedTable implements TypedTable {
	private final List<Label> header;

	private final List<List<Value>> rows;

	private final List<List<Value>> columns;

	private final Metadata metadata;

	/**
	 * Builds table from header row and other typed rows.
	 * 
	 * @param header
	 *            header row
	 * @param rows
	 *            other rows
	 * @param metadata
	 *            the table meta-data
	 * @return the typed table
	 */
	public static TypedTable fromRows(final List<? extends Label> header,
			final List<? extends List<? extends Value>> rows, final Metadata metadata) {
		checkNotNull(header);
		checkNotNull(rows);
		checkNotNull(metadata);

		return new NestedListsTypedTable(ImmutableList.copyOf(header), Matrix.copy(rows), Matrix.transpose(rows),
				metadata);
	}

	private NestedListsTypedTable(List<Label> header, List<List<Value>> rows, List<List<Value>> columns,
			Metadata metadata) {
		assert header != null;
		assert rows != null;
		assert metadata != null;

		this.header = header;
		this.rows = rows;
		this.columns = columns;
		this.metadata = metadata;
	}

	@Override
	public List<Label> getHeaders() {
		return header;
	}

	@Override
	public List<List<Value>> getRows() {
		return rows;
	}

	@Override
	public List<List<Value>> getColumns() {
		return columns;
	}

	@Override
	public Metadata getMetadata() {
		return metadata;
	}

	@Override
	public int getWidth() {
		return columns.size();
	}

	@Override
	public int getHeight() {
		return rows.size();
	}

	@Override
	public List<Value> getRow(final int index) {
		return rows.get(index);
	}

	@Override
	public List<Value> getColumn(final int index) {
		return columns.get(index);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + metadata.hashCode();
		result = prime * result + header.hashCode();
		result = prime * result + rows.hashCode();
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
		NestedListsTypedTable other = (NestedListsTypedTable) obj;
		if (!metadata.equals(other.metadata)) {
			return false;
		}
		if (!header.equals(other.header)) {
			return false;
		}
		if (!rows.equals(other.rows)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ParsedTable [header=" + header + ", rows=" + rows + ", columns=" + columns + ", metadata=" + metadata
				+ ", width=" + getWidth() + ", height=" + getHeight() + "]";
	}
}
