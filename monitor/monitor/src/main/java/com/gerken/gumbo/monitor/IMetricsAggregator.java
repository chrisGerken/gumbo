package com.gerken.gumbo.monitor;

import java.util.Collection;

public interface IMetricsAggregator {

	public abstract void increment(String metricGroup, String metric,
			Long increase, Collection<Integer> ids);

	public abstract void increment(String metricGroup, String metric,
			Long increase, Integer id);

}