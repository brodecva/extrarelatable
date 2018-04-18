package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

@Component("propertyUriLabelTextFallback")
public final class PropertyUriLabelTextFallbackPropertyTreesMergingStrategy implements PropertyTreesMergingStrategy, Serializable {

	private static final long serialVersionUID = 596148026170222626L;

	@Override
	public Property merge(final PropertyTree propertyTree, final Set<? extends Property> properties) {
		checkNotNull(propertyTree);
		checkNotNull(properties);
		
		final URI treeDeclaredPropertyUri = propertyTree.getContext().getDeclaredPropertyUri();
		
		final Property mergedByUri = properties.stream().filter(
			property -> {
				final URI propertyUri = property.getUri();
				
				return propertyUri != null && propertyUri.equals(treeDeclaredPropertyUri);
			}
		).findFirst().orElse(null);
		
		if (mergedByUri == null) {
			final String propertyTreeLabelText = getLabelText(propertyTree);
			
			final Property mergedByText = properties.stream().filter(
				property -> property.getInstances().stream().map(toLabelText()).anyMatch(equal(propertyTreeLabelText))
			).findFirst().orElse(null);
			
			if (mergedByText == null) {
				final Property newProperty = new Property();
				newProperty.add(propertyTree);
				newProperty.setUri(treeDeclaredPropertyUri);
				
				return newProperty;
			} else {
				mergedByText.add(propertyTree);
				if (mergedByText.getUri() == null) {
					if (treeDeclaredPropertyUri != null) {
						mergedByText.setUri(treeDeclaredPropertyUri);
					}
				}
				
				return null;
			}
		} else {
			mergedByUri.add(propertyTree);
			
			return null;
		}
	}

	private static String getLabelText(final PropertyTree propertyTree) {
		return propertyTree.getRoot().getLabel().getText();
	}
	
	private static Function<PropertyTree, String> toLabelText() {
		return p -> getLabelText(p);
	}

	private static Predicate<String> equal(final String labelText) {
		return t -> t.equals(labelText);
	}
}
