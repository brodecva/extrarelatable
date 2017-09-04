package eu.odalic.extrarelatable.model.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public final class Context {
	private final Set<ColumnHeader> columnHeaders;
	private final String tableAauthor;
	private final String tableTitle;
	private final Set<AttributeValuePair> attributeValuePairs;
	
	public Context(Set<? extends ColumnHeader> columnHeaders, String tableAauthor, String tableTitle,
			Set<? extends AttributeValuePair> attributeValuePairs) {
		checkNotNull(columnHeaders);
		checkNotNull(attributeValuePairs);
		
		this.columnHeaders = ImmutableSet.copyOf(columnHeaders);
		this.tableAauthor = tableAauthor;
		this.tableTitle = tableTitle;
		this.attributeValuePairs = ImmutableSet.copyOf(attributeValuePairs);
	}

	public Set<ColumnHeader> getColumnHeaders() {
		return columnHeaders;
	}

	public String getTableAauthor() {
		return tableAauthor;
	}

	public String getTableTitle() {
		return tableTitle;
	}

	public Set<AttributeValuePair> getAttributeValuePairs() {
		return attributeValuePairs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + attributeValuePairs.hashCode();
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
		if (!attributeValuePairs.equals(other.attributeValuePairs)) {
			return false;
		}
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
				+ tableTitle + ", attributeValuePairs=" + attributeValuePairs + "]";
	}
}
