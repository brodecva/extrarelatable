package eu.odalic.extrarelatable.model.table;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableMap;

import eu.odalic.extrarelatable.api.rest.adapters.MetadataAdapter;

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
	
	public Metadata(final String title, final String author, final String languageTag) {
		this(title, author, languageTag, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of());
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

	public Map<Integer, DeclaredEntity> getDeclaredProperties() {
		return declaredProperties;
	}

	public Map<Integer, DeclaredEntity> getDeclaredClasses() {
		return declaredClasses;
	}
	
	public Map<Integer, DeclaredEntity> getCollectedProperties() {
		return collectedProperties;
	}

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
				+ ", collectedProperties=" + collectedProperties + ", collectedClasses=" + collectedClasses
				+ "]";
	}
}
