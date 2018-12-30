package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.NavigableSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.collections.impl.block.factory.Comparators;

import com.google.common.collect.ImmutableSortedSet;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import eu.odalic.extrarelatable.model.graph.Property;

/**
 * <p>
 * RDFS property representation within the context of background knowledge base
 * derived from learned files. It encapsulates its URI (which may be left-out),
 * internal unique identifier and all the labels (collected from column headers)
 * associated with it.
 * </p>
 * 
 * <p>
 * {@link Property} adapted for REST API.
 * </p>
 * 
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "property")
public final class PropertyValue implements Serializable {

	private static final long serialVersionUID = -3871722417767779928L;

	private UUID uuid;

	private URI uri;

	private NavigableSet<String> labels;

	@SuppressWarnings("unused")
	private PropertyValue() {
		this.uuid = null;
		this.uri = null;
		this.labels = ImmutableSortedSet.of();
	}

	public PropertyValue(final Property adaptee) {
		this.uuid = adaptee.getUuid();
		this.uri = adaptee.getUri();

		if (adaptee.getDeclaredLabels().isEmpty()) {
			this.labels = adaptee.getInstances().stream().map(instance -> instance.getRoot().getLabel().getText())
					.distinct().collect(ImmutableSortedSet.toImmutableSortedSet(Comparators.naturalOrder()));
		} else {
			this.labels = adaptee.getDeclaredLabels();
		}
	}

	/**
	 * UUID assigned by ERT instance to the property
	 * 
	 * @return UUID assigned by ERT instance to the property
	 */
	@XmlElement
	@DocumentationExample("123e4567-e89b-12d3-a456-426655440000")
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * the URI
	 * 
	 * @return the URI
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("http://dbpedia.org/ontology/population")
	public URI getUri() {
		return uri;
	}

	public void setUuid(UUID uuid) {
		checkNotNull(uuid);

		this.uuid = uuid;
	}

	public void setUri(@Nullable URI uri) {
		this.uri = uri;
	}

	/**
	 * associated labels
	 * 
	 * @return associated labels
	 */
	@XmlElement
	@DocumentationExample(value = "Population", value2 = "Population as of 2010")
	@TypeHint(String[].class)
	public NavigableSet<String> getLabels() {
		return labels;
	}

	public void setLabels(final Set<? extends String> labels) {
		checkNotNull(labels);

		this.labels = ImmutableSortedSet.copyOf(labels);
	}

	@Override
	public String toString() {
		return "PropertyValue [uuid=" + uuid + ", uri=" + uri + ", labels=" + labels + "]";
	}
}
