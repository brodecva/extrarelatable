package eu.odalic.extrarelatable.services.odalic;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.stereotype.Component;

import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.services.odalic.values.ComputationInputValue;

@Component
public class DefaultComputationInputConverter implements ComputationInputConverter {

	@Override
	public ComputationInputValue convert(final ParsedTable table) {
		checkNotNull(table);
		
		return new ComputationInputValue(table.getRows(), table.getHeaders(), table.getMetadata().getTitle());
	}

}
