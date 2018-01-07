package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class Property implements Iterable<PropertyTree> {
	
	private URI uri;
	
	private final Set<PropertyTree> instances;
	
	public Property() {
		this.setUri(null);
		this.instances = new HashSet<>();
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public Set<PropertyTree> getInstances() {
		return Collections.unmodifiableSet(instances);
	}

	public void add(final PropertyTree instance) {
		checkNotNull(instance);
		
		instance.setProperty(this);
		this.instances.add(instance);
	}
	
	public void addAll(final Set<? extends PropertyTree> instances) {
		checkNotNull(instances);
		instances.forEach(instance -> checkNotNull(instance));
		
		instances.forEach(instance -> instance.setProperty(this));
		this.instances.addAll(instances);
	}

	@Override
	public Iterator<PropertyTree> iterator() {
		return instances.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((instances == null) ? 0 : instances.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		if (instances == null) {
			if (other.instances != null) {
				return false;
			}
		} else if (!instances.equals(other.instances)) {
			return false;
		}
		if (uri == null) {
			if (other.uri != null) {
				return false;
			}
		} else if (!uri.equals(other.uri)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Property [uri=" + uri + ", instances=" + instances + "]";
	}
}
