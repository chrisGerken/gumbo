package com.gerken.gumbo.monitor.contract.cargo;

// Begin imports 

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// End imports 

public class DeclareMetric extends GumboCargo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long _historyId;
	private String _metricGroup;
	private String _metric;
	private Integer _task;

	public DeclareMetric() {

	}

	public DeclareMetric(Long _historyId, String _metricGroup, String _metric, Integer _task) {	
		this._historyId = _historyId;
		this._metricGroup = _metricGroup;
		this._metric = _metric;
		this._task = _task;
	}
	
	public DeclareMetric(byte[] bytes) throws Exception {
		super(bytes);
	}

	public Long getHistoryId() { 
		return _historyId;
	}
	
	public void setHistoryId(Long value) {
		this._historyId = value;
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

	protected void writeObject(ObjectOutputStream out) throws IOException {

		if (_historyId == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(_historyId);
		}

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

	}

	protected void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		if (in.readBoolean()) {
			_historyId = in.readLong();
		} else {
			_historyId = null;
		}

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

	}
	
	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("DeclareMetric [historyId = " + _historyId + "; metricGroup = " + _metricGroup + "; metric = " + _metric + "; task = " + _task + "]");
		return sb.toString();
		
	}

// Begin custom methods 


// End custom methods 
	
}