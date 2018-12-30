package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.google.common.collect.ImmutableList;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import eu.odalic.extrarelatable.model.graph.Property;
import eu.odalic.extrarelatable.model.graph.SearchResult;

/**
 * Properties matching a search query executed on a graph.
 * 
 * <p>
 * {@link SearchResult} adapted for REST API.
 * </p>
 * 
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "searchResult")
public final class SearchResultValue implements Serializable {

	private static final long serialVersionUID = -7717008514761870971L;

	private List<Property> properties;

	public SearchResultValue() {
		this.properties = ImmutableList.of();
	}

	public SearchResultValue(final SearchResult searchResult) {
		this.properties = searchResult.getProperties();
	}

	/**
	 * the matching {@link PropertyValue}s
	 * 
	 * @return the matching {@link PropertyValue}s
	 */
	@XmlElement
	@TypeHint(PropertyValue[].class)
	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<? extends Property> properties) {
		checkNotNull(properties);

		this.properties = ImmutableList.copyOf(properties);
	}

	@Override
	public String toString() {
		return "SearchResultValue [properties=" + properties + "]";
	}
}
