package eu.odalic.extrarelatable.api.rest.values;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Preconditions;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import eu.odalic.extrarelatable.model.table.csv.Format;

/**
 * <p>
 * Format of the CSV file. Essential for reliable parsing.
 * </p>
 * 
 * <p>
 * {@link Format} adapted for REST API.
 * </p>
 *
 * @author Václav Brodec
 */
@XmlRootElement(name = "format")
public final class FormatValue implements Serializable {

	private static final long serialVersionUID = -1586827772971166587L;

	private String charset;
	private char delimiter;
	private boolean emptyLinesIgnored;
	private Character quoteCharacter;
	private Character escapeCharacter;
	private Character commentMarker;

	public FormatValue() {
		this(new Format());
	}

	public FormatValue(final Format adaptee) {
		this.charset = adaptee.getCharset().name();
		this.delimiter = adaptee.getDelimiter();
		this.emptyLinesIgnored = adaptee.isEmptyLinesIgnored();
		this.quoteCharacter = adaptee.getQuoteCharacter();
		this.escapeCharacter = adaptee.getEscapeCharacter();
		this.commentMarker = adaptee.getCommentMarker();
	}

	/**
	 * the commentMarker
	 * 
	 * @return the commentMarker
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("=")
	public Character getCommentMarker() {
		return this.commentMarker;
	}

	/**
	 * the delimiter
	 * 
	 * @return the delimiter
	 */
	@XmlElement
	@DocumentationExample(",")
	@TypeHint(String.class)
	public char getDelimiter() {
		return this.delimiter;
	}

	/**
	 * the escapeCharacter
	 * 
	 * @return the escapeCharacter
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("\\")
	public Character getEscapeCharacter() {
		return this.escapeCharacter;
	}

	/**
	 * the character set
	 * 
	 * @return the character set
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("UTF-8")
	public String getCharset() {
		return this.charset;
	}

	/**
	 * the quote character
	 * 
	 * @return the quote character
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("\"")
	public Character getQuoteCharacter() {
		return this.quoteCharacter;
	}

	/**
	 * empty lines ignored
	 * 
	 * @return empty lines ignored
	 */
	@XmlElement
	public boolean isEmptyLinesIgnored() {
		return this.emptyLinesIgnored;
	}

	/**
	 * @param commentMarker
	 *            the commentMarker to set
	 */
	public void setCommentMarker(@Nullable final Character commentMarker) {
		this.commentMarker = commentMarker;
	}

	/**
	 * @param delimiter
	 *            the delimiter to set
	 */
	public void setDelimiter(final char delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @param emptyLinesIgnored
	 *            the emptyLinesIgnored to set
	 */
	public void setEmptyLinesIgnored(final boolean emptyLinesIgnored) {
		this.emptyLinesIgnored = emptyLinesIgnored;
	}

	/**
	 * @param escapeCharacter
	 *            the escapeCharacter to set
	 */
	public void setEscapeCharacter(@Nullable final Character escapeCharacter) {
		this.escapeCharacter = escapeCharacter;
	}

	/**
	 * @param charset
	 *            the character set to set
	 */
	public void setCharset(final String charset) {
		Preconditions.checkNotNull(charset, "The charset cannot be null!");

		this.charset = charset;
	}

	/**
	 * @param quoteCharacter
	 *            the quoteCharacter to set
	 */
	public void setQuoteCharacter(@Nullable final Character quoteCharacter) {
		this.quoteCharacter = quoteCharacter;
	}

	@Override
	public String toString() {
		return "FormatValue [charset=" + this.charset + ", delimiter=" + this.delimiter + ", emptyLinesIgnored="
				+ this.emptyLinesIgnored + ", quoteCharacter=" + this.quoteCharacter + ", escapeCharacter="
				+ this.escapeCharacter + ", commentMarker=" + this.commentMarker + "]";
	}
}
