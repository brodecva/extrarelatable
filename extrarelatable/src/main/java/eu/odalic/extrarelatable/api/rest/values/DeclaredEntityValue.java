package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.NavigableSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableSortedSet;

import eu.odalic.extrarelatable.model.table.DeclaredEntity;

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

	@XmlElement
	public URI getUri() {
		return uri;
	}

	public void setUri(final URI uri) {
		checkNotNull(uri);

		this.uri = uri;
	}

	@XmlElement
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