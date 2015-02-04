package com.gerken.gumbo.graph;

import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class GraphEdge {

	private GraphNode from;
	private GraphNode to;
	private String stream;
	
	private boolean loopBack = false;
	private boolean used = false;
	private ArrayList<WayPoint> path = new ArrayList<WayPoint>();
	
	public GraphEdge(GraphNode from, GraphNode to, String stream) {
		this.from = from;
		this.to = to;
		this.stream = stream;
	}

	public GraphNode getFrom() {
		return from;
	}

	public GraphNode getTo() {
		return to;
	}

	public String getStream() {
		return stream;
	}

	public boolean isLoopBack() {
		return loopBack;
	}

	public void setLoopBack(boolean loopBack) {
		this.loopBack = loopBack;
	}

	public boolean hasBeenUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public void addWayPoint(int x, int y) {
		path.add(new WayPoint(x, y));
	}
	
	public String toString() {
		String buf = "Edge: "+from.getComponent()+" --> "+to.getComponent()+";  "+from.getWayPoint().toString();
		for (WayPoint wp : path) {
			buf = buf + "  " + wp.toString();
		}
		buf = buf + "  " + to.getWayPoint().toString();
		return buf;
	}
	
	public JSONObject getJson() throws JSONException {

		JSONObject json = new JSONObject();
		json.put("from", from.getComponent());
		json.put("to", to.getComponent());
		json.put("stream", stream);
		json.put("loopBack",loopBack);
		JSONArray jarr = new JSONArray();
		jarr.put(from.getCoordinatesJson());
		for (WayPoint wp : path) {
			jarr.put(wp.getCoordinatesJson());
		}
		jarr.put(to.getCoordinatesJson());
		json.put("path", jarr);

		return json;
	}

}
