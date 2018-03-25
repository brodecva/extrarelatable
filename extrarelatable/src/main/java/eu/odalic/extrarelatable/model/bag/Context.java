package eu.odalic.extrarelatable.model.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Immutable
public final class Context implements Serializable {
	
	private static final long serialVersionUID = 3692200553185310125L;
	
	private final List<Label> columnHeaders;
	private final String tableAauthor;
	private final String tableTitle;
	private final URI declaredPropertyUri;
	private final Map<Integer, URI> declaredColumnProperties;
	private final Integer columnIndex;
	private final Set<Integer> contextColumnIndices;
	
	public Context(final List<? extends Label> columnHeaders, final String tableAuthor, final String tableTitle) {
		this(columnHeaders, tableAuthor, tableTitle, null, ImmutableMap.of(), null, ImmutableSet.of());
	}
	
	public Context(final List<? extends Label> columnHeaders, final String tableAuthor, final String tableTitle, final URI declaredPropertyUri, final Map<? extends Integer, ? extends URI> declaredColumnProperties, final Integer columnIndex, final Set<? extends Integer> contextColumnIndices) {
		checkNotNull(columnHeaders);
		
		this.columnHeaders = ImmutableList.copyOf(columnHeaders);
		this.tableAauthor = tableAuthor;
		this.tableTitle = tableTitle;
		this.declaredPropertyUri = declaredPropertyUri;
		this.declaredColumnProperties = ImmutableMap.copyOf(declaredColumnProperties);
		this.columnIndex = columnIndex;
		this.contextColumnIndices = ImmutableSet.copyOf(contextColumnIndices);
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
	
	public URI getDeclaredPropertyUri() {
		return declaredPropertyUri;
	}

	public Map<Integer, URI> getDeclaredColumnProperties() {
		return declaredColumnProperties;
	}

	public Integer getColumnIndex() {
		return columnIndex;
	}

	public Set<Integer> getContextColumnIndices() {
		return contextColumnIndices;
	}
	
	public Map<Integer, URI> getDeclaredContextColumnProperties() {
		return declaredColumnProperties.entrySet().stream().filter(e -> contextColumnIndices.contains(e.getKey())).collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> e.getValue()));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnHeaders == null) ? 0 : columnHeaders.hashCode());
		result = prime * result + ((columnIndex == null) ? 0 : columnIndex.hashCode());
		result = prime * result + ((contextColumnIndices == null) ? 0 : contextColumnIndices.hashCode());
		result = prime * result + ((declaredColumnProperties == null) ? 0 : declaredColumnProperties.hashCode());
		result = prime * result + ((declaredPropertyUri == null) ? 0 : declaredPropertyUri.hashCode());
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
		Context other = (Context) obj;
		if (columnHeaders == null) {
			if (other.columnHeaders != null) {
				return false;
			}
		} else if (!columnHeaders.equals(other.columnHeaders)) {
			return false;
		}
		if (columnIndex == null) {
			if (other.columnIndex != null) {
				return false;
			}
		} else if (!columnIndex.equals(other.columnIndex)) {
			return false;
		}
		if (contextColumnIndices == null) {
			if (other.contextColumnIndices != null) {
				return false;
			}
		} else if (!contextColumnIndices.equals(other.contextColumnIndices)) {
			return false;
		}
		if (declaredColumnProperties == null) {
			if (other.declaredColumnProperties != null) {
				return false;
			}
		} else if (!declaredColumnProperties.equals(other.declaredColumnProperties)) {
			return false;
		}
		if (declaredPropertyUri == null) {
			if (other.declaredPropertyUri != null) {
				return false;
			}
		} else if (!declaredPropertyUri.equals(other.declaredPropertyUri)) {
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
				+ tableTitle + ", declaredPropertyUri=" + declaredPropertyUri + ", declaredColumnProperties="
				+ declaredColumnProperties + ", columnIndex=" + columnIndex + ", contextColumnIndices="
				+ contextColumnIndices + "]";
	}
}
