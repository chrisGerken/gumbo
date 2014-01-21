package com.gerken.gumbo.monitor;

public class TaskKey {

	private String metricGroup;
	private String metric;
	private Integer task;
	
	private int hash;
	
	public TaskKey(String metricGroup, String metric, Integer task) {
		this.metricGroup = metricGroup;
		this.metric = metric;
		this.task = task;
		hash = (metricGroup + metric + task).hashCode(); 
	}

	@Override
	public int hashCode() {
		return hash;
	}

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

	public String getMetricGroup() {
		return metricGroup;
	}

	public String getMetric() {
		return metric;
	}

	public Integer getTask() {
		return task;
	}

}
