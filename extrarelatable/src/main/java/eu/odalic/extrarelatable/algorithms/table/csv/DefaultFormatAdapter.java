/**
 *
 */
package eu.odalic.extrarelatable.algorithms.table.csv;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.csv.CSVFormat;
import org.springframework.stereotype.Component;

import eu.odalic.extrarelatable.model.table.csv.Format;

/**
 * <p>
 * Default {@link FormatAdapter} implementation.
 * </p>
 * 
 * <p>
 * Adapted from Odalic.
 * </p>
 *
 * @author Jan Váňa
 * @author Václav Brodec
 *
 */
@Immutable
@Component
public final class DefaultFormatAdapter implements FormatAdapter {

  @Override
  public CSVFormat convert(final Format applicationFormat) {
    CSVFormat format = CSVFormat.newFormat(applicationFormat.getDelimiter())
        .withAllowMissingColumnNames().withIgnoreEmptyLines(applicationFormat.isEmptyLinesIgnored())
        .withRecordSeparator(applicationFormat.getLineSeparator());

    final Character quoteCharacter = applicationFormat.getQuoteCharacter();
    if (quoteCharacter != null) {
      format = format.withQuote(quoteCharacter);
    }

    format = format.withHeader(); // Must be present.

    final Character escapeCharacter = applicationFormat.getEscapeCharacter();
    if (escapeCharacter != null) {
      format = format.withEscape(escapeCharacter);
    }

    final Character commentMarker = applicationFormat.getCommentMarker();
    if (commentMarker != null) {
      format = format.withCommentMarker(commentMarker);
    }

    return format;
  }

}
