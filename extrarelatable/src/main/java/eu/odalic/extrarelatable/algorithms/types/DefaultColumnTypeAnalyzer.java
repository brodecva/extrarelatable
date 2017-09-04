package eu.odalic.extrarelatable.algorithms.types;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

import eu.odalic.extrarelatable.model.bag.Value;
import eu.odalic.extrarelatable.model.table.TypedTable;

@Component
public class DefaultColumnTypeAnalyzer implements ColumnTypeAnalyzer {

	@Override
	public double isNumeric(final int columnIndex, final TypedTable table) {
		return test(v -> v.isNumeric(), columnIndex, table);
	}
	
	@Override
	public double isTextual(final int columnIndex, final TypedTable table) {
		return test(v -> v.isTextual(), columnIndex, table);
	}
	
	private double test(Predicate<Value> predicate, final int columnIndex, final TypedTable table) {
		checkArgument(columnIndex >= 0);
		checkArgument(columnIndex < table.getWidth());
		
		final List<Value> columnValues = table.getColumn(columnIndex);
		if (columnValues.isEmpty()) {
			return 0;
		}
		
		int passingValuesCount = 0;
		for (final Value value : columnValues) {
			if (predicate.test(value)) {
				passingValuesCount++;
			}
		}
		
		return passingValuesCount / columnValues.size();
	}
}
