package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.collections.impl.block.factory.Comparators;

import com.google.common.collect.ImmutableSortedSet;

import eu.odalic.extrarelatable.model.graph.Property;

@XmlRootElement(name = "property")
public final class PropertyValue implements Serializable {
	
	private static final long serialVersionUID = -3871722417767779928L;

	private UUID uuid;
	
	private URI uri;
	
	private SortedSet<String> labels;
	
	public PropertyValue() {
		this.uuid = null;
		this.uri = null;
		this.labels = ImmutableSortedSet.of();
	}
	
	public PropertyValue(final Property adaptee) {
		this.uuid = adaptee.getUuid();
		this.uri = adaptee.getUri();
		
		if (adaptee.getDeclaredLabels().isEmpty()) {
			this.labels = adaptee.getInstances().stream().map(instance -> instance.getRoot().getLabel().getText()).distinct().collect(ImmutableSortedSet.toImmutableSortedSet(Comparators.naturalOrder()));
		} else {
			this.labels = adaptee.getDeclaredLabels();
		}
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
	
	public Set<String> getLabels() {
		return labels;
	}

	public void setLabels(final Set<? extends String> labels) {
		checkNotNull(labels);
		
		this.labels = ImmutableSortedSet.copyOf(labels);
	}

	@Override
	public String toString() {
		return "PropertyValue [uri=" + uri + ", labels=" + labels + "]";
	}
}
