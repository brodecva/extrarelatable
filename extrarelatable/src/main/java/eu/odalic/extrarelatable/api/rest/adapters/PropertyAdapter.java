package eu.odalic.extrarelatable.api.rest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.odalic.extrarelatable.api.rest.values.PropertyValue;
import eu.odalic.extrarelatable.model.graph.Property;


public final class PropertyAdapter extends XmlAdapter<PropertyValue, Property> {

  @Override
  public PropertyValue marshal(final Property bound) throws Exception {
	  return new PropertyValue(bound);
  }

  @Override
  public Property unmarshal(final PropertyValue value) throws Exception {
    throw new UnsupportedOperationException();
  }
}
