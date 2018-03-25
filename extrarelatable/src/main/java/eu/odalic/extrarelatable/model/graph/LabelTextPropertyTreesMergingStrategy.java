package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

@Component("labelText")
public final class LabelTextPropertyTreesMergingStrategy implements PropertyTreesMergingStrategy, Serializable {

	private static final long serialVersionUID = -7006330829978202126L;

	@Override
	public Property find(final PropertyTree propertyTree, final Set<? extends Property> properties) {
		checkNotNull(propertyTree);
		checkNotNull(properties);
		
		final String propertyTreeLabelText = getLabelText(propertyTree);
		
		return properties.stream().filter(
			property -> property.getInstances().stream().map(toLabelText()).anyMatch(equal(propertyTreeLabelText))
		).findFirst().orElse(null);
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
