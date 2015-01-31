package com.gerken.gumbo.graph;

import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class GraphNode {

	private String component;
	private int depth = 0;
	private int order = 0;
	
	private ArrayList<GraphNode> children = new ArrayList<GraphNode>();
	private ArrayList<GraphNode> parents = new ArrayList<GraphNode>();
	
	private ArrayList<GraphEdge> inboundEdges = new ArrayList<GraphEdge>();
	private ArrayList<GraphEdge> outboundEdges = new ArrayList<GraphEdge>();
	
	public GraphNode(String component) {
		this.component = component;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((component == null) ? 0 : component.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphNode other = (GraphNode) obj;
		if (component == null) {
			if (other.component != null)
				return false;
		} else if (!component.equals(other.component))
			return false;
		return true;
	}

	public void addChild(GraphNode node) {
		children.add(node);
	}

	public void addParent(GraphNode node) {
		parents.add(node);
	}

	public void addParents(Collection<GraphNode> nodes) {
		parents.addAll(nodes);
	}

	public ArrayList<GraphNode> getParents() {
		return parents;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	public boolean hasOrder() {
		return order > 0;
	}

	public String getComponent() {
		return component;
	}
	
	public boolean isRoot() {
		return parents.isEmpty();
	}
	
	public boolean hasDepth() {
		return depth > 0;
	}
	
	public boolean hasParents() {
		return !parents.isEmpty();
	}

	public boolean isParentOf(GraphNode from) {
		return from.parents.contains(this);
	}
	
	public String toString() {
		return "Node: "+component+" at ("+depth+","+order+")";
	}

	public void addInboundEdge(GraphEdge edge) {
		inboundEdges.add(edge);
	}

	public ArrayList<GraphEdge> getInboundEdges() {
		return inboundEdges;
	}

	public void addOutboundEdge(GraphEdge edge) {
		outboundEdges.add(edge);
	}

	public ArrayList<GraphEdge> getOutboundEdges() {
		return outboundEdges;
	}
	
	public WayPoint getWayPoint() {
		return new WayPoint(depth, order);
	}
	
	public JSONObject getJson() throws JSONException {

		JSONObject json = new JSONObject();
		json.put("component", component);
		json.put("location",getCoordinatesJson());

		return json;
	}

	public JSONObject getCoordinatesJson() throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("depth",depth);
		jobj.put("order", order);
		return jobj;
	}

}
