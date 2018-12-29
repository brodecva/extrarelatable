package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSortedSet;

import eu.odalic.extrarelatable.api.rest.adapters.PropertyAdapter;

/**
 * RDFS property representation within the context of background knowledge base
 * derived from learned files. It encapsulates its URI (which may be left-out), internal unique
 * identifier, all the label (collected from column headers) associated with it
 * and finally the set of {@link PropertyTree}s, which are instances of the
 * property as found in the learned files.
 * 
 * @author Václav Brodec
 *
 */
@XmlJavaTypeAdapter(PropertyAdapter.class)
public final class Property implements Iterable<PropertyTree>, Comparable<Property>, Serializable {

	private static final long serialVersionUID = 3889990772214135061L;

	private final UUID uuid;

	private URI uri;

	private NavigableSet<String> declaredLabels;

	private final Set<PropertyTree> instances;

	/**
	 * Creates a property.
	 * 
	 * @param uuid UUID of the property
	 */
	public Property(final UUID uuid) {
		checkNotNull(uuid);

		this.uuid = uuid;
		this.uri = null;
		this.declaredLabels = ImmutableSortedSet.of();
		this.instances = new HashSet<>();
	}

	/**
	 * Copies the property.
	 * 
	 * @param original copied instance
	 */
	public Property(final Property original) {
		this.uuid = original.uuid;
		this.uri = original.uri;
		this.declaredLabels = original.declaredLabels;
		this.instances = new HashSet<>(original.instances);
	}

	/**
	 * @return the identifier of the property
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * @return URI of the property, null if unknown or not established
	 */
	@Nullable
	public URI getUri() {
		return uri;
	}

	/**
	 * @param uri the URI to set
	 */
	public void setUri(@Nullable URI uri) {
		this.uri = uri;
	}

	/**
	 * @return labels associated with the property
	 */
	public NavigableSet<String> getDeclaredLabels() {
		return declaredLabels;
	}

	public void setDeclaredLabels(final Set<? extends String> declaredLabels) {
		checkNotNull(declaredLabels);

		this.declaredLabels = ImmutableSortedSet.copyOf(declaredLabels);
	}

	/**
	 * @return the property trees merged into this property
	 */
	@XmlTransient
	@JsonIgnore
	public Set<PropertyTree> getInstances() {
		return Collections.unmodifiableSet(instances);
	}

	/**
	 * Adds property tree, an instance of the property in some learned file.
	 * 
	 * @param instance property tree instance
	 */
	@XmlTransient
	@JsonIgnore
	public void add(final PropertyTree instance) {
		checkNotNull(instance);

		instance.setProperty(this);
		this.instances.add(instance);
	}

	/**
	 * Executes {@link #add(PropertyTree)} in bulk.
	 * 
	 * @param instances property tree instances
	 */
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
		return "Property [uuid=" + uuid + ", uri=" + uri + ", declaredLabels=" + declaredLabels + ", instances="
				+ instances + "]";
	}
}
