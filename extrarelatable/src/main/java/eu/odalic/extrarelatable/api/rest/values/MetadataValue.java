package eu.odalic.extrarelatable.api.rest.values;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.odalic.extrarelatable.model.table.Metadata;

/**
 * Domain class {@link Metadata} adapted for REST API.
 *
 * @author Václav Brodec
 */
@XmlRootElement(name = "metadata")
public final class MetadataValue implements Serializable {

	private static final long serialVersionUID = -1586827772971166587L;

	private String title;
	private String author;
	private String languageTag;

	public MetadataValue() {
		this(new Metadata());
	}

	public MetadataValue(final Metadata adaptee) {
		this.title = adaptee.getTitle();
		this.author = adaptee.getAuthor();
		this.languageTag = adaptee.getLanguageTag();
	}

	@XmlElement
	@Nullable
	public String getTitle() {
		return title;
	}

	public void setTitle(@Nullable String title) {
		this.title = title;
	}

	@XmlElement
	@Nullable
	public String getAuthor() {
		return author;
	}

	public void setAuthor(@Nullable String author) {
		this.author = author;
	}

	@XmlElement
	@Nullable
	public String getLanguageTag() {
		return languageTag;
	}

	public void setLanguageTag(@Nullable String languageTag) {
		this.languageTag = languageTag;
	}

	@Override
	public String toString() {
		return "MetadataValue [title=" + title + ", author=" + author + ", languageTag=" + languageTag + "]";
	}
}
