package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component("propertyUri")
public final class PropertyUriPropertyTreesMergingStrategy implements PropertyTreesMergingStrategy, Serializable {

	private static final long serialVersionUID = 596148026170222626L;

	@Override
	public Property find(final PropertyTree propertyTree, final Set<? extends Property> properties) {
		checkNotNull(propertyTree);
		checkNotNull(properties);
		
		final URI declaredPropertyUri = propertyTree.getContext().getDeclaredPropertyUri();
		
		return properties.stream().filter(
			property -> {
				final URI propertyUri = property.getUri();
				
				return propertyUri != null && propertyUri.equals(declaredPropertyUri);
			}
		).findFirst().orElse(null);
	}
}
