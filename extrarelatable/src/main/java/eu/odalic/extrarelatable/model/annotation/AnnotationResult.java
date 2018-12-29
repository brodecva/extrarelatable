package eu.odalic.extrarelatable.model.annotation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;

import eu.odalic.extrarelatable.api.rest.adapters.AnnotationResultAdapter;
import eu.odalic.extrarelatable.model.table.ParsedTable;

/**
 * {@link Annotation}s for each recognized numeric column and the parsed table
 * from which they were computed.
 * 
 * @author Václav Brodec
 *
 */
@XmlJavaTypeAdapter(AnnotationResultAdapter.class)
public final class AnnotationResult implements Serializable {

	private static final long serialVersionUID = 8678871094793797335L;

	private final ParsedTable parsedTable;

	private final Map<Integer, Annotation> annotations;

	@SuppressWarnings("unused")
	private AnnotationResult() {
		parsedTable = null;
		annotations = ImmutableMap.of();
	}

	/**
	 * Creates the complete result of the annotating process.
	 * 
	 * @param parsedTable
	 *            the original parsed table
	 * @param annotations
	 *            annotations for each recognized numeric column
	 */
	public AnnotationResult(final ParsedTable parsedTable,
			final Map<? extends Integer, ? extends Annotation> annotations) {
		checkNotNull(parsedTable);
		checkNotNull(annotations);

		this.parsedTable = parsedTable;
		this.annotations = ImmutableMap.copyOf(annotations);
	}

	/**
	 * @return the original parsed table, which served as the input
	 */
	@XmlTransient
	@JsonIgnore
	public ParsedTable getParsedTable() {
		return parsedTable;
	}

	/**
	 * @return the map of indices of recognized numeric columns to the assigned
	 *         annotations
	 */
	public Map<Integer, Annotation> getAnnotations() {
		return annotations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
		result = prime * result + ((parsedTable == null) ? 0 : parsedTable.hashCode());
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
		AnnotationResult other = (AnnotationResult) obj;
		if (annotations == null) {
			if (other.annotations != null) {
				return false;
			}
		} else if (!annotations.equals(other.annotations)) {
			return false;
		}
		if (parsedTable == null) {
			if (other.parsedTable != null) {
				return false;
			}
		} else if (!parsedTable.equals(other.parsedTable)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AnnotationResult [parsedTable=" + parsedTable + ", annotations=" + annotations + "]";
	}
}
