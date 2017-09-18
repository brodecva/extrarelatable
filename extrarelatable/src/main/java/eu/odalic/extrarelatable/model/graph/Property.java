package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class Property implements Iterable<PropertyTree> {
	private final Set<PropertyTree> instances;
	
	public Property() {
		this.instances = new HashSet<>();
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
		result = prime * result + instances.hashCode();
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
		final Property other = (Property) obj;
		if (!instances.equals(other.instances)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Property [instances=" + instances + "]";
	}
}
