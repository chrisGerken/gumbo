package com.gerken.gumbo.monitor.contract.cargo;

// Begin imports 

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// End imports 

public class InitKafka extends GumboCargo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long _historyId;
	private Long _start;
	private Long _bucketSize;
	private Integer _port;
	private String _brokers;

	public InitKafka() {

	}

	public InitKafka(Long _historyId, Long _start, Long _bucketSize, Integer _port, String _brokers) {	
		this._historyId = _historyId;
		this._start = _start;
		this._bucketSize = _bucketSize;
		this._port = _port;
		this._brokers = _brokers;
	}
	
	public InitKafka(byte[] bytes) throws Exception {
		super(bytes);
	}

	public Long getHistoryId() { 
		return _historyId;
	}
	
	public void setHistoryId(Long value) {
		this._historyId = value;
	}

	public Long getStart() { 
		return _start;
	}
	
	public void setStart(Long value) {
		this._start = value;
	}

	public Long getBucketSize() { 
		return _bucketSize;
	}
	
	public void setBucketSize(Long value) {
		this._bucketSize = value;
	}

	public Integer getPort() { 
		return _port;
	}
	
	public void setPort(Integer value) {
		this._port = value;
	}

	public String getBrokers() { 
		return _brokers;
	}
	
	public void setBrokers(String value) {
		this._brokers = value;
	}

	protected void writeObject(ObjectOutputStream out) throws IOException {

		if (_historyId == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(_historyId);
		}

		if (_start == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(_start);
		}

		if (_bucketSize == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(_bucketSize);
		}

		if (_port == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(_port);
		}

		if (_brokers == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(_brokers.length());
			out.write(_brokers.getBytes());
		}

	}

	protected void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		if (in.readBoolean()) {
			_historyId = in.readLong();
		} else {
			_historyId = null;
		}

		if (in.readBoolean()) {
			_start = in.readLong();
		} else {
			_start = null;
		}

		if (in.readBoolean()) {
			_bucketSize = in.readLong();
		} else {
			_bucketSize = null;
		}

		if (in.readBoolean()) {
			_port = in.readInt();
		} else {
			_port = null;
		}

		if (in.readBoolean()) {
			byte b[] = new byte[in.readInt()];
			in.read(b);
			_brokers = new String(b);
		} else {
			_brokers = null;
		}

	}
	
	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("InitKafka [historyId = " + _historyId + "; start = " + _start + "; bucketSize = " + _bucketSize + "; port = " + _port + "; brokers = " + _brokers + "]");
		return sb.toString();
		
	}

// Begin custom methods 


// End custom methods 
	
}