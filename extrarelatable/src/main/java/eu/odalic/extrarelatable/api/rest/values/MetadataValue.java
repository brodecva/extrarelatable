package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.webcohesion.enunciate.metadata.DocumentationExample;

import eu.odalic.extrarelatable.model.table.DeclaredEntity;
import eu.odalic.extrarelatable.model.table.Metadata;

/**
 * <p>Table meta-data. Next to optional values such as title, author and language
 * tag according to BCP 47 norm, it contains both the declared and collected
 * context for that table. The declared context is considered to be of high
 * quality, as it is a result of manual curation, whereas the collected one may
 * originate from unsupervised automatic processing.</p>
 * 
 * <p>{@link Metadata} adapted for REST API.</p>
 * 
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "metadata")
public final class MetadataValue implements Serializable {

	private static final long serialVersionUID = -1586827772971166587L;

	private String title;
	private String author;
	private String languageTag;
	private Map<Integer, DeclaredEntity> declaredProperties;
	private Map<Integer, DeclaredEntity> declaredClasses;
	private Map<Integer, DeclaredEntity> collectedProperties;
	private Map<Integer, DeclaredEntity> collectedClasses;

	public MetadataValue() {
		this(new Metadata());
	}

	public MetadataValue(final Metadata adaptee) {
		this.title = adaptee.getTitle();
		this.author = adaptee.getAuthor();
		this.languageTag = adaptee.getLanguageTag();
		this.declaredProperties = adaptee.getDeclaredProperties();
		this.declaredClasses = adaptee.getDeclaredClasses();
		this.collectedClasses = adaptee.getCollectedClasses();
		this.collectedProperties = adaptee.getCollectedProperties();
	}

	/**
	 * the table tiitle
	 * 
	 * @return the table tiitle
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("American cities")
	public String getTitle() {
		return title;
	}

	public void setTitle(@Nullable String title) {
		this.title = title;
	}

	/**
	 * the table author
	 * 
	 * @return the table author
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("John Smith")
	public String getAuthor() {
		return author;
	}

	public void setAuthor(@Nullable String author) {
		this.author = author;
	}

	/**
	 * BCP 47 norm language tag
	 * 
	 * @return BCP 47 norm language tag
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("en-US")
	public String getLanguageTag() {
		return languageTag;
	}

	public void setLanguageTag(@Nullable String languageTag) {
		this.languageTag = languageTag;
	}
	
	/**
	 * map of integer column indices to {@link DeclaredEntityValue} objects keeping the manually curated properties
	 * 
	 * @return map of integer column indices to {@link DeclaredEntityValue} objects keeping the manually curated properties
	 */
	@XmlElement
	public Map<Integer, DeclaredEntity> getDeclaredProperties() {
		return Collections.unmodifiableMap(declaredProperties);
	}

	public void setDeclaredProperties(Map<? extends Integer, ? extends DeclaredEntity>  declaredProperties) {
		checkNotNull(declaredProperties);
		
		this.declaredProperties = new HashMap<>(declaredProperties);
	}
	
	/**
	 * map of integer column indices to {@link DeclaredEntityValue} objects keeping the manually curated classes
	 * 
	 * @return map of integer column indices to {@link DeclaredEntityValue} objects keeping the manually curated classes
	 */
	@XmlElement
	public Map<Integer, DeclaredEntity> getDeclaredClasses() {
		return Collections.unmodifiableMap(declaredClasses);
	}

	public void setDeclaredClasses(Map<? extends Integer, ? extends DeclaredEntity>  declaredClasses) {
		checkNotNull(declaredClasses);
		
		this.declaredClasses = new HashMap<>(declaredClasses);
	}
	
	/**
	 * map of integer column indices to {@link DeclaredEntityValue} objects keeping the automatically collected properties
	 * 
	 * @return map of integer column indices to {@link DeclaredEntityValue} objects keeping the automatically collected properties
	 */
	@XmlElement
    public Map<Integer, DeclaredEntity> getCollectedProperties() {
        return Collections.unmodifiableMap(collectedProperties);
    }

    public void setCollectedProperties(Map<? extends Integer, ? extends DeclaredEntity>  collectedProperties) {
        checkNotNull(collectedProperties);
        
        this.collectedProperties = new HashMap<>(collectedProperties);
    }
    
    /**
	 * map of integer column indices to {@link DeclaredEntityValue} objects keeping the automatically collected classes
	 * 
	 * @return map of integer column indices to {@link DeclaredEntityValue} objects keeping the automatically collected classes
	 */
	@XmlElement
    public Map<Integer, DeclaredEntity> getCollectedClasses() {
        return Collections.unmodifiableMap(collectedClasses);
    }

    public void setCollectedClasses(Map<? extends Integer, ? extends DeclaredEntity>  collectedClasses) {
        checkNotNull(collectedClasses);
        
        this.collectedClasses = new HashMap<>(collectedClasses);
    }

	@Override
	public String toString() {
		return "MetadataValue [title=" + title + ", author=" + author + ", languageTag=" + languageTag
				+ ", declaredProperties=" + declaredProperties + ", declaredClasses=" + declaredClasses + "]";
	}
}
