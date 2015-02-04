package com.gerken.gumbo.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Graph {

	private HashMap<String, HashSet<String>> components = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> streams = new HashMap<String, HashSet<String>>();
	
	private HashMap<String, GraphNode> nodes = new HashMap<String, GraphNode>();
	private ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
	
	private JSONObject json = null;
	
	public Graph(HashMap<String, HashSet<String>> components, HashMap<String, HashSet<String>> streams) {
		this.components = components;
		this.streams = streams;
		layout();
	}

	private void layout() {
		
		// build the nodes and edges
		for (String fromComponent : components.keySet()) {
			GraphNode from = nodeFor(fromComponent);
			HashSet<String> outStreams = components.get(fromComponent);
			if (outStreams==null) {
				outStreams = new HashSet<String>();
			}
			// for each stream coming out of fromComponent...
			for (String stream : outStreams) {
				// for each component reading from stream...
				HashSet<String> dests = streams.get(stream);
				if (dests==null) {
					dests = new HashSet<String>();
				}
				for (String toComponent : dests) {
					GraphNode to = nodeFor(toComponent);
					GraphEdge edge = new GraphEdge(from, to, stream);
					edges.add(edge);
					from.addChild(to);
					from.addOutboundEdge(edge);
					to.addInboundEdge(edge);
				}
			}
		}
		
		// set the root node depths
		for (GraphNode node : nodes.values()) {
			if (!node.hasParents()) {
				node.setDepth(1);
			} else {
				node.setDepth(0);
			}
		}
		
		// set the remaining node depths
		boolean changed = true;
		while (changed) {
			changed = false;
			for (GraphEdge edge : edges) {
				GraphNode from = edge.getFrom();
				GraphNode to = edge.getTo();

				if (from.hasDepth()) {
					if (to.isParentOf(from)) {
						changed = changed | !edge.hasBeenUsed();
						edge.setLoopBack(true);
						edge.setUsed(true);
					} else {
						if (!edge.hasBeenUsed()) {
							changed = true;
							to.addParents(from.getParents());
							to.addParent(from);
						}
						if (from.getDepth() >= to.getDepth()) {
							to.setDepth(from.getDepth()+1);
							changed = true;
						}
						edge.setUsed(true);
					}
				} else {
					// can't do anything with this edge until the from node has a depth
				}
			}
		}
		
		// calculate the paths for the edges
		
		int margin = 25;
		int nodeWidth = 6;

		int max = maxDepth();
		int leftToRight = margin;
		int width[] = new int[max+1];
		
		// build a list, left to right, of the edges coming out of the root nodes
		ArrayList<GraphEdge> currentPaths = new ArrayList<GraphEdge>();
		for (GraphNode node : nodes.values()) {
			if (node.getDepth()==1) {
				currentPaths.addAll(node.getOutboundEdges());
				node.setOrder(leftToRight);
				leftToRight = leftToRight + nodeWidth + margin;
			}
		}
		width[1] = leftToRight-1;
		
		for (int level = 2; level <= max; level++) {
			ArrayList<GraphEdge> nextPaths = new ArrayList<GraphEdge>();
			leftToRight = margin;
			for (GraphEdge edge : currentPaths) {
				GraphNode to = edge.getTo();
				if (to.getDepth()==level) {
					// if a node already has an order then it's already contributed to the next paths
					if (!to.hasOrder()) {
						to.setOrder(leftToRight);
						leftToRight = leftToRight + nodeWidth + margin;
						for (GraphEdge outbound : to.getOutboundEdges()) {
							if (!outbound.isLoopBack()) {
								nextPaths.add(outbound);
							}
						}
						for (GraphEdge outbound : to.getOutboundEdges()) {
							if (outbound.isLoopBack()) {
								nextPaths.add(outbound);
								outbound.addWayPoint(level, leftToRight);
								leftToRight = leftToRight + margin + nodeWidth;;
							}
						}
					}
				} else {
					// just passing through
					nextPaths.add(edge);
					edge.addWayPoint(level,leftToRight);
					leftToRight = leftToRight + margin + nodeWidth;
				}
			}
			width[level] = leftToRight-1;
			currentPaths = nextPaths;
		}
		
	}

	private int maxDepth() {
		int max = -1;
		for (GraphNode node : nodes.values()) {
			if (node.getDepth() > max) {
				max = node.getDepth();
			}
		}
		return max;
	}

	private GraphNode nodeFor(String component) {
		GraphNode node = nodes.get(component);
		if (node == null) {
			node = new GraphNode(component);
			nodes.put(component,node);
		}
		return node;
	}
	
	public void print() {
		for (GraphNode node : nodes.values()) {
			System.out.println(node.toString());
		}
		for (GraphEdge edge : edges) {
			System.out.println(edge.toString());
		}
	}

	public JSONObject asJson() {
		
		if (json==null) {

			json = new JSONObject();
			
			try {
				JSONArray jarr = new JSONArray();
				for (GraphEdge edge : edges) {
					jarr.put(edge.getJson());
				}
				json.put("edges", jarr);
				
				jarr = new JSONArray();
				for (GraphNode node : nodes.values()) {
					jarr.put(node.getJson());
				}
				json.put("nodes", jarr);
				
				json.put("created", System.currentTimeMillis());
				
			} catch (JSONException e) {
			}
		}
		
		return json;
	}

}
