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

@Component
public class DefaultPropertyTreesBuilder implements PropertyTreesBuilder {

	private final PropertyTreeBuilder propertyTreeBuilder;
	
	public DefaultPropertyTreesBuilder(final PropertyTreeBuilder propertyTreeBuilder) {
		checkNotNull(propertyTreeBuilder);
		
		this.propertyTreeBuilder = propertyTreeBuilder;
	}

	@Override
	public Set<PropertyTree> build(final SlicedTable slicedTable) {
		return build(slicedTable, ImmutableMap.of(), ImmutableMap.of(), false);
	}

	@Override
	public Set<PropertyTree> build(final SlicedTable slicedTable, final Map<? extends Integer, ? extends DeclaredEntity> declaredProperties,
			final Map<? extends Integer, ? extends DeclaredEntity> declaredClasses,
			final boolean onlyWithProperties) {
		return slicedTable.getDataColumns().keySet().stream().map(columnIndex -> 
			this.propertyTreeBuilder.build(slicedTable, columnIndex, declaredProperties, declaredClasses, onlyWithProperties)
		).filter(tree -> tree != null).collect(ImmutableSet.toImmutableSet());
	}
}
