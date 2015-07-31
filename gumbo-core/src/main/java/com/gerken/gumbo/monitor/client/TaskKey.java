package com.gerken.gumbo.monitor.client;

/**
 * Represents a key with which to determine which metric count gets incremented or decremented.
 * 
 * @author chrisgerken
 *
 */
public class TaskKey {

	private String metricGroup;
	private String metric;
	private Integer task;
	
	private int hash;
	
	/**
	 * Constructs a key for a given metric group, metric and task
	 * @param metricGroup the name of the metric group
	 * @param metric the name of the metric
	 * @param task the task number
	 */
	public TaskKey(String metricGroup, String metric, Integer task) {
		this.metricGroup = metricGroup;
		this.metric = metric;
		this.task = task;
		hash = (metricGroup + metric + task).hashCode(); 
	}

	/**
	 * Retrun the key's hashcode
	 */
	@Override
	public int hashCode() {
		return hash;
	}

	/**
	 * Answer whether the specified key is the same as the receiver
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TaskKey) {
			TaskKey that = (TaskKey) obj;
			if (!metricGroup.equals(that.metricGroup)) { 
				return false; 
			}
			if (!metric.equals(that.metric)) { 
				return false; 
			}
			return task == that.task;
		}
		return false;
	}

	/**
	 * Answer the name of the metric group
	 * @return the name of the metric group
	 */
	public String getMetricGroup() {
		return metricGroup;
	}

	/**
	 * Answer the name of the metric
	 * @return the name of the metric
	 */
	public String getMetric() {
		return metric;
	}

	/**
	 * Answer the task number
	 * @return the task number
	 */
	public Integer getTask() {
		return task;
	}

}
