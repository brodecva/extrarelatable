package eu.odalic.extrarelatable.algorithms.table;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.springframework.beans.factory.annotation.Qualifier;
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
import eu.odalic.extrarelatable.util.UuidGenerator;
import eu.odalic.extrarelatable.model.table.NestedListsTypedTable;

/**
 * Default implementation of {@link TableAnalyzer}.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
@Component
public final class DefaultTableAnalyzer implements TableAnalyzer {

	private static final int MAXIMUM_PREVIEW_SIZE = 5;

	private final NumericValueParser numericValueParser;
	private final InstantValueParser instantValueParser;
	private final UnitValueParser unitValueParser;
	private final ValueTypeAnalyzer valueTypeAnalyzer;
	private final UuidGenerator uuidGenerator;

	private final boolean datesParsed;

	/**
	 * Instantiates the analyzer.
	 * 
	 * @param numericValueParser
	 *            used data type parser
	 * @param instantValueParser
	 *            used data type parser
	 * @param unitValueParser
	 *            used data type parser
	 * @param valueTypeAnalyzer
	 *            analyzer of individual cell values
	 * @param uuidGenerator
	 *            generator of UUIDs for
	 *            {@link eu.odalic.extrarelatable.model.bag.Label}s created
	 *            throughout the process
	 * @param datesParsed
	 *            turns on and off parsing of dates (even for type analysis) for
	 *            columns without existing type hint
	 */
	public DefaultTableAnalyzer(final NumericValueParser numericValueParser,
			final InstantValueParser instantValueParser, final UnitValueParser unitValueParser,
			final ValueTypeAnalyzer valueTypeAnalyzer, @Qualifier("UuidGenerator") final UuidGenerator uuidGenerator,
			final @org.springframework.beans.factory.annotation.Value("${eu.odalic.extrarelatable.datesParsed:false}") boolean datesParsed) {
		checkNotNull(numericValueParser);
		checkNotNull(unitValueParser);
		checkNotNull(instantValueParser);
		checkNotNull(valueTypeAnalyzer);
		checkNotNull(uuidGenerator);

		this.numericValueParser = numericValueParser;
		this.unitValueParser = unitValueParser;
		this.instantValueParser = instantValueParser;
		this.valueTypeAnalyzer = valueTypeAnalyzer;
		this.uuidGenerator = uuidGenerator;
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

		final List<Label> labels = readColumnLabels(table);
		final List<List<Value>> rows = inferRows(table, locale, columnTypeHints);

		return NestedListsTypedTable.fromRows(labels, rows, table.getMetadata());
	}

	private List<List<Value>> inferRows(final ParsedTable table, final Locale locale,
			final Map<? extends Integer, ? extends Type> columnTypeHints) {
		final List<List<String>> inputRows = table.getRows();
		final List<List<Value>> rows = inputRows.stream().map(row -> {
			final ImmutableList.Builder<Value> builder = ImmutableList.builder();

			for (int index = 0; index < row.size(); index++) {
				final String cell = row.get(index);

				if (valueTypeAnalyzer.isEmpty(cell)) {
					builder.add(EmptyValue.INSTANCE);
					continue;
				}

				// Determine the type of the cell either from a hint or by parsing attempts
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
		return rows;
	}

	private List<Label> readColumnLabels(final ParsedTable table) {
		final List<String> headers = table.getHeaders();
		final ImmutableList.Builder<Label> labelsBuilder = ImmutableList.builder();
		final int headersSize = headers.size();
		for (int headerIndex = 0; headerIndex < headersSize; headerIndex++) {
			final String header = headers.get(headerIndex);
			final String tableTitle = table.getMetadata().getTitle();
			final List<String> column = table.getColumn(headerIndex);
			final List<List<String>> rows = table.getRows();

			if (valueTypeAnalyzer.isEmpty(header)) {
				labelsBuilder.add(Label.synthetic(this.uuidGenerator.generate(), headerIndex, tableTitle,
						preview(column), headers, preview(rows)));
			} else {
				labelsBuilder.add(Label.of(this.uuidGenerator.generate(), header, null, false, headerIndex, tableTitle,
						preview(column), headers, preview(rows)));
			}
		}
		return labelsBuilder.build();
	}

	private static <T> List<T> preview(final List<T> list) {
		return list.subList(0, Math.min(list.size(), MAXIMUM_PREVIEW_SIZE));
	}
}
