package eu.odalic.extrarelatable.api.rest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.odalic.extrarelatable.api.rest.values.AttributeValuePairValue;
import eu.odalic.extrarelatable.model.bag.AttributeValuePair;


public final class AttributeValuePairAdapter extends XmlAdapter<AttributeValuePairValue, AttributeValuePair> {

  @Override
  public AttributeValuePairValue marshal(final AttributeValuePair bound) throws Exception {
	  return new AttributeValuePairValue(bound);
  }

  @Override
  public AttributeValuePair unmarshal(final AttributeValuePairValue value) throws Exception {
	  throw new UnsupportedOperationException();
  }
}
