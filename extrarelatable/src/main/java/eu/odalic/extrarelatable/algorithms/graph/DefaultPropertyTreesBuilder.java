package eu.odalic.extrarelatable.algorithms.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.table.DeclaredEntity;
import eu.odalic.extrarelatable.model.table.SlicedTable;

/**
 * Default implementation of {@link PropertyTreesBuilder}.
 * 
 * @author Václav Brodec
 *
 */
@Component
public class DefaultPropertyTreesBuilder implements PropertyTreesBuilder {

	private final PropertyTreeBuilder propertyTreeBuilder;
	
	/**
	 * Constructs a set builder from a builder for a single tree.
	 * 
	 * @param propertyTreeBuilder the builder used to build the individual trees
	 */
	public DefaultPropertyTreesBuilder(final PropertyTreeBuilder propertyTreeBuilder) {
		checkNotNull(propertyTreeBuilder);
		
		this.propertyTreeBuilder = propertyTreeBuilder;
	}

	@Override
	public Set<PropertyTree> build(final SlicedTable slicedTable) {
		return build(slicedTable, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), false, false);
	}

	@Override
	public Set<PropertyTree> build(final SlicedTable slicedTable, final Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			final Map<? extends Integer, ? extends DeclaredEntity> declaredClasses,
			final Map<? extends Integer, ? extends DeclaredEntity> contextProperties,
			final Map<? extends Integer, ? extends DeclaredEntity> contextClasses, final boolean onlyWithProperties, final boolean onlyDeclaredAsContext) {
		return slicedTable.getDataColumns().keySet().stream().map(columnIndex -> 
		this.propertyTreeBuilder.build(slicedTable, columnIndex, declaredProperties, declaredClasses, contextProperties, contextClasses, onlyWithProperties, onlyDeclaredAsContext)
	).filter(tree -> tree != null).collect(ImmutableSet.toImmutableSet());
	}
}
