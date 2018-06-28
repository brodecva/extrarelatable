package eu.odalic.extrarelatable.algorithms.subcontext;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.model.bag.Attribute;
import eu.odalic.extrarelatable.model.bag.NumberLikeValue;
import eu.odalic.extrarelatable.model.bag.Value;
import eu.odalic.extrarelatable.model.subcontext.NumericCell;
import eu.odalic.extrarelatable.model.subcontext.Partition;
import eu.odalic.extrarelatable.model.subcontext.Subcontext;
import eu.odalic.extrarelatable.model.table.TypedTable;

@Immutable
@Component
public class DefaultSubcontextCompiler implements SubcontextCompiler {

	@Override
	public Set<Subcontext> compile(final Partition partition,
			final Set<Integer> availableContextColumnIndices, final TypedTable table, final double minimumPartitionRelativeSize,
			final double maximumPartitionRelativeSize, final int minimumPartitionSize) {
		checkNotNull(partition);
		checkNotNull(availableContextColumnIndices);
		checkNotNull(table);
		checkArgument(minimumPartitionRelativeSize >= 0);
		checkArgument(maximumPartitionRelativeSize >= 0);
		checkArgument(maximumPartitionRelativeSize <= 1);
		checkArgument(minimumPartitionRelativeSize <= maximumPartitionRelativeSize);
		checkArgument(minimumPartitionSize >= 0);
		checkArgument(partition.size() >= minimumPartitionSize);
		
		final int parentalPartitionSize = partition.size();
		
		final ImmutableSet.Builder<Subcontext> subcontextsBuilder = ImmutableSet.builder();
		for (final Integer availableContextColumnIndex : availableContextColumnIndices) {
			final Subcontext candidateSubcontext = compile(availableContextColumnIndex, partition,
					table);
			if (candidateSubcontext == null) {
				continue;
			}
			
			final int largestPartitionSize = candidateSubcontext.getLargestPartitionSize();
			if (largestPartitionSize < minimumPartitionRelativeSize * parentalPartitionSize) {
				continue;
			}
			
			final int smallestPartitionSize = candidateSubcontext.getSmallestPartitionSize();
			if (smallestPartitionSize > maximumPartitionRelativeSize * parentalPartitionSize) {
				continue;
			}
			
			if (largestPartitionSize < minimumPartitionSize) {
				continue;
			}

			subcontextsBuilder.add(candidateSubcontext);
		}
		final Set<Subcontext> subcontexts = subcontextsBuilder.build();
		return subcontexts;
	}

	private Subcontext compile(int contextValuesColumnIndex, final Partition partition, TypedTable table) {
		checkArgument(contextValuesColumnIndex >= 0);
		checkArgument(contextValuesColumnIndex < table.getWidth());
		
		final Subcontext.Builder builder = Subcontext.builder();
		final List<Value> contextColumn = table.getColumn(contextValuesColumnIndex);
		boolean found = false;
		for (final Entry<Integer, NumberLikeValue> entry : partition.getCells().entrySet()) {
			final int rowIndex = entry.getKey();
			checkArgument(rowIndex < contextColumn.size());
			
			final Value contextColumnValue = contextColumn.get(rowIndex);
			
			builder.put(contextColumnValue, new NumericCell(rowIndex, entry.getValue()));
			found = true;
		}
		if (!found) {
			return null;
		}
		
		
		builder.setAttribute(new Attribute(table.getHeaders().get(contextValuesColumnIndex).getText()));
		builder.setColumnIndex(contextValuesColumnIndex);
		
		return builder.build();
	}
}
