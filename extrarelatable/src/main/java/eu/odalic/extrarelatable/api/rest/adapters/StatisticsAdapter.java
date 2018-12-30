package eu.odalic.extrarelatable.api.rest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.odalic.extrarelatable.api.rest.values.StatisticsValue;
import eu.odalic.extrarelatable.model.annotation.Statistics;

/**
 * Adapter of {@link Statistics} to {@link StatisticsValue}.
 * 
 * @author Václav Brodec
 *
 */
public final class StatisticsAdapter extends XmlAdapter<StatisticsValue, Statistics> {

	@Override
	public StatisticsValue marshal(final Statistics bound) throws Exception {
		return new StatisticsValue(bound);
	}

	@Override
	public Statistics unmarshal(final StatisticsValue value) throws Exception {
		throw new UnsupportedOperationException();
	}
}
