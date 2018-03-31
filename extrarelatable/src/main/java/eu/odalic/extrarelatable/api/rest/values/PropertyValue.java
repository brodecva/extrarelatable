package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableList;

import eu.odalic.extrarelatable.model.graph.Property;

@XmlRootElement(name = "property")
public final class PropertyValue implements Serializable {
	
	private static final long serialVersionUID = 8601808486386249467L;

	private URI uri;
	
	private List<String> labels;
	
	public PropertyValue() {
		this.uri = null;
		this.labels = ImmutableList.of();
	}
	
	public PropertyValue(final Property adaptee) {
		this.uri = adaptee.getUri();
		this.labels = adaptee.getInstances().stream().map(instance -> instance.getRoot().getLabel().getText()).distinct().sorted().collect(ImmutableList.toImmutableList());
	}

	@XmlElement
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		checkNotNull(uri);
		
		this.uri = uri;
	}
	
	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(final List<? extends String> labels) {
		checkNotNull(labels);
		
		this.labels = ImmutableList.copyOf(labels);
	}

	@Override
	public String toString() {
		return "PropertyValue [uri=" + uri + ", labels=" + labels + "]";
	}
}
