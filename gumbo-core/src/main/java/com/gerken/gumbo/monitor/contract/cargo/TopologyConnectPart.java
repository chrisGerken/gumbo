package com.gerken.gumbo.monitor.contract.cargo;

// Begin imports 

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// End imports 

public class TopologyConnectPart extends GumboCargo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long _historyId;
	private String _fromNode;
	private String _edgeName;

	public TopologyConnectPart() {

	}

	public TopologyConnectPart(Long _historyId, String _fromNode, String _edgeName) {	
		this._historyId = _historyId;
		this._fromNode = _fromNode;
		this._edgeName = _edgeName;
	}
	
	public TopologyConnectPart(byte[] bytes) throws Exception {
		super(bytes);
	}

	public Long getHistoryId() { 
		return _historyId;
	}
	
	public void setHistoryId(Long value) {
		this._historyId = value;
	}

	public String getFromNode() { 
		return _fromNode;
	}
	
	public void setFromNode(String value) {
		this._fromNode = value;
	}

	public String getEdgeName() { 
		return _edgeName;
	}
	
	public void setEdgeName(String value) {
		this._edgeName = value;
	}

	protected void writeObject(ObjectOutputStream out) throws IOException {

		if (_historyId == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(_historyId);
		}

		if (_fromNode == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(_fromNode.length());
			out.write(_fromNode.getBytes());
		}

		if (_edgeName == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(_edgeName.length());
			out.write(_edgeName.getBytes());
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
			_fromNode = new String(b);
		} else {
			_fromNode = null;
		}

		if (in.readBoolean()) {
			byte b[] = new byte[in.readInt()];
			in.read(b);
			_edgeName = new String(b);
		} else {
			_edgeName = null;
		}

	}
	
	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("TopologyConnectPart [historyId = " + _historyId + "; fromNode = " + _fromNode + "; edgeName = " + _edgeName + "]");
		return sb.toString();
		
	}

// Begin custom methods 


// End custom methods 
	
}