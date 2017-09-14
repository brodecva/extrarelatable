package eu.odalic.extrarelatable.algorithms.graph;

import java.util.SortedSet;

import com.google.common.collect.SetMultimap;

import eu.odalic.extrarelatable.model.annotation.MeasuredNode;

public interface ResultAggregator {
	<T> SortedSet<T> aggregate(SetMultimap<T, ? extends MeasuredNode> levelAggregates);
}
