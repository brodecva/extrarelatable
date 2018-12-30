package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableMap;
import eu.odalic.extrarelatable.model.annotation.Annotation;
import eu.odalic.extrarelatable.model.annotation.AnnotationResult;

/**
 * <p>
 * A container of annotations for each recognized numeric column, accompanied by
 * the parsed table from which the annotations were computed.
 * </p>
 * 
 * <p>
 * {@link AnnotationResult} adapted for REST API.
 * </p>
 * 
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "annotationResult")
public class AnnotationResultValue implements Serializable {

	private static final long serialVersionUID = 447474098862783908L;

	private Map<Integer, Annotation> annotations;

	public AnnotationResultValue() {
		this.annotations = ImmutableMap.of();
	}

	public AnnotationResultValue(final AnnotationResult adaptee) {
		checkNotNull(adaptee);

		this.annotations = adaptee.getAnnotations();
	}

	/**
	 * Maps integer column indices of numeric columns to relational {@link AnnotationValue}s.
	 * 
	 * @return map of integer column indices of numeric columns to relational {@link AnnotationValue}s
	 */
	@XmlElement
	public Map<Integer, Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(final Map<? extends Integer, ? extends Annotation> annotations) {
		this.annotations = ImmutableMap.copyOf(annotations);
	}

	@Override
	public String toString() {
		return "AnnotationResultValue [annotations=" + annotations + "]";
	}
}
