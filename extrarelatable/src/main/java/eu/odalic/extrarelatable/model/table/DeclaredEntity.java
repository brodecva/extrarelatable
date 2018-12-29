package eu.odalic.extrarelatable.model.table;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.NavigableSet;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableSortedSet;

import eu.odalic.extrarelatable.api.rest.adapters.DeclaredEntityAdapter;

/**
 * This class encapsulates either the RDFS property or class that is a part of
 * either the declared or collected context associated with a table. The URI of
 * the property (or class) is accompanied by a list of associated text labels.
 * 
 * @author Václav Brodec
 *
 */
@XmlJavaTypeAdapter(DeclaredEntityAdapter.class)
public final class DeclaredEntity implements Serializable {

	private static final long serialVersionUID = -9009289293200568981L;

	private final URI uri;

	private final NavigableSet<String> labels;

	public DeclaredEntity(final URI uri, final Set<? extends String> labels) {
		checkNotNull(uri);
		checkNotNull(labels);

		this.uri = uri;
		this.labels = ImmutableSortedSet.copyOf(labels);
	}

	/**
	 * @return entity URI
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * @return labels associated with the entity
	 */
	public NavigableSet<String> getLabels() {
		return labels;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		DeclaredEntity other = (DeclaredEntity) obj;
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
		return uri + " " + labels;
	}
}
