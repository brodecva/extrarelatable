package eu.odalic.extrarelatable.model.table;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableMap;

import eu.odalic.extrarelatable.api.rest.adapters.MetadataAdapter;

/**
 * Table meta-data. Next to optional values such as title, author and language
 * tag according to BCP 47 norm, it contains both the declared and collected
 * context for that table. The declared context is considered to be of high
 * quality, as it is a result of manual curation, whereas the collected one may
 * originate from unsupervised automatic processing.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
@XmlJavaTypeAdapter(MetadataAdapter.class)
public final class Metadata implements Serializable {

	private static final long serialVersionUID = -8245805148358617046L;

	private final String title;
	private final String author;
	private final String languageTag;
	private final Map<Integer, DeclaredEntity> declaredProperties;
	private final Map<Integer, DeclaredEntity> declaredClasses;
	private final Map<Integer, DeclaredEntity> collectedProperties;
	private final Map<Integer, DeclaredEntity> collectedClasses;

	/**
	 * Creates the meta-data object.
	 * 
	 * @param title
	 *            title of the table
	 * @param author
	 *            author of the table
	 * @param languageTag
	 *            language tag according to BCP 47 norm
	 * @param declaredProperties
	 *            manually curated properties assigned to the select columns (by
	 *            their indices)
	 * @param declaredClasses
	 *            manually curated classes assigned to the select columns (by their
	 *            indices)
	 * @param collectedProperties
	 *            automatically recognized properties assigned to the select columns
	 *            (by their indices)
	 * @param collectedClasses
	 *            automatically recognized classes assigned to the select columns
	 *            (by their indices)
	 */
	public Metadata(final String title, final String author, final String languageTag,
			final Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			final Map<? extends Integer, ? extends DeclaredEntity> declaredClasses,
			final Map<? extends Integer, ? extends DeclaredEntity> collectedProperties,
			final Map<? extends Integer, ? extends DeclaredEntity> collectedClasses) {
		checkNotNull(declaredProperties);
		checkNotNull(declaredClasses);
		checkNotNull(collectedClasses);
		checkNotNull(collectedProperties);

		this.title = title;
		this.author = author;
		this.languageTag = languageTag;
		this.declaredProperties = ImmutableMap.copyOf(declaredProperties);
		this.declaredClasses = ImmutableMap.copyOf(declaredClasses);
		this.collectedClasses = ImmutableMap.copyOf(collectedClasses);
		this.collectedProperties = ImmutableMap.copyOf(collectedProperties);
	}

	/**
	 * Creates the meta-data object without any context data.
	 * 
	 * @param title
	 *            title of the table
	 * @param author
	 *            author of the table
	 * @param languageTag
	 *            language tag according to BCP 47 norm of the content language of
	 *            the table
	 */
	public Metadata(final String title, final String author, final String languageTag) {
		this(title, author, languageTag, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of());
	}

	/**
	 * Creates an empty meta-data object.
	 */
	public Metadata() {
		this(null, null, null);
	}

	/**
	 * @return the table title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the author of the table
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return language tag of the table
	 */
	public String getLanguageTag() {
		return languageTag;
	}

	/**
	 * @return map of column indices to the properties they are manually annotated
	 *         by
	 */
	public Map<Integer, DeclaredEntity> getDeclaredProperties() {
		return declaredProperties;
	}

	/**
	 * @return map of column indices to the classes they are manually annotated by
	 */
	public Map<Integer, DeclaredEntity> getDeclaredClasses() {
		return declaredClasses;
	}

	/**
	 * @return map of column indices to the properties they are automatically
	 *         annotated by
	 */
	public Map<Integer, DeclaredEntity> getCollectedProperties() {
		return collectedProperties;
	}

	/**
	 * @return map of column indices to the classes they are automatically annotated
	 *         by
	 */
	public Map<Integer, DeclaredEntity> getCollectedClasses() {
		return collectedClasses;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((collectedClasses == null) ? 0 : collectedClasses.hashCode());
		result = prime * result + ((collectedProperties == null) ? 0 : collectedProperties.hashCode());
		result = prime * result + ((declaredClasses == null) ? 0 : declaredClasses.hashCode());
		result = prime * result + ((declaredProperties == null) ? 0 : declaredProperties.hashCode());
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
		if (collectedClasses == null) {
			if (other.collectedClasses != null) {
				return false;
			}
		} else if (!collectedClasses.equals(other.collectedClasses)) {
			return false;
		}
		if (collectedProperties == null) {
			if (other.collectedProperties != null) {
				return false;
			}
		} else if (!collectedProperties.equals(other.collectedProperties)) {
			return false;
		}
		if (declaredClasses == null) {
			if (other.declaredClasses != null) {
				return false;
			}
		} else if (!declaredClasses.equals(other.declaredClasses)) {
			return false;
		}
		if (declaredProperties == null) {
			if (other.declaredProperties != null) {
				return false;
			}
		} else if (!declaredProperties.equals(other.declaredProperties)) {
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
		return "Metadata [title=" + title + ", author=" + author + ", languageTag=" + languageTag
				+ ", declaredProperties=" + declaredProperties + ", declaredClasses=" + declaredClasses
				+ ", collectedProperties=" + collectedProperties + ", collectedClasses=" + collectedClasses + "]";
	}
}
