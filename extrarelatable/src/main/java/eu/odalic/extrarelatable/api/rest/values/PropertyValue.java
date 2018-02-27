package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.odalic.extrarelatable.model.graph.Property;

@XmlRootElement(name = "property")
public final class PropertyValue implements Serializable {
	
	private static final long serialVersionUID = 8601808486386249467L;

	private URI uri;
	
	public PropertyValue() {
	}
	
	public PropertyValue(final Property adaptee) {
		this.uri = adaptee.getUri();
	}

	@XmlElement
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		checkNotNull(uri);
		
		this.uri = uri;
	}

	@Override
	public String toString() {
		return "PropertyValue [uri=" + uri + "]";
	}
}
