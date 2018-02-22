package eu.odalic.extrarelatable.services;

import java.util.List;
import java.util.Map;

import eu.odalic.extrarelatable.model.bag.Type;

public interface CsvProfileTypesToHintsConverter {

	Map<Integer, Type> toHints(final List<? extends Type> types);

}
