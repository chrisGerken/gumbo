package com.gerken.gumbo.graph;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class WayPoint {

	private int depth;
	private int leftToRight;
	
	public WayPoint(int depth, int leftToRight) {
		this.depth = depth;
		this.leftToRight = leftToRight;
	}

	public String toString() {
		return "("+depth+","+leftToRight+")";
	}

	public JSONObject getCoordinatesJson() throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("depth",depth);
		jobj.put("order", leftToRight);
		return jobj;
	}
}
