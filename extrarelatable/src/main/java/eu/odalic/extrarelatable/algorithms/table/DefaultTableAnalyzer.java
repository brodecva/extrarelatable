package eu.odalic.extrarelatable.algorithms.table;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.odalic.extrarelatable.algorithms.bag.InstantValueParser;
import eu.odalic.extrarelatable.algorithms.bag.NumericValueParser;
import eu.odalic.extrarelatable.algorithms.bag.UnitValueParser;
import eu.odalic.extrarelatable.algorithms.bag.ValueTypeAnalyzer;
import eu.odalic.extrarelatable.model.bag.EmptyValue;
import eu.odalic.extrarelatable.model.bag.EntityValue;
import eu.odalic.extrarelatable.model.bag.IdValue;
import eu.odalic.extrarelatable.model.bag.InstantValue;
//import eu.odalic.extrarelatable.model.bag.InstantValue;
import eu.odalic.extrarelatable.model.bag.Label;
import eu.odalic.extrarelatable.model.bag.NumericValue;
import eu.odalic.extrarelatable.model.bag.TextValue;
import eu.odalic.extrarelatable.model.bag.Type;
import eu.odalic.extrarelatable.model.bag.UnitValue;
import eu.odalic.extrarelatable.model.bag.Value;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;
import eu.odalic.extrarelatable.model.table.NestedListsTypedTable;

@Immutable
@Component
public final class DefaultTableAnalyzer implements TableAnalyzer {

	private static final int MAXIMUM_PREVIEW_SIZE = 5;

	private final NumericValueParser numericValueParser;
	private final InstantValueParser instantValueParser;
	private final UnitValueParser unitValueParser;
	private final ValueTypeAnalyzer valueTypeAnalyzer;

	private final boolean datesParsed;

	public DefaultTableAnalyzer(final NumericValueParser numericValueParser,
			final InstantValueParser instantValueParser, final UnitValueParser unitValueParser,
			final ValueTypeAnalyzer valueTypeAnalyzer,
			final @org.springframework.beans.factory.annotation.Value("${eu.odalic.extrarelatable.datesParsed:true}") boolean datesParsed) {
		checkNotNull(numericValueParser);
		checkNotNull(unitValueParser);
		checkNotNull(instantValueParser);
		checkNotNull(valueTypeAnalyzer);

		this.numericValueParser = numericValueParser;
		this.unitValueParser = unitValueParser;
		this.instantValueParser = instantValueParser;
		this.valueTypeAnalyzer = valueTypeAnalyzer;
		this.datesParsed = datesParsed;
	}

	@Override
	public TypedTable infer(final ParsedTable table, final Locale locale) {
		return infer(table, locale, ImmutableMap.of());
	}

	@Override
	public TypedTable infer(final ParsedTable table, final Locale locale,
			final Map<? extends Integer, ? extends Type> columnTypeHints) {
		checkNotNull(table);
		checkNotNull(columnTypeHints);

		final List<String> headers = table.getHeaders();
		final ImmutableList.Builder<Label> labelsBuilder = ImmutableList.builder();
		final int headersSize = headers.size();
		for (int headerIndex = 0; headerIndex < headersSize; headerIndex++) {
			final String header = headers.get(headerIndex);
			final String tableTitle = table.getMetadata().getTitle();
			final List<String> column = table.getColumn(headerIndex);
			final List<List<String>> rows = table.getRows();

			if (valueTypeAnalyzer.isEmpty(header)) {
				labelsBuilder.add(Label.synthetic(headerIndex, tableTitle, preview(column), headers, preview(rows)));
			} else {
				labelsBuilder.add(Label.of(header, null, false, headerIndex, tableTitle, preview(column), headers,
						preview(rows)));
			}
		}

		final List<List<String>> inputRows = table.getRows();
		final List<List<Value>> rows = inputRows.stream().map(row -> {
			final ImmutableList.Builder<Value> builder = ImmutableList.builder();

			for (int index = 0; index < row.size(); index++) {
				final String cell = row.get(index);

				if (valueTypeAnalyzer.isEmpty(cell)) {
					builder.add(EmptyValue.INSTANCE);
					continue;
				}

				final Type hint = columnTypeHints.get(index);
				if (hint != null) {
					switch (hint) {
					case TEXT:
						builder.add(TextValue.of(cell));
						break;
					case ID:
						builder.add(IdValue.of(cell));
						break;
					case ENTITY:
						builder.add(EntityValue.of(cell));
						break;
					case UNIT:
						if (valueTypeAnalyzer.isUnit(cell, locale)) {
							builder.add(UnitValue.of(unitValueParser.parse(cell, locale), cell));
							break;
						}
					case NUMERIC:
						if (valueTypeAnalyzer.isNumeric(cell, locale)) {
							builder.add(NumericValue.of(numericValueParser.parse(cell, locale)));
							break;
						}
					case DATE:
						if (valueTypeAnalyzer.isInstant(cell, locale)) {
							builder.add(InstantValue.of(instantValueParser.parse(cell, locale)));
							break;
						}
					default:
						builder.add(TextValue.of(cell));
					}
				} else {
					if (valueTypeAnalyzer.isNumeric(cell, locale)) {
						builder.add(NumericValue.of(numericValueParser.parse(cell, locale)));
					} else if (this.datesParsed && valueTypeAnalyzer.isInstant(cell, locale)) {
						builder.add(InstantValue.of(instantValueParser.parse(cell, locale)));
					} else if (valueTypeAnalyzer.isUnit(cell, locale)) {
						builder.add(UnitValue.of(unitValueParser.parse(cell, locale), cell));
					} else {
						builder.add(TextValue.of(cell));
					}
				}
			}

			return builder.build();
		}).collect(ImmutableList.toImmutableList());

		return NestedListsTypedTable.fromRows(labelsBuilder.build(), rows, table.getMetadata());
	}

	private static <T> List<T> preview(final List<T> list) {
		return list.subList(0, Math.min(list.size(), MAXIMUM_PREVIEW_SIZE));
	}

	@Override
	public TypedTable infer(ParsedTable table) {
		return infer(table, null);
	}
}
