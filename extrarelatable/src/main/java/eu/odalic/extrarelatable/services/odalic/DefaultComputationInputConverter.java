package eu.odalic.extrarelatable.services.odalic;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.services.odalic.values.ComputationInputValue;

@Component
public class DefaultComputationInputConverter implements ComputationInputConverter {

	@Override
	public ComputationInputValue convert(final ParsedTable table, final int rowsLimit, final Random random) {
		checkNotNull(table);
		
		if (rowsLimit >= table.getHeight()) {
			return new ComputationInputValue(table.getRows(), table.getHeaders(), table.getMetadata().getTitle());
		} else {
			final List<List<String>> rows = new ArrayList<>(table.getRows());
			final int initialRowsSize = table.getHeight();
			final int toRemove = Math.max(0, initialRowsSize - rowsLimit);
			
			for (int i = 0; i < toRemove; i++) {
				final int removedIndex = random.nextInt(rows.size());
				rows.remove(removedIndex);
			}
			
			return new ComputationInputValue(rows, table.getHeaders(), table.getMetadata().getTitle());
		}
	}

}
