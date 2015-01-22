package com.gerken.gumbo.monitor;

import java.util.Collection;

public interface IMetricsAggregator {

	public void increment(String metricGroup, String metric,
			Long increase, Collection<Integer> ids);

	public void increment(String metricGroup, String metric,
			Long increase, Integer id);

	public void setColor(String metric, String color);

}