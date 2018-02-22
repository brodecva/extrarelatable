package eu.odalic.extrarelatable.model.table;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Metadata implements Serializable {
	
	private static final long serialVersionUID = -8245805148358617046L;
	
	private final String title;
	private final String author;
	private final String languageTag;
	
	public Metadata(final String title, final String author, final String languageTag) {
		this.title = title;
		this.author = author;
		this.languageTag = languageTag;
	}

	public Metadata() {
		this(null, null, null);
	}
	
	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getLanguageTag() {
		return languageTag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((languageTag == null) ? 0 : languageTag.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Metadata other = (Metadata) obj;
		if (author == null) {
			if (other.author != null) {
				return false;
			}
		} else if (!author.equals(other.author)) {
			return false;
		}
		if (languageTag == null) {
			if (other.languageTag != null) {
				return false;
			}
		} else if (!languageTag.equals(other.languageTag)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Metadata [title=" + title + ", author=" + author + ", languageTag=" + languageTag + "]";
	}
}
