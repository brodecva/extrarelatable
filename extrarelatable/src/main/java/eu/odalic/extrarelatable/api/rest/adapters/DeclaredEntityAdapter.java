package eu.odalic.extrarelatable.api.rest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.odalic.extrarelatable.api.rest.values.DeclaredEntityValue;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;

/**
 * Adapter of {@link DeclaredEntity} to {@link DeclaredEntityValue} and back.
 * 
 * @author Václav Brodec
 *
 */
public final class DeclaredEntityAdapter extends XmlAdapter<DeclaredEntityValue, DeclaredEntity> {

  @Override
  public DeclaredEntityValue marshal(final DeclaredEntity bound) throws Exception {
	  return new DeclaredEntityValue(bound);
  }

  @Override
  public DeclaredEntity unmarshal(final DeclaredEntityValue value) throws Exception {
    return new DeclaredEntity(value.getUri(), value.getLabels());
  }
}
