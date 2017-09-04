package eu.odalic.extrarelatable.algorithms.subcontexts;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.model.bag.NumericValue;
import eu.odalic.extrarelatable.model.bag.TextValue;
import eu.odalic.extrarelatable.model.bag.Value;
import eu.odalic.extrarelatable.model.histogram.NumericCell;
import eu.odalic.extrarelatable.model.histogram.Partition;
import eu.odalic.extrarelatable.model.histogram.Subcontext;
import eu.odalic.extrarelatable.model.table.TypedTable;

@Component
public class DefaultSubcontextCompiler implements SubcontextCompiler {

	@Override
	public Set<Subcontext> compile(final Partition partition,
			final Set<Integer> availableContextColumnIndices, final TypedTable table, final double minimumPartitionRelativeSize,
			final double maximumPartitionRelativeSize) {
		final int parentalPartitionSize = partition.size();
		
		final ImmutableSet.Builder<Subcontext> subcontextsBuilder = ImmutableSet.builder();
		for (final Integer availableContextColumnIndex : availableContextColumnIndices) {
			final Subcontext candidateSubcontext = compile(availableContextColumnIndex, partition,
					table);
			final int largestPartitionSize = candidateSubcontext.getLargestPartitionSize();
			if (largestPartitionSize < minimumPartitionRelativeSize * parentalPartitionSize) {
				continue;
			}
			
			final int smallestPartitionSize = candidateSubcontext.getSmallestPartitionSize();
			if (smallestPartitionSize > maximumPartitionRelativeSize * parentalPartitionSize) {
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
		for (final Entry<Integer, NumericValue> entry : partition.getCells().entrySet()) {
			final int rowIndex = entry.getKey();
			checkArgument(rowIndex < contextColumn.size());
			
			final Value contextColumnValue = contextColumn.get(rowIndex);
			if (!contextColumnValue.isTextual()) {
				continue;
			}
			
			builder.put((TextValue) contextColumnValue, new NumericCell(rowIndex, entry.getValue()));
		}
		
		return builder.build();
	}
}
