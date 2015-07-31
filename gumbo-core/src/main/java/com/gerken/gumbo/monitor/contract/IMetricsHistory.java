package com.gerken.gumbo.monitor.contract;

import java.util.ArrayList;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public interface IMetricsHistory {

	/**
	 * Specify the start time in miliseconds, the length in milliseconds of a time bucket and the port
	 * @param start
	 * @param bucketSize
	 * @param port
	 */
	public void init(Map config);

	/**
	 * Specify the start time and bucket size, both in milliseconds
	 * @param start
	 * @param bucketSize
	 */
	void init(long start, long bucketSize);

	/**
	 * Declare a new metric with the given name in the specified metric group if it 
	 * hasn't already been declared.  Then associate the given component task ID 
	 * with the metric.
	 */
	public void declare(String metricGroup, String metric, Integer task);

	/**
	 *  Declare that in the topology there is an edge (a stream) going between
	 * the specified nodes (component names).
	 */
	public void topologyConnect(String fromNode, String edgeName, String toNode);

	public void topologyConnect(String fromNode, String edgeName);
	
	/**
	 *  Request that the metrics history server shut down
	 */
	public void stop();

	/**
	 * Send the given metric count updates to the metrics history server
	 */
	public void update(ArrayList<MetricSnapshot> snapshots);

	/**
	 * Hint that the given metric should be displayed using the given RGB color
	 * (e.g. "255,255,255")
	 */
	public void setColor(String metric, String color);

	public JSONObject getJson() throws JSONException;

}
