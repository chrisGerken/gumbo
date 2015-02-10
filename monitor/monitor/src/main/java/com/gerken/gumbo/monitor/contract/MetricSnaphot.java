package com.gerken.gumbo.monitor.contract;

public class MetricSnaphot {

	private String metricGroup;
	private String metric;
	private Integer task;
	private long bucket;
	private long value;

	public MetricSnaphot(String metricGroup, String metric, Integer task, long bucket, long value) {
		this.metricGroup = metricGroup;
		this.metric = metric;
		this.task = task;
		this.bucket = bucket;
		this.value = value;
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

	public long getBucket() {
		return bucket;
	}

	public long getValue() {
		return value;
	}
}
