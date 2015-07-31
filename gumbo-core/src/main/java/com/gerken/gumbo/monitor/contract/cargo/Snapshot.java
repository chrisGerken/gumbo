package com.gerken.gumbo.monitor.contract.cargo;

// Begin imports 

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.gerken.gumbo.monitor.contract.MetricSnapshot;

// End imports 

public class Snapshot extends GumboCargo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String _metricGroup;
	private String _metric;
	private Integer _task;
	private Long _bucket;
	private Long _value;

	public Snapshot() {

	}

	public Snapshot(String _metricGroup, String _metric, Integer _task, Long _bucket, Long _value) {	
		this._metricGroup = _metricGroup;
		this._metric = _metric;
		this._task = _task;
		this._bucket = _bucket;
		this._value = _value;
	}
	
	public Snapshot(byte[] bytes) throws Exception {
		super(bytes);
	}

	public String getMetricGroup() { 
		return _metricGroup;
	}
	
	public void setMetricGroup(String value) {
		this._metricGroup = value;
	}

	public String getMetric() { 
		return _metric;
	}
	
	public void setMetric(String value) {
		this._metric = value;
	}

	public Integer getTask() { 
		return _task;
	}
	
	public void setTask(Integer value) {
		this._task = value;
	}

	public Long getBucket() { 
		return _bucket;
	}
	
	public void setBucket(Long value) {
		this._bucket = value;
	}

	public Long getValue() { 
		return _value;
	}
	
	public void setValue(Long value) {
		this._value = value;
	}

	protected void writeObject(ObjectOutputStream out) throws IOException {

		if (_metricGroup == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(_metricGroup.length());
			out.write(_metricGroup.getBytes());
		}

		if (_metric == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(_metric.length());
			out.write(_metric.getBytes());
		}

		if (_task == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(_task);
		}

		if (_bucket == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(_bucket);
		}

		if (_value == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(_value);
		}

	}

	protected void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		if (in.readBoolean()) {
			byte b[] = new byte[in.readInt()];
			in.read(b);
			_metricGroup = new String(b);
		} else {
			_metricGroup = null;
		}

		if (in.readBoolean()) {
			byte b[] = new byte[in.readInt()];
			in.read(b);
			_metric = new String(b);
		} else {
			_metric = null;
		}

		if (in.readBoolean()) {
			_task = in.readInt();
		} else {
			_task = null;
		}

		if (in.readBoolean()) {
			_bucket = in.readLong();
		} else {
			_bucket = null;
		}

		if (in.readBoolean()) {
			_value = in.readLong();
		} else {
			_value = null;
		}

	}
	
	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("Snapshot [metricGroup = " + _metricGroup + "; metric = " + _metric + "; task = " + _task + "; bucket = " + _bucket + "; value = " + _value + "]");
		return sb.toString();
		
	}

// Begin custom methods 


// End custom methods 
	
}