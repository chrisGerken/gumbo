package com.gerken.gumbo.graph;

import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Represents an edge in the graph
 * 
 * @author chrisgerken
 */
public class GraphEdge {

	private GraphNode from;
	private GraphNode to;
	private String stream;
	
	private boolean loopBack = false;
	private boolean used = false;
	private ArrayList<WayPoint> path = new ArrayList<WayPoint>();
	private ArrayList<PathSegment>  segments = null;

	/**
	 * Construct an edge between two nodes with a given label (stream name for Storm topologies)
	 * @param from the "from" end node
	 * @param to the "to" end node
	 * @param stream the label for this edge.  For Storm topologies the label is the name of a stream
	 * between the componentes represented by the from and to nodes.
	 */
	public GraphEdge(GraphNode from, GraphNode to, String stream) {
		this.from = from;
		this.to = to;
		this.stream = stream;
	}

	/**
	 * Return the from end node
	 * @return the from end node
	 */
	public GraphNode getFrom() {
		return from;
	}

	/**
	 * Return the to end node
	 * @return the to end node
	 */
	public GraphNode getTo() {
		return to;
	}

	/**
	 * Return the label (Storm stream name) of the edge
	 * @return
	 */
	public String getStream() {
		return stream;
	}

	/**
	 * Return whether this edge loops back.  Loop back edges create cycles
	 * in the directed graph
	 * @return whether this edge loops back.
	 */
	public boolean isLoopBack() {
		return loopBack;
	}

	/**
	 * Specify whether this edge loops back
	 * @param loopBack whether this edge loops back
	 */
	public void setLoopBack(boolean loopBack) {
		this.loopBack = loopBack;
	}

	/**
	 * Return whether this edge has been laid out already
	 * @return whether this edge has been laid out already
	 */
	public boolean hasBeenUsed() {
		return used;
	}

	/**
	 * Specify whether this edge has been laid out already
	 * @param used whether this edge has been laid out already
	 */
	public void setUsed(boolean used) {
		this.used = used;
	}

	/**
	 * Constructs if necessary and returns an array of path segments that
	 * describe this edge's actual layout.
	 * @return an array of path segments that
	 * describe this edge's actual layout.
	 */
	public ArrayList<PathSegment> getSegments() {
		if (segments == null) {
			segments = new ArrayList<PathSegment>();
			WayPoint prev;
			if (loopBack) {
				prev = from.getWayPoint();
				boolean first = true;
				for (WayPoint wp : path) {
					if (first) {
						segments.add(new PathSegment(prev,wp,PathSegment.TYPE_LOOPBACK_FROM));
					} else {
						segments.add(new PathSegment(prev,wp,PathSegment.TYPE_NORMAL));
					}
					prev = wp;
				}
				segments.add(new PathSegment(prev,to.getWayPoint(),PathSegment.TYPE_LOOPBACK_TO));
			} else {
				prev = from.getWayPoint();
				for (WayPoint wp : path) {
					segments.add(new PathSegment(prev,wp,PathSegment.TYPE_NORMAL));
					prev = wp;
				}
				segments.add(new PathSegment(prev,to.getWayPoint(),PathSegment.TYPE_NORMAL));
			}
			
		}
		return segments;
	}

	/**
	 *  Add another way point to the path.
	 */
	public void addWayPoint(int x, int y) {
		path.add(new WayPoint(x, y));
	}
	
	/**
	 * Return a human-readable representation of the receiver
	 */
	public String toString() {
		String buf = "Edge: "+from.getComponent()+" --> "+to.getComponent()+";  "+from.getWayPoint().toString();
		for (WayPoint wp : path) {
			buf = buf + "  " + wp.toString();
		}
		buf = buf + "  " + to.getWayPoint().toString();
		return buf;
	}
	
	/**
	 * Return a Json object representing the receiver's state
	 * @return a Json object representing the receiver's state
	 * @throws JSONException
	 */
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
//		json.put("path", jarr);

		jarr = new JSONArray();
		for (PathSegment segment : segments) {
			jarr.put(segment.asJson());
		}
		json.put("segments", jarr);

		return json;
	}

}
