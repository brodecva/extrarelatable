package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import eu.odalic.extrarelatable.model.table.DeclaredEntity;

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
		
		final DeclaredEntity declaredProperty = propertyTree.getContext().getDeclaredProperty();
		final URI declaredPropertyUri = declaredProperty == null ? null : declaredProperty.getUri();
		final Set<String> declaredPropertyLabels = declaredProperty == null ? ImmutableSet.of() : declaredProperty.getLabels();
		
		if (merged == null) {
			final Property newProperty = new Property();
			newProperty.add(propertyTree);
			newProperty.setUri(declaredPropertyUri);
			newProperty.setDeclaredLabels(declaredPropertyLabels);
			
			return newProperty;
		} else {
			merged.add(propertyTree);
			if (merged.getUri() == null) {
				if (declaredPropertyUri != null) {
					merged.setUri(declaredPropertyUri);
				}
			}
			merged.setDeclaredLabels(Sets.union(merged.getDeclaredLabels(), declaredPropertyLabels).immutableCopy());
			
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
