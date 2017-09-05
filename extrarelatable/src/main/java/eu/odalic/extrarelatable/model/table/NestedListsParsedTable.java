package eu.odalic.extrarelatable.model.table;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import eu.odalic.extrarelatable.util.Matrix;

@Immutable
public final class NestedListsParsedTable implements ParsedTable {
	
	/**
	 * <p>Incrementally, row by row, helps to produce the complete {@link ListsBackedInput}.</p>
	 * 
	 * <p>Adapted from Odalic.</p>
	 *
	 * @author Jan Váňa
	 * @author Václav Brodec
	 *
	 */
	public static final class Builder {

	  private final List<String> headers = new ArrayList<>();
	  private final List<List<String>> rows = new ArrayList<>();
	  
	  private Metadata metadata = null;

	  public NestedListsParsedTable build() {
	    return NestedListsParsedTable.fromRows(headers, rows, metadata);
	  }

	  public void insertCell(final String value, final int rowIndex, final int columnIndex) {
	    while (rows.size() <= rowIndex) {
	      rows.add(new ArrayList<>());
	    }

	    insertToList(rows.get(rowIndex), value, columnIndex);
	  }

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

	public static Builder builder() {
		return new Builder();
	}
	
	public static NestedListsParsedTable fromRows(final List<? extends String> header, final List<? extends List<? extends String>> rows, final Metadata metadata) {
		checkNotNull(header);
		checkNotNull(rows);
		checkNotNull(metadata);
		
		return new NestedListsParsedTable(ImmutableList.copyOf(header), Matrix.copy(rows), Matrix.translate(rows), metadata);
	}
	
	private NestedListsParsedTable(List<String> header, List<List<String>> rows, List<List<String>> columns, Metadata metadata) {
		assert header != null;
		assert rows != null;
		assert metadata != null;
		
		this.header = header;
		this.rows = rows;
		this.columns = columns;
		this.metadata = metadata;
	}
	
	/* (non-Javadoc)
	 * @see eu.odalic.extrarelatable.model.table.ParsedTable#getHeaders()
	 */
	@Override
	public List<String> getHeaders() {
		return header;
	}
	
	/* (non-Javadoc)
	 * @see eu.odalic.extrarelatable.model.table.ParsedTable#getRows()
	 */
	@Override
	public List<List<String>> getRows() {
		return rows;
	}
	
	/* (non-Javadoc)
	 * @see eu.odalic.extrarelatable.model.table.ParsedTable#getColumns()
	 */
	@Override
	public List<List<String>> getColumns() {
		return columns;
	}
	
	/* (non-Javadoc)
	 * @see eu.odalic.extrarelatable.model.table.ParsedTable#getMetadata()
	 */
	@Override
	public Metadata getMetadata() {
		return metadata;
	}

	/* (non-Javadoc)
	 * @see eu.odalic.extrarelatable.model.table.ParsedTable#getWidth()
	 */
	@Override
	public int getWidth() {
		return columns.size();
	}
	
	/* (non-Javadoc)
	 * @see eu.odalic.extrarelatable.model.table.ParsedTable#getHeight()
	 */
	@Override
	public int getHeight() {
		return rows.size();
	}
	
	/* (non-Javadoc)
	 * @see eu.odalic.extrarelatable.model.table.ParsedTable#getRow(int)
	 */
	@Override
	public List<String> getRow(final int index) {
		return rows.get(index);
	}
	
	/* (non-Javadoc)
	 * @see eu.odalic.extrarelatable.model.table.ParsedTable#getColumn(int)
	 */
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
