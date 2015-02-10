package com.gerken.gumbo.monitor.contract;

import java.util.ArrayList;

public interface IMetricsHistory {

	/*
	 * Declare a new metric with the given name in the specified metric group if it 
	 * hasn't already been declared.  Then associate the given component task ID 
	 * with the metric.
	 */
	void declare(String metricGroup, String metric, Integer task);

	/* Declare that in the topology there is an edge (a stream) going between
	 * the specified nodes (component names).
	 */
	void topologyConnect(String fromNode, String edgeName, String toNode);

	void topologyConnect(String fromNode, String edgeName);

	/*
	 *  Request that the metrics history server shut down
	 */
	void stop();

	/*
	 * Send the given metric count updates to the metrics history server
	 */
	void update(ArrayList<MetricSnaphot> snapshots);

	/*
	 * Hint that the given metric should be displayed using the given RGB color
	 * (e.g. "255,255,255")
	 */
	void setColor(String metric, String color);

}
