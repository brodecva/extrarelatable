package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;

@Component("trivial")
public final class TrivialPropertyTreesMergingStrategy implements PropertyTreesMergingStrategy, Serializable {

	private static final long serialVersionUID = -7006330829978202126L;

	@Override
	public Property merge(final PropertyTree propertyTree, final Set<? extends Property> properties) {
		checkNotNull(propertyTree);
		checkNotNull(properties);
		
		final DeclaredEntity declaredProperty = propertyTree.getContext().getDeclaredProperty();
		final URI declaredPropertyUri = declaredProperty == null ? null : declaredProperty.getUri();
		final Set<String> declaredPropertyLabels = declaredProperty == null ? ImmutableSet.of() : declaredProperty.getLabels();
		
		final Property newProperty = new Property();
		newProperty.add(propertyTree);
		newProperty.setUri(declaredPropertyUri);
		newProperty.setDeclaredLabels(declaredPropertyLabels);
		
		return newProperty;
	}
}
