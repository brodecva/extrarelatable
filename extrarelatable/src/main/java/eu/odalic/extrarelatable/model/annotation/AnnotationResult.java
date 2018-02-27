package eu.odalic.extrarelatable.model.annotation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableMap;

import eu.odalic.extrarelatable.api.rest.adapters.AnnotationResultAdapter;
import eu.odalic.extrarelatable.model.table.ParsedTable;

@XmlJavaTypeAdapter(AnnotationResultAdapter.class)
public class AnnotationResult {
	private final ParsedTable parsedTable;
	
	private final Map<Integer, Annotation> annotations;

	public AnnotationResult(final ParsedTable parsedTable, final Map<? extends Integer, ? extends Annotation> annotations) {
		checkNotNull(parsedTable);
		checkNotNull(annotations);
		
		this.parsedTable = parsedTable;
		this.annotations = ImmutableMap.copyOf(annotations);
	}
	
	public ParsedTable getParsedTable() {
		return parsedTable;
	}

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
