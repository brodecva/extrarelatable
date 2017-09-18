package eu.odalic.extrarelatable.algorithms.table;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import eu.odalic.extrarelatable.algorithms.bag.NumericValueParser;
import eu.odalic.extrarelatable.algorithms.bag.ValueTypeAnalyzer;
import eu.odalic.extrarelatable.model.bag.EmptyValue;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.NumericValue;
import eu.odalic.extrarelatable.model.bag.TextValue;
import eu.odalic.extrarelatable.model.bag.Value;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;
import eu.odalic.extrarelatable.model.table.NestedListsTypedTable;

@Immutable
@Component
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
		final List<String> headers = table.getHeaders();
		final ImmutableList.Builder<Label> labelsBuilder = ImmutableList.builder();
		final int headersSize = headers.size();
		for (int headerIndex = 0; headerIndex < headersSize; headerIndex++) {
			final String header = headers.get(headerIndex);
			final String description = String.valueOf(headerIndex);
			
			if (valueTypeAnalyzer.isEmpty(header)) {
				labelsBuilder.add(Label.synthetic(description));
			} else {
				labelsBuilder.add(Label.of(header, description));
			}
		}
		
		final List<List<String>> inputRows = table.getRows();
		final List<List<Value>> rows = inputRows.stream().map(row -> {
			return row.stream().map(cell -> {
				if (valueTypeAnalyzer.isEmpty(cell)) {
					return EmptyValue.INSTANCE;
				} else if (valueTypeAnalyzer.isNumeric(cell, forcedLocale)) {
					return NumericValue.of(numericValueParser.parse(cell, forcedLocale));
				} else {
					return TextValue.of(cell);
				}
			}).collect(ImmutableList.toImmutableList());
		}).collect(ImmutableList.toImmutableList());
		
		return NestedListsTypedTable.fromRows(labelsBuilder.build(), rows, table.getMetadata());
	}

	@Override
	public TypedTable infer(ParsedTable table) {
		return infer(table, null);
	}
}
