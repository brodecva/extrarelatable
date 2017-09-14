package eu.odalic.extrarelatable.model.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

@Immutable
public final class Context {
	private final List<Label> columnHeaders;
	private final String tableAauthor;
	private final String tableTitle;
	
	public Context(final List<? extends Label> columnHeaders, final String tableAuthor, final String tableTitle) {
		checkNotNull(columnHeaders);
		
		this.columnHeaders = ImmutableList.copyOf(columnHeaders);
		this.tableAauthor = tableAuthor;
		this.tableTitle = tableTitle;
	}

	public List<Label> getColumnHeaders() {
		return columnHeaders;
	}

	public String getTableAauthor() {
		return tableAauthor;
	}

	public String getTableTitle() {
		return tableTitle;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + columnHeaders.hashCode();
		result = prime * result + ((tableAauthor == null) ? 0 : tableAauthor.hashCode());
		result = prime * result + ((tableTitle == null) ? 0 : tableTitle.hashCode());
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
		final Context other = (Context) obj;
		if (!columnHeaders.equals(other.columnHeaders)) {
			return false;
		}
		if (tableAauthor == null) {
			if (other.tableAauthor != null) {
				return false;
			}
		} else if (!tableAauthor.equals(other.tableAauthor)) {
			return false;
		}
		if (tableTitle == null) {
			if (other.tableTitle != null) {
				return false;
			}
		} else if (!tableTitle.equals(other.tableTitle)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Context [columnHeaders=" + columnHeaders + ", tableAauthor=" + tableAauthor + ", tableTitle="
				+ tableTitle + "]";
	}
}
