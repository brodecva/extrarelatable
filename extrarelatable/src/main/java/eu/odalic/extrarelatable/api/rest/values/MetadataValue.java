package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.odalic.extrarelatable.model.table.DeclaredEntity;
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
	
	@XmlElement
	public Map<Integer, DeclaredEntity> getDeclaredProperties() {
		return Collections.unmodifiableMap(declaredProperties);
	}

	public void setDeclaredProperties(Map<? extends Integer, ? extends DeclaredEntity>  declaredProperties) {
		checkNotNull(declaredProperties);
		
		this.declaredProperties = new HashMap<>(declaredProperties);
	}
	
	@XmlElement
	public Map<Integer, DeclaredEntity> getDeclaredClasses() {
		return Collections.unmodifiableMap(declaredClasses);
	}

	public void setDeclaredClasses(Map<? extends Integer, ? extends DeclaredEntity>  declaredClasses) {
		checkNotNull(declaredClasses);
		
		this.declaredClasses = new HashMap<>(declaredClasses);
	}
	
	@XmlElement
    public Map<Integer, DeclaredEntity> getCollectedProperties() {
        return Collections.unmodifiableMap(collectedProperties);
    }

    public void setCollectedProperties(Map<? extends Integer, ? extends DeclaredEntity>  collectedProperties) {
        checkNotNull(collectedProperties);
        
        this.collectedProperties = new HashMap<>(collectedProperties);
    }
    
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
