package eu.odalic.extrarelatable.input.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

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
   * @param format application CSV format
   * 
   * @return a {@link CSVFormat} instance derived from the application format
   */
  CSVFormat convert(Format format);
}
