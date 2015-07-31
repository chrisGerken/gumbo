package com.gerken.gumbo.monitor.contract.cargo;

// Begin imports 

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
 
import com.gerken.gumbo.monitor.contract.MetricSnapshot;

// End imports 

public class Update extends GumboCargo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long _historyId;
	private MetricSnapshot[] _snapshot;

	public Update() {

	}

	public Update(Long _historyId, MetricSnapshot[] _snapshot) {	
		this._historyId = _historyId;
		this._snapshot = _snapshot;
	}
	
	public Update(byte[] bytes) throws Exception {
		super(bytes);
	}

	public Long getHistoryId() { 
		return _historyId;
	}
	
	public void setHistoryId(Long value) {
		this._historyId = value;
	}

	public MetricSnapshot[] getSnapshot() { 
		return _snapshot;
	}
	
	public void setSnapshot(MetricSnapshot[] value) {
		this._snapshot = value;
	}

	protected void writeObject(ObjectOutputStream out) throws IOException {

		if (_historyId == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(_historyId);
		}

		if (_snapshot == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(_snapshot.length);
			for (int i = 0; i < _snapshot.length; i++) {
				_snapshot[i].writeObject(out);
			}
		}

	}

	protected void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		if (in.readBoolean()) {
			_historyId = in.readLong();
		} else {
			_historyId = null;
		}

		if (in.readBoolean()) {
			MetricSnapshot s[] = new MetricSnapshot[in.readInt()];
			for (int i = 0; i < s.length; i++) {
				s[i] = new MetricSnapshot();
				s[i].readObject(in);
			}
			_snapshot = s;
		} else {
			_snapshot = null;
		}

	}
	
	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("Update [historyId = " + _historyId + "; snapshot = " + _snapshot + "]");
		return sb.toString();
		
	}

// Begin custom methods 

	public ArrayList<MetricSnapshot> getSnapshots() {
		ArrayList<MetricSnapshot> snapshots = new ArrayList<MetricSnapshot>();
		for (MetricSnapshot ms : getSnapshot()) {
			snapshots.add(ms);
		}
		return snapshots;
	}


// End custom methods 
	
}