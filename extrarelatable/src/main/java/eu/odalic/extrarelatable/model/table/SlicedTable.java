package eu.odalic.extrarelatable.model.table;

import java.util.List;
import java.util.Map;

import eu.odalic.extrarelatable.model.bag.Value;

public interface SlicedTable extends TypedTable {

	Map<Integer, List<Value>> getNumericColumns();

	Map<Integer, List<Value>> getTextualColumns();

}