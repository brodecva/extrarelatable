package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.NavigableSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableSortedSet;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import eu.odalic.extrarelatable.model.table.DeclaredEntity;

/**
 * <p>
 * It encapsulates either the RDFS property or class that is a part of either
 * the declared or collected context associated with a table. The URI of the
 * property (or class) is accompanied by a list of associated text labels.
 * </p>
 * 
 * <p>
 * {@link DeclaredEntity} adapted for REST API.
 * </p>
 * 
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "declaredEntity")
public final class DeclaredEntityValue implements Serializable {

	private static final long serialVersionUID = -9009289293200568981L;

	private URI uri;

	private NavigableSet<String> labels;

	@SuppressWarnings("unused")
	private DeclaredEntityValue() {
		this.uri = null;
		this.labels = ImmutableSortedSet.of();
	}

	public DeclaredEntityValue(DeclaredEntity bound) {
		this.uri = bound.getUri();
		this.labels = ImmutableSortedSet.copyOf(bound.getLabels());
	}

	/**
	 * the URI
	 * 
	 * @return the URI
	 */
	@XmlElement
	@DocumentationExample("http://dbpedia.org/ontology/population")
	public URI getUri() {
		return uri;
	}

	public void setUri(final URI uri) {
		checkNotNull(uri);

		this.uri = uri;
	}

	/**
	 * the associated labels
	 * 
	 * @return the associated labels
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
		return "DeclaredEntityValue [uri=" + uri + ", labels=" + labels + "]";
	}
}
