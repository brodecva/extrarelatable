package eu.odalic.extrarelatable.algorithms.table;

import java.util.Locale;

import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.TypedTable;

public interface TableAnalyzer {
	TypedTable infer(final ParsedTable table);

	TypedTable infer(ParsedTable table, Locale forcedLocale);
}
