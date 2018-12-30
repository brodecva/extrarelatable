package eu.odalic.extrarelatable.algorithms.table.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import eu.odalic.extrarelatable.model.table.csv.Format;

/**
 * Adapter from {@link Format} to {@link CSVFormat}.
 *
 * @author Václav Brodec
 *
 */
public interface FormatAdapter {
	/**
	 * Converts to the CSV file formatting configuration used by {@link CSVParser}.
	 *
	 * @param format
	 *            application CSV format
	 * 
	 * @return a {@link CSVFormat} instance derived from the application format
	 */
	CSVFormat convert(Format format);
}
