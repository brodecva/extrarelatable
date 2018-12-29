package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableList;

import eu.odalic.extrarelatable.api.rest.adapters.SearchResultAdapter;

/**
 * Properties matching a search query done on {@link BackgroundKnowledgeGraph}.
 * 
 * @author Václav Brodec
 *
 */
@XmlJavaTypeAdapter(SearchResultAdapter.class)
public final class SearchResult implements Serializable {

	private static final long serialVersionUID = -7717008514761870971L;

	private List<Property> properties;

	/**
	 * Creates an empty search result.
	 */
	public SearchResult() {
		this.properties = ImmutableList.of();
	}

	/**
	 * Creates a search result.
	 * 
	 * @param properties matching properties
	 */
	public SearchResult(final List<? extends Property> properties) {
		checkNotNull(properties);
		
		this.properties = ImmutableList.copyOf(properties);
	}

	/**
	 * @return matching properties
	 */
	@XmlElement
	public List<Property> getProperties() {
		return properties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + properties.hashCode();
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
		final SearchResult other = (SearchResult) obj;
		if (!properties.equals(other.properties)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SearchResult [properties=" + properties + "]";
	}
}
