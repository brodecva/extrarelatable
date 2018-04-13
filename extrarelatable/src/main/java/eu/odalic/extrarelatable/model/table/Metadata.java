package eu.odalic.extrarelatable.model.table;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import eu.odalic.extrarelatable.api.rest.adapters.MetadataAdapter;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

@Immutable
@XmlJavaTypeAdapter(MetadataAdapter.class)
public final class Metadata implements Serializable {
	
	private static final long serialVersionUID = -8245805148358617046L;
	
	private final String title;
	private final String author;
	private final String languageTag;
	private final Map<Integer, URI> declaredPropertyUris;
	private final Map<Integer, URI> declaredClassUris;

	private Map<Integer, URI> collectedPropertyUris;

	private Map<Integer, URI> collectedClassUris;
	
	public Metadata(final String title, final String author, final String languageTag,
			final Map<? extends Integer, ? extends URI> declaredPropertyUris,
			final Map<? extends Integer, ? extends URI> declaredClassUris,
			final Map<? extends Integer, ? extends URI> collectedPropertyUris,
			final Map<? extends Integer, ? extends URI> collectedClassUris) {
		checkNotNull(declaredPropertyUris);
		checkNotNull(declaredClassUris);
		checkNotNull(collectedClassUris);
		checkNotNull(collectedPropertyUris);
		
		this.title = title;
		this.author = author;
		this.languageTag = languageTag;
		this.declaredPropertyUris = ImmutableMap.copyOf(declaredPropertyUris);
		this.declaredClassUris = ImmutableMap.copyOf(declaredClassUris);
		this.collectedClassUris = ImmutableMap.copyOf(collectedClassUris);
		this.collectedPropertyUris = ImmutableMap.copyOf(collectedPropertyUris);
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

	public Map<Integer, URI> getDeclaredPropertyUris() {
		return declaredPropertyUris;
	}

	public Map<Integer, URI> getDeclaredClassUris() {
		return declaredClassUris;
	}
	
	public Map<Integer, URI> getCollectedPropertyUris() {
		return collectedPropertyUris;
	}

	public Map<Integer, URI> getCollectedClassUris() {
		return collectedClassUris;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((collectedClassUris == null) ? 0 : collectedClassUris.hashCode());
		result = prime * result + ((collectedPropertyUris == null) ? 0 : collectedPropertyUris.hashCode());
		result = prime * result + ((declaredClassUris == null) ? 0 : declaredClassUris.hashCode());
		result = prime * result + ((declaredPropertyUris == null) ? 0 : declaredPropertyUris.hashCode());
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
		if (collectedClassUris == null) {
			if (other.collectedClassUris != null) {
				return false;
			}
		} else if (!collectedClassUris.equals(other.collectedClassUris)) {
			return false;
		}
		if (collectedPropertyUris == null) {
			if (other.collectedPropertyUris != null) {
				return false;
			}
		} else if (!collectedPropertyUris.equals(other.collectedPropertyUris)) {
			return false;
		}
		if (declaredClassUris == null) {
			if (other.declaredClassUris != null) {
				return false;
			}
		} else if (!declaredClassUris.equals(other.declaredClassUris)) {
			return false;
		}
		if (declaredPropertyUris == null) {
			if (other.declaredPropertyUris != null) {
				return false;
			}
		} else if (!declaredPropertyUris.equals(other.declaredPropertyUris)) {
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
				+ ", declaredPropertyUris=" + declaredPropertyUris + ", declaredClassUris=" + declaredClassUris
				+ ", collectedPropertyUris=" + collectedPropertyUris + ", collectedClassUris=" + collectedClassUris
				+ "]";
	}
}
