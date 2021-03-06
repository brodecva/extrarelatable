package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import eu.odalic.extrarelatable.model.table.DeclaredEntity;
import eu.odalic.extrarelatable.util.UuidGenerator;

/**
 * A merge strategy that attempts to merge the {@link PropertyTree} into an
 * existing {@link Property} on comparison of the URI declared for that tree and
 * the URI of the candidate property.
 * 
 * @author Václav Brodec
 *
 */
@Component("propertyUri")
public final class PropertyUriPropertyTreesMergingStrategy implements PropertyTreesMergingStrategy, Serializable {

	private static final long serialVersionUID = 596148026170222626L;

	private final UuidGenerator uuidGenerator;

	@Autowired
	public PropertyUriPropertyTreesMergingStrategy(@Qualifier("UuidGenerator") final UuidGenerator uuidGenerator) {
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

		final Property merged = properties.stream().filter(property -> {
			final URI propertyUri = property.getUri();

			return propertyUri != null && propertyUri.equals(declaredPropertyUri);
		}).findFirst().orElse(null);

		if (merged == null) {
			final Property newProperty = new Property(this.uuidGenerator.generate());
			newProperty.add(propertyTree);
			newProperty.setUri(declaredPropertyUri);
			newProperty.setDeclaredLabels(declaredPropertyLabels);

			return newProperty;
		} else {
			merged.add(propertyTree);
			merged.setDeclaredLabels(Sets.union(merged.getDeclaredLabels(), declaredPropertyLabels).immutableCopy());

			return null;
		}
	}
}
