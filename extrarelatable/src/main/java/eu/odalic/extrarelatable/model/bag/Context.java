package eu.odalic.extrarelatable.model.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;

/**
 * COntext in which a {@link PropertyTree} appears in the original file.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
public final class Context implements Serializable {

	private static final long serialVersionUID = 3692200553185310125L;

	private final List<Label> columnHeaders;
	private final String tableAauthor;
	private final String tableTitle;
	private final DeclaredEntity declaredProperty;
	private final Map<Integer, DeclaredEntity> declaredColumnProperties;
	private final Map<Integer, DeclaredEntity> declaredColumnClasses;
	private final Integer columnIndex;
	private final Set<Integer> contextColumnIndices;

	/**
	 * Creates context object without any context entitites.
	 * 
	 * @param columnHeaders
	 *            column labels collected from the original file
	 * @param tableAuthor
	 *            author of the table
	 * @param tableTitle
	 *            title of the table
	 */
	public Context(final List<? extends Label> columnHeaders, final String tableAuthor, final String tableTitle) {
		this(columnHeaders, tableAuthor, tableTitle, null, ImmutableMap.of(), ImmutableMap.of(), null,
				ImmutableSet.of());
	}

	/**
	 * Creates context object.
	 * 
	 * @param columnHeaders
	 *            column labels collected from the original file
	 * @param tableAuthor
	 *            author of the table
	 * @param tableTitle
	 *            title of the table
	 * @param declaredProperty
	 *            property assigned manually to the column which the property tree
	 *            models
	 * @param declaredColumnProperties
	 *            all declared properties for the columns in the original file
	 * @param declaredColumnClasses
	 *            all declared classes for the columns in the original file
	 * @param columnIndex
	 *            index of the original column
	 * @param contextColumnIndices
	 *            indices of columns which do not contain number-like data, but are
	 *            only meant to provide row context
	 */
	public Context(final List<? extends Label> columnHeaders, final String tableAuthor, final String tableTitle,
			final DeclaredEntity declaredProperty,
			final Map<? extends Integer, ? extends DeclaredEntity> declaredColumnProperties,
			final Map<? extends Integer, ? extends DeclaredEntity> declaredColumnClasses, final Integer columnIndex,
			final Set<? extends Integer> contextColumnIndices) {
		checkNotNull(columnHeaders);
		checkNotNull(declaredColumnProperties);
		checkNotNull(declaredColumnClasses);
		checkNotNull(contextColumnIndices);

		this.columnHeaders = ImmutableList.copyOf(columnHeaders);
		this.tableAauthor = tableAuthor;
		this.tableTitle = tableTitle;
		this.declaredProperty = declaredProperty;
		this.declaredColumnProperties = ImmutableMap.copyOf(declaredColumnProperties);
		this.declaredColumnClasses = ImmutableMap.copyOf(declaredColumnClasses);
		this.columnIndex = columnIndex;
		this.contextColumnIndices = ImmutableSet.copyOf(contextColumnIndices);
	}

	/**
	 * @return column labels collected from the original file
	 */
	public List<Label> getColumnHeaders() {
		return columnHeaders;
	}

	/**
	 * @return the author of the table
	 */
	@Nullable
	public String getTableAauthor() {
		return tableAauthor;
	}

	/**
	 * @return the title of the table
	 */
	@Nullable
	public String getTableTitle() {
		return tableTitle;
	}

	/**
	 * @return property assigned manually to the column which the property tree
	 *         models
	 */
	@Nullable
	public DeclaredEntity getDeclaredProperty() {
		return declaredProperty;
	}

	/**
	 * @return all declared properties for the columns in the original file
	 */
	public Map<Integer, DeclaredEntity> getDeclaredColumnProperties() {
		return declaredColumnProperties;
	}

	/**
	 * @return all declared classes for the columns in the original file
	 */
	public Map<Integer, DeclaredEntity> getDeclaredColumnClasses() {
		return declaredColumnClasses;
	}

	/**
	 * @return index of the original column
	 */
	public Integer getColumnIndex() {
		return columnIndex;
	}

	/**
	 * @return indices of columns which do not contain number-like data, but are
	 *         only meant to provide row context
	 */
	public Set<Integer> getContextColumnIndices() {
		return contextColumnIndices;
	}

	/**
	 * @return declared properties belonging to the columns providing row context
	 */
	public Map<Integer, DeclaredEntity> getDeclaredContextColumnProperties() {
		return declaredColumnProperties.entrySet().stream().filter(e -> contextColumnIndices.contains(e.getKey()))
				.collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> e.getValue()));
	}

	/**
	 * @return declared classes belonging to the columns providing row context
	 */
	public Map<Integer, DeclaredEntity> getDeclaredContextColumnClasses() {
		return declaredColumnClasses.entrySet().stream().filter(e -> contextColumnIndices.contains(e.getKey()))
				.collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> e.getValue()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnHeaders == null) ? 0 : columnHeaders.hashCode());
		result = prime * result + ((columnIndex == null) ? 0 : columnIndex.hashCode());
		result = prime * result + ((contextColumnIndices == null) ? 0 : contextColumnIndices.hashCode());
		result = prime * result + ((declaredColumnProperties == null) ? 0 : declaredColumnProperties.hashCode());
		result = prime * result + ((declaredColumnClasses == null) ? 0 : declaredColumnClasses.hashCode());
		result = prime * result + ((declaredProperty == null) ? 0 : declaredProperty.hashCode());
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
		if (declaredColumnClasses == null) {
			if (other.declaredColumnClasses != null) {
				return false;
			}
		} else if (!declaredColumnClasses.equals(other.declaredColumnClasses)) {
			return false;
		}
		if (declaredProperty == null) {
			if (other.declaredProperty != null) {
				return false;
			}
		} else if (!declaredProperty.equals(other.declaredProperty)) {
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
				+ tableTitle + ", declaredPropertyUri=" + declaredProperty + ", declaredColumnProperties="
				+ declaredColumnProperties + ", declaredColumnClasses=" + declaredColumnClasses + ", columnIndex="
				+ columnIndex + ", contextColumnIndices=" + contextColumnIndices + "]";
	}
}
