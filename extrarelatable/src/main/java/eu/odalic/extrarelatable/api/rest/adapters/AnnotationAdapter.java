package eu.odalic.extrarelatable.api.rest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.odalic.extrarelatable.api.rest.values.AnnotationValue;
import eu.odalic.extrarelatable.model.annotation.Annotation;


/**
 * Adapter of {@link Annotation} to {@link AnnotationValue}.
 * 
 * @author Václav Brodec
 *
 */
public final class AnnotationAdapter extends XmlAdapter<AnnotationValue, Annotation> {

  @Override
  public AnnotationValue marshal(final Annotation bound) throws Exception {
	  return new AnnotationValue(bound);
  }

  @Override
  public Annotation unmarshal(final AnnotationValue value) throws Exception {
    throw new UnsupportedOperationException();
  }
}
