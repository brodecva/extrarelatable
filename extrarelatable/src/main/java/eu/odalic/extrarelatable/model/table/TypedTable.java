package eu.odalic.extrarelatable.model.table;

import java.util.List;

import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.Value;

public interface TypedTable {
	List<Label> getHeaders();

	List<List<Value>> getRows();

	List<List<Value>> getColumns();

	Metadata getMetadata();

	int getWidth();

	int getHeight();

	List<Value> getRow(int index);

	List<Value> getColumn(int index);
}