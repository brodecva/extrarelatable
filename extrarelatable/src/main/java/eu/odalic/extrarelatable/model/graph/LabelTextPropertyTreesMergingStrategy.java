package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

@Component("labelText")
public final class LabelTextPropertyTreesMergingStrategy implements PropertyTreesMergingStrategy, Serializable {

	private static final long serialVersionUID = -7006330829978202126L;

	@Override
	public Property merge(final PropertyTree propertyTree, final Set<? extends Property> properties) {
		checkNotNull(propertyTree);
		checkNotNull(properties);
		
		final String propertyTreeLabelText = getLabelText(propertyTree);
		
		final Property merged = properties.stream().filter(
			property -> property.getInstances().stream().map(toLabelText()).anyMatch(equal(propertyTreeLabelText))
		).findFirst().orElse(null);
		
		final URI treeDeclaredPropertyUri = propertyTree.getContext().getDeclaredPropertyUri();
		
		if (merged == null) {
			final Property newProperty = new Property();
			newProperty.add(propertyTree);
			newProperty.setUri(treeDeclaredPropertyUri);
			
			return newProperty;
		} else {
			merged.add(propertyTree);
			if (merged.getUri() == null) {
				if (treeDeclaredPropertyUri != null) {
					merged.setUri(treeDeclaredPropertyUri);
				}
			}
			
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
