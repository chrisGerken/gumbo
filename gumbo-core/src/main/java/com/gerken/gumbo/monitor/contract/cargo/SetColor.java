package com.gerken.gumbo.monitor.contract.cargo;

// Begin imports 

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// End imports 

public class SetColor extends GumboCargo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long _historyId;
	private String _metric;
	private String _color;

	public SetColor() {

	}

	public SetColor(Long _historyId, String _metric, String _color) {	
		this._historyId = _historyId;
		this._metric = _metric;
		this._color = _color;
	}
	
	public SetColor(byte[] bytes) throws Exception {
		super(bytes);
	}

	public Long getHistoryId() { 
		return _historyId;
	}
	
	public void setHistoryId(Long value) {
		this._historyId = value;
	}

	public String getMetric() { 
		return _metric;
	}
	
	public void setMetric(String value) {
		this._metric = value;
	}

	public String getColor() { 
		return _color;
	}
	
	public void setColor(String value) {
		this._color = value;
	}

	protected void writeObject(ObjectOutputStream out) throws IOException {

		if (_historyId == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(_historyId);
		}

		if (_metric == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(_metric.length());
			out.write(_metric.getBytes());
		}

		if (_color == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(_color.length());
			out.write(_color.getBytes());
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
			_metric = new String(b);
		} else {
			_metric = null;
		}

		if (in.readBoolean()) {
			byte b[] = new byte[in.readInt()];
			in.read(b);
			_color = new String(b);
		} else {
			_color = null;
		}

	}
	
	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("SetColor [historyId = " + _historyId + "; metric = " + _metric + "; color = " + _color + "]");
		return sb.toString();
		
	}

// Begin custom methods 


// End custom methods 
	
}