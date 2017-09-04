package eu.odalic.extrarelatable.input;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableList;
import eu.odalic.extrarelatable.algorithms.types.ValueTypeAnalyzer;
import eu.odalic.extrarelatable.model.bag.EmptyValue;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.NumericValue;
import eu.odalic.extrarelatable.model.bag.TextValue;
import eu.odalic.extrarelatable.model.bag.Value;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;
import eu.odalic.extrarelatable.model.table.NestedListsTypedTable;

public final class DefaultTableAnalyzer implements TableAnalyzer {

	private final NumericValueParser numericValueParser;
	private final ValueTypeAnalyzer valueTypeAnalyzer;
	
	public DefaultTableAnalyzer(final NumericValueParser numericValueParser, final ValueTypeAnalyzer valueTypeAnalyzer) {
		checkNotNull(numericValueParser);
		checkNotNull(valueTypeAnalyzer);
		
		this.numericValueParser = numericValueParser;
		this.valueTypeAnalyzer = valueTypeAnalyzer;
	}

	@Override
	public TypedTable infer(final ParsedTable table, final Locale forcedLocale) {
		final List<Label> labels = table.getHeaders().stream().map(header -> {
			if (valueTypeAnalyzer.isEmpty(header)) {
				return Label.synthetic();
			} else {
				return Label.of(header);
			}
		}).collect(ImmutableList.toImmutableList());
		
		final List<List<String>> inputRows = table.getRows();
		final List<List<Value>> rows = inputRows.stream().map(row -> {
			return row.stream().map(cell -> {
				if (valueTypeAnalyzer.isEmpty(cell)) {
					return EmptyValue.INSTANCE;
				} else if (valueTypeAnalyzer.isNumeric(cell)) {
					return NumericValue.of(numericValueParser.parse(cell, forcedLocale));
				} else {
					return TextValue.of(cell);
				}
			}).collect(ImmutableList.toImmutableList());
		}).collect(ImmutableList.toImmutableList());
		
		return NestedListsTypedTable.fromRows(labels, rows, table.getMetadata());
	}

	@Override
	public TypedTable infer(ParsedTable table) {
		return infer(table, null);
	}
}
