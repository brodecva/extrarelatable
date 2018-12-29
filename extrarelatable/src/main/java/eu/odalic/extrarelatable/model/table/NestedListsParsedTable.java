package eu.odalic.extrarelatable.model.table;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import eu.odalic.extrarelatable.util.Matrix;

/**
 * Implementation of {@link ParsedTable} based on immutable list of immutable
 * lists.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
public final class NestedListsParsedTable implements ParsedTable {

	/**
	 * <p>
	 * Incrementally, row by row, helps to produce the complete
	 * {@link NestedListsParsedTable}.
	 * </p>
	 * 
	 * <p>
	 * Adapted from Odalic with permission.
	 * </p>
	 *
	 * @author Jan Váňa
	 * @author Václav Brodec
	 *
	 */
	public static final class Builder {

		private final List<String> headers = new ArrayList<>();
		private final List<List<String>> rows = new ArrayList<>();

		private Metadata metadata = null;

		/**
		 * Build the table.
		 * 
		 * @return built table
		 */
		public NestedListsParsedTable build() {
			return NestedListsParsedTable.fromRows(headers, rows, metadata);
		}

		/**
		 * Insert new cell.
		 * 
		 * @param value cell value
		 * @param rowIndex row index
		 * @param columnIndex column index
		 * @return the builder
		 */
		public Builder insertCell(final String value, final int rowIndex, final int columnIndex) {
			while (rows.size() <= rowIndex) {
				rows.add(new ArrayList<>());
			}

			insertToList(rows.get(rowIndex), value, columnIndex);
			
			return this;
		}

		/**
		 * Insert a header.
		 * 
		 * @param value header value
		 * @param position position index
		 * @return the builder
		 */
		public Builder insertHeader(final String value, final int position) {
			insertToList(headers, value, position);

			return this;
		}

		private void insertToList(final List<String> list, final String value, final int position) {
			while (list.size() <= position) {
				list.add(null);
			}

			list.set(position, value);
		}

		/**
		 * Sets the table meta-data.
		 * 
		 * @param metadata the meta-data
		 * @return the builder
		 */
		public Builder setMetadata(final Metadata metadata) {
			checkNotNull(metadata);

			this.metadata = metadata;

			return this;
		}
	}

	private final List<String> header;

	private final List<List<String>> rows;

	private final List<List<String>> columns;

	private final Metadata metadata;

	/**
	 * Provides a table builder.
	 * 
	 * @return the builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builds table from columns. The first cell of each column becomes a header.
	 * 
	 * @param columns source columns
	 * @param metadata table meta-data
	 * @return the table
	 */
	public static NestedListsParsedTable fromColumns(final List<? extends List<? extends String>> columns,
			final Metadata metadata) {
		checkNotNull(columns);
		checkNotNull(metadata);

		final List<List<String>> rowsWithHeader = Matrix.transpose(columns);
		checkArgument(!rowsWithHeader.isEmpty(), "The table must have at least one row!");

		final List<String> header = rowsWithHeader.get(0);

		final int rowsWithHeaderSize = rowsWithHeader.size();
		final List<List<String>> rows = rowsWithHeader.subList(Math.min(1, rowsWithHeaderSize), rowsWithHeaderSize);

		return new NestedListsParsedTable(ImmutableList.copyOf(header), Matrix.copy(rows), Matrix.transpose(rows),
				metadata);
	}

	/**
	 * Builds a table from header row and other rows.
	 * 
	 * @param header headers
	 * @param rows source rows
	 * @param metadata table meta-data
	 * @return the table
	 */
	public static NestedListsParsedTable fromRows(final List<? extends String> header,
			final List<? extends List<? extends String>> rows, final Metadata metadata) {
		checkNotNull(header);
		checkNotNull(rows);
		checkNotNull(metadata);

		return new NestedListsParsedTable(ImmutableList.copyOf(header), Matrix.copy(rows), Matrix.transpose(rows),
				metadata);
	}

	private NestedListsParsedTable(List<String> header, List<List<String>> rows, List<List<String>> columns,
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
	public List<String> getHeaders() {
		return header;
	}

	@Override
	public List<List<String>> getRows() {
		return rows;
	}

	@Override
	public List<List<String>> getColumns() {
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
	public List<String> getRow(final int index) {
		return rows.get(index);
	}

	@Override
	public List<String> getColumn(final int index) {
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
		NestedListsParsedTable other = (NestedListsParsedTable) obj;
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
		return "Table [header=" + header + ", rows=" + rows + ", columns=" + columns + ", metadata=" + metadata
				+ ", width=" + getWidth() + ", height=" + getHeight() + "]";
	}
}
