package com.gerken.gumbo.retired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class GraphNode {

	private String name;
	private boolean placed;
	private int depth;
	private HashMap<String, HashSet<GraphNode>> outputs = new HashMap<String, HashSet<GraphNode>>();
	private HashSet<GraphNode> ancestors = new HashSet<GraphNode>();
	
	public GraphNode(String name) {
		this.name = name;
	}

	public void addOutput(String edgeName, GraphNode node) {
		HashSet<GraphNode> nodes = outputs.get(edgeName);
		if (nodes == null) {
			nodes = new HashSet<GraphNode>();
			outputs.put(edgeName, nodes);
		}
		nodes.add(node);
	}
	
	public HashMap<String, HashSet<GraphNode>> getOutputs() {
		return outputs;
	}
	
	public Set<GraphNode> allOutputs() {
		HashSet<GraphNode> result = new HashSet<GraphNode>();
		for (HashSet<GraphNode> set : outputs.values()) {
			result.addAll(set);
		}
		return result;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public int getDepth() {
		return depth;
	}

	public void setPlaced(boolean place) {
		this.placed = placed;
	}
	
	public boolean isPlaced() {
		return placed;
	}
	
	public void addAncestor(GraphNode node) {
		ancestors.add(node);
	}
	
	public void addAncestors(Set<GraphNode> nodes) {
		ancestors.addAll(nodes);
	}
	
	public boolean isAncestorOf(GraphNode node) {
		return node.getAncestors().contains(this);
	}
	
	public void reset() {
		placed = false;
		ancestors = new HashSet<GraphNode>();
		depth = -1;
	}

	public Set<GraphNode> getAncestors() {
		return ancestors;
	}

//	public JSONObject asJson() {
//		JSONObject jobj = new JSONObject();
//		jobj.put("node", name);
//		JSONArray jarr = new JSONArray();
//		for (String stream : outputs.keySet()) {
//			JSONObject node = new JSONObject();
//			
//			jarr.put(node);
//		}
//		jobj.put("nodes", jarr);
//		return jobj;
//		private HashMap<String, HashSet<GraphNode>> outputs = new HashMap<String, HashSet<GraphNode>>();
//	}
	
}
