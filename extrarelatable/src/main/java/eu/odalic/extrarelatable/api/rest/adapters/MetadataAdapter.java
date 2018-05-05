package eu.odalic.extrarelatable.api.rest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.odalic.extrarelatable.api.rest.values.MetadataValue;
import eu.odalic.extrarelatable.model.table.Metadata;


public final class MetadataAdapter extends XmlAdapter<MetadataValue, Metadata> {

  @Override
  public MetadataValue marshal(final Metadata bound) throws Exception {
	  return new MetadataValue(bound);
  }

  @Override
  public Metadata unmarshal(final MetadataValue value) throws Exception {
    return new Metadata(value.getTitle(), value.getAuthor(), value.getLanguageTag(), value.getDeclaredProperties(), value.getDeclaredClasses(), value.getCollectedProperties(), value.getCollectedClasses());
  }
}
