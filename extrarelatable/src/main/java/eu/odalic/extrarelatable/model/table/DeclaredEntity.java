package eu.odalic.extrarelatable.model.table;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.api.rest.adapters.DeclaredEntityAdapter;

@XmlJavaTypeAdapter(DeclaredEntityAdapter.class)
public final class DeclaredEntity implements Serializable {

	private static final long serialVersionUID = -9009289293200568981L;

	private final URI uri;

	private final Set<String> labels;

	public DeclaredEntity(final URI uri, final List<? extends String> labels) {
		checkNotNull(uri);
		checkNotNull(labels);

		this.uri = uri;
		this.labels = ImmutableSet.copyOf(labels);
	}

	public URI getUri() {
		return uri;
	}

	public Set<String> getLabels() {
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
		return "DeclaredEntity [uri=" + uri + ", labels=" + labels + "]";
	}
}
