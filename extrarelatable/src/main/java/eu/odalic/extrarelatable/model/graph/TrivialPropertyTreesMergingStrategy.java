package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;
import eu.odalic.extrarelatable.util.UuidGenerator;

/**
 * A merge strategy that does not attempt to merge the {@link PropertyTree} into
 * an existing {@link Property}. Instead it immediately creates a new one for
 * it.
 * 
 * @author Václav Brodec
 *
 */
@Component("trivial")
public final class TrivialPropertyTreesMergingStrategy implements PropertyTreesMergingStrategy, Serializable {

	private static final long serialVersionUID = -7006330829978202126L;

	private final UuidGenerator uuidGenerator;

	@Autowired
	public TrivialPropertyTreesMergingStrategy(@Qualifier("UuidGenerator") final UuidGenerator uuidGenerator) {
		checkNotNull(uuidGenerator);

		this.uuidGenerator = uuidGenerator;
	}

	@Override
	public Property merge(final PropertyTree propertyTree, final Set<? extends Property> properties) {
		checkNotNull(propertyTree);
		checkNotNull(properties);

		final DeclaredEntity declaredProperty = propertyTree.getContext().getDeclaredProperty();
		final URI declaredPropertyUri = declaredProperty == null ? null : declaredProperty.getUri();
		final Set<String> declaredPropertyLabels = declaredProperty == null ? ImmutableSet.of()
				: declaredProperty.getLabels();

		final Property newProperty = new Property(this.uuidGenerator.generate());
		newProperty.add(propertyTree);
		newProperty.setUri(declaredPropertyUri);
		newProperty.setDeclaredLabels(declaredPropertyLabels);

		return newProperty;
	}
}
