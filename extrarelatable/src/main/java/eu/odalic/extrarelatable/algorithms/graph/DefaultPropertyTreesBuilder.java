package eu.odalic.extrarelatable.algorithms.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;
import eu.odalic.extrarelatable.model.graph.PropertyTree;
import eu.odalic.extrarelatable.model.table.SlicedTable;

@Component
public class DefaultPropertyTreesBuilder implements PropertyTreesBuilder {

	private final PropertyTreeBuilder propertyTreeBuilder;
	
	public DefaultPropertyTreesBuilder(final PropertyTreeBuilder propertyTreeBuilder) {
		checkNotNull(propertyTreeBuilder);
		
		this.propertyTreeBuilder = propertyTreeBuilder;
	}

	@Override
	public Set<PropertyTree> build(SlicedTable slicedTable) {
		return slicedTable.getDataColumns().keySet().stream().map(columnIndex -> 
			this.propertyTreeBuilder.build(slicedTable, columnIndex)
		).collect(ImmutableSet.toImmutableSet());
	}
}
