package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.google.common.collect.ImmutableMap;

import eu.odalic.extrarelatable.model.annotation.Annotation;
import eu.odalic.extrarelatable.model.annotation.AnnotationResult;

@XmlRootElement(name = "annotationResult")
public class AnnotationResultValue {
	
	private Map<Integer, Annotation> annotations;

	public AnnotationResultValue() {
		this.annotations = ImmutableMap.of();
	}
	
	public AnnotationResultValue(final AnnotationResult adaptee) {
		checkNotNull(adaptee);
		
		this.annotations = adaptee.getAnnotations();
	}

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
