package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.odalic.extrarelatable.api.rest.adapters.PropertyAdapter;

@XmlJavaTypeAdapter(PropertyAdapter.class)
public final class Property implements Iterable<PropertyTree>, Comparable<Property>, Serializable {
	
	private static final long serialVersionUID = 3889990772214135061L;

	private final UUID uuid;
	
	private URI uri;
	
	private final Set<PropertyTree> instances;
	
	public Property() {
		this.uuid = UUID.randomUUID();
		this.uri = null;
		this.instances = new HashSet<>();
	}
	
	public Property(final Property original) {
		this.uuid = original.uuid;
		this.uri = original.uri;
		this.instances = new HashSet<>(original.instances);
	}

	public UUID getUuid() {
		return uuid;
	}
	
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	@XmlTransient
	@JsonIgnore
	public Set<PropertyTree> getInstances() {
		return Collections.unmodifiableSet(instances);
	}

	@XmlTransient
	@JsonIgnore
	public void add(final PropertyTree instance) {
		checkNotNull(instance);
		
		instance.setProperty(this);
		this.instances.add(instance);
	}
	
	@XmlTransient
	@JsonIgnore
	public void addAll(final Set<? extends PropertyTree> instances) {
		checkNotNull(instances);
		instances.forEach(instance -> checkNotNull(instance));
		
		instances.forEach(instance -> instance.setProperty(this));
		this.instances.addAll(instances);
	}

	@Override
	@XmlTransient
	@JsonIgnore
	public Iterator<PropertyTree> iterator() {
		return instances.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + uuid.hashCode();
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
		Property other = (Property) obj;
		if (uuid == null) {
			if (other.uuid != null) {
				return false;
			}
		} else if (!uuid.equals(other.uuid)) {
			return false;
		}
		return true;
	}

	@XmlTransient
	@JsonIgnore
	public int compareTo(final Property other) {
		return uuid.compareTo(other.uuid);
	}
	
	@Override
	public String toString() {
		return "Property [uuid=" + uuid + ", uri=" + uri + ", instances=" + instances + "]";
	}
}
