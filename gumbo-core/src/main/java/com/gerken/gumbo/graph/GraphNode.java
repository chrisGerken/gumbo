package com.gerken.gumbo.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Represents a node in the graph
 * 
 * A GraphNode has knowledge of the position of the node with respect to other nodes before
 * and after it in the graph.
 * 
 * @author chrisgerken
 *
 */
public class GraphNode {

	private String component;
	private int depth = 0;
	private int order = 0;
	
	private HashSet<GraphNode> children = new HashSet<GraphNode>();
	private HashSet<GraphNode> parents = new HashSet<GraphNode>();
	
	private ArrayList<GraphEdge> inboundEdges = new ArrayList<GraphEdge>();
	private ArrayList<GraphEdge> outboundEdges = new ArrayList<GraphEdge>();
	
	/**
	 * Constructs a GraphNode to represent a given component in a Storm topology.
	 * @param component the name of a component in a Storm topology
	 */
	public GraphNode(String component) {
		this.component = component;
	}

	/**
	 * Calculate and return the hashcode for the receiver
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((component == null) ? 0 : component.hashCode());
		return result;
	}

	/**
	 * Returns whether this node represents the same component as another GraphNode
	 */
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

	/**
	 * Specify that an edge goes from the receiver to the the given node
	 * @param node a child node
	 */
	public void addChild(GraphNode node) {
		children.add(node);
	}

	/**
	 * Specify that an edge goes from the given node to the receiver
	 * @param node a parent node
	 */
	public void addParent(GraphNode node) {
		parents.add(node);
	}

	/**
	 * Specify that the nodes in the given collection are all parents of the receiver
	 * @param nodes
	 */
	public void addParents(Collection<GraphNode> nodes) {
		parents.addAll(nodes);
	}

	/**
	 * Return the parents of the receiver
	 * @return A collection of unique parents
	 */
	public HashSet<GraphNode> getParents() {
		return parents;
	}

	/**
	 * Get the depth of the receiver
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Set the depth of the receiver
	 * @param depth the logical distance from the root of the graph
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * Get the receiver's placement within nodes of the same depth
	 * @return the receiver's placement within nodes of the same depth
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Set the receiver's placement within nodes of the same depth
	 * @param order the receiver's placement within nodes of the same depth
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	
	/**
	 * Whether the receiver has been placed
	 * @return whether the receiver has been placed
	 */
	public boolean hasOrder() {
		return order > 0;
	}

	/**
	 * Return the label (Storm component name) of the node
	 * @return the label (Storm component name) of the node
	 */
	public String getComponent() {
		return component;
	}
	
	/**
	 * After placement within the graph, return whether the receiver has any parents
	 * @return whether the receiver has any parents
	 */
	public boolean isRoot() {
		return parents.isEmpty();
	}
	
	/**
	 * Return whether the receiver has been placed
	 * @return whether the receiver has been placed
	 */
	public boolean hasDepth() {
		return depth > 0;
	}
	
	/**
	 * Return whether the receiver has any parents
	 * @return whether the receiver has any parents
	 */
	public boolean hasParents() {
		if (parents.isEmpty()) {
			return false;
		}
		if (parents.size()>1) {
			return true;
		}
		return !parents.contains(this);
	}

	/**
	 * Return whether the receiver is a child, direct or not, of the given node
	 * @param from another node
	 * @return whether the receiver is a child, direct or not, of the given node
	 */
	public boolean isParentOf(GraphNode from) {
		return from.parents.contains(this);
	}
	
	/**
	 * Return a human-readable representation of the receiver
	 */
	public String toString() {
		return "Node: "+component+" at ("+depth+","+order+")";
	}

	/**
	 * Add an inbound edge to the receiver
	 * @param edge an edge in the graph
	 */
	public void addInboundEdge(GraphEdge edge) {
		inboundEdges.add(edge);
	}

	/**
	 * Return a list of the inbound edges to the receiver
	 * @return a list of edges
	 */
	public ArrayList<GraphEdge> getInboundEdges() {
		return inboundEdges;
	}

	/**
	 * Add an outbound edge from the receiver
	 * @param edge an edge in the grapg
	 */
	public void addOutboundEdge(GraphEdge edge) {
		outboundEdges.add(edge);
	}

	/**
	 * Return a list of outbound edges from the receiver
	 * @return a list of edges
	 */
	public ArrayList<GraphEdge> getOutboundEdges() {
		return outboundEdges;
	}
	
	/**
	 * Return a waypoint representing the location of the receiver
	 * @return a waypoint representing the location of the receiver
	 */
	public WayPoint getWayPoint() {
		return new WayPoint(depth, order);
	}
	
	/**
	 * Return a Json object representing the state of the receiver
	 * @return a Json object
	 * @throws JSONException
	 */
	public JSONObject getJson() throws JSONException {

		JSONObject json = new JSONObject();
		json.put("component", component);
		json.put("location",getCoordinatesJson());

		return json;
	}

	/**
	 * Return a Json object representing the state of the receiver's waypoint
	 * @return a Json object representing the state of the receiver's waypoint
	 * @throws JSONException
	 */
	public JSONObject getCoordinatesJson() throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("depth",depth);
		jobj.put("order", order);
		return jobj;
	}

}
