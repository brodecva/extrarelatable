package eu.odalic.extrarelatable.model.annotation;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.odalic.extrarelatable.model.graph.PropertyTree.Node;
import eu.odalic.extrarelatable.util.UuidGenerator;

/**
 * Default implementation of {@link MeasuredNodeFactory}. It uses the
 * {@link UuidGenerator} provided during initialization to identify each
 * measured node with unique identifier.
 * 
 * @author Václav Brodec
 *
 */
@Service
public class DefaultMeasuredNodeFactory implements MeasuredNodeFactory {

	final UuidGenerator uuidGenerator;

	/**
	 * Initializes the factory.
	 * 
	 * @param uuidGenerator
	 *            the used {@link UuidGenerator}}
	 */
	public DefaultMeasuredNodeFactory(@Qualifier("UuidGenerator") final UuidGenerator uuidGenerator) {
		checkNotNull(uuidGenerator);

		this.uuidGenerator = uuidGenerator;
	}

	@Override
	public MeasuredNode create(Node node, double distance) {
		return new MeasuredNode(this.uuidGenerator.generate(), node, distance);
	}

}
