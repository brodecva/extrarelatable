package eu.odalic.extrarelatable.api.rest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.odalic.extrarelatable.api.rest.values.SearchResultValue;
import eu.odalic.extrarelatable.model.graph.SearchResult;

/**
 * Adapter of {@link SearchResult} to {@link SearchResultValue}.
 * 
 * @author Václav Brodec
 *
 */
public final class SearchResultAdapter extends XmlAdapter<SearchResultValue, SearchResult> {

	@Override
	public SearchResultValue marshal(final SearchResult bound) throws Exception {
		return new SearchResultValue(bound);
	}

	@Override
	public SearchResult unmarshal(final SearchResultValue value) throws Exception {
		throw new UnsupportedOperationException();
	}
}
