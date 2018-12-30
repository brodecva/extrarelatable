package eu.odalic.extrarelatable.api.rest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.odalic.extrarelatable.api.rest.values.AnnotationResultValue;
import eu.odalic.extrarelatable.model.annotation.AnnotationResult;

/**
 * Adapter of {@link AnnotationResult} to {@link AnnotationResultValue}.
 * 
 * @author Václav Brodec
 *
 */
public final class AnnotationResultAdapter extends XmlAdapter<AnnotationResultValue, AnnotationResult> {

	@Override
	public AnnotationResultValue marshal(final AnnotationResult bound) throws Exception {
		return new AnnotationResultValue(bound);
	}

	@Override
	public AnnotationResult unmarshal(final AnnotationResultValue value) throws Exception {
		throw new UnsupportedOperationException();
	}
}
