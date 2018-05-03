package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableList;

import eu.odalic.extrarelatable.model.graph.Property;

@XmlRootElement(name = "property")
public final class PropertyValue implements Serializable {
	
	private static final long serialVersionUID = -3871722417767779928L;

	private UUID uuid;
	
	private URI uri;
	
	private List<String> labels;
	
	public PropertyValue() {
		this.uuid = null;
		this.uri = null;
		this.labels = ImmutableList.of();
	}
	
	public PropertyValue(final Property adaptee) {
		this.uuid = adaptee.getUuid();
		this.uri = adaptee.getUri();
		this.labels = adaptee.getInstances().stream().map(instance -> instance.getRoot().getLabel().getText()).distinct().sorted().collect(ImmutableList.toImmutableList());
	}

	@XmlElement
	public UUID getUuid() {
		return uuid;
	}
	
	@XmlElement
	public URI getUri() {
		return uri;
	}

	public void setUuid(UUID uuid) {
		checkNotNull(uuid);
		
		this.uuid = uuid;
	}
	
	public void setUri(URI uri) {
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
