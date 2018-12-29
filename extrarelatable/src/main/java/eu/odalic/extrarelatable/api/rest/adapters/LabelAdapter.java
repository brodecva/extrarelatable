package eu.odalic.extrarelatable.api.rest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.odalic.extrarelatable.api.rest.values.LabelValue;
import eu.odalic.extrarelatable.model.bag.Label;

/**
 * Adapter of {@link Label} to {@link LabelValue}.
 * 
 * @author Václav Brodec
 *
 */
public final class LabelAdapter extends XmlAdapter<LabelValue, Label> {

  @Override
  public LabelValue marshal(final Label bound) throws Exception {
	  return new LabelValue(bound);
  }

  @Override
  public Label unmarshal(final LabelValue value) throws Exception {
	  throw new UnsupportedOperationException();
  }
}
