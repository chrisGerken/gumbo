package com.gerken.gumbo.monitor.contract;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MetricSnapshot implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String metricGroup;
	private String metric;
	private Integer task;
	private Long bucket;
	private Long value;

	public MetricSnapshot(String metricGroup, String metric, Integer task, long bucket, long value) {
		this.metricGroup = metricGroup;
		this.metric = metric;
		this.task = task;
		this.bucket = bucket;
		this.value = value;
	}

	public MetricSnapshot() {

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

	public Long getBucket() {
		return bucket;
	}

	public Long getValue() {
		return value;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {

		if (metricGroup == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(metricGroup.length());
			out.write(metricGroup.getBytes());
		}

		if (metric == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(metric.length());
			out.write(metric.getBytes());
		}

		if (task == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(task);
		}

		if (bucket == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(bucket);
		}

		if (value == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(value);
		}

	}

	public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		if (in.readBoolean()) {
			byte b[] = new byte[in.readInt()];
			in.read(b);
			metricGroup = new String(b);
		} else {
			metricGroup = null;
		}

		if (in.readBoolean()) {
			byte b[] = new byte[in.readInt()];
			in.read(b);
			metric = new String(b);
		} else {
			metric = null;
		}

		if (in.readBoolean()) {
			task = in.readInt();
		} else {
			task = null;
		}

		if (in.readBoolean()) {
			bucket = in.readLong();
		} else {
			bucket = null;
		}

		if (in.readBoolean()) {
			value = in.readLong();
		} else {
			value = null;
		}

	}
	
}
