package com.gerken.gumbo.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Graph {

	private HashMap<String, HashSet<String>> components;
	private HashMap<String, HashSet<String>> streams;
	
	private HashMap<String, GraphNode> nodes = new HashMap<String, GraphNode>();
	private ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
	
	public Graph(HashMap<String, HashSet<String>> components, HashMap<String, HashSet<String>> streams) {
		this.components = components;
		this.streams = streams;
		layout();
	}

	private void layout() {
		
		// build the nodes and edges
		for (String fromComponent : components.keySet()) {
			GraphNode from = nodeFor(fromComponent);
			// for each stream coming out of fromComponent...
			for (String stream : components.get(fromComponent)) {
				// for each component reading from stream...
				for (String toComponent : streams.get(stream)) {
					GraphNode to = nodeFor(toComponent);
					GraphEdge edge = new GraphEdge(from, to, stream);
					edges.add(edge);
					from.addChild(to);
					to.addParent(from);
				}
			}
		}
	}

	private GraphNode nodeFor(String component) {
		GraphNode node = nodes.get(component);
		if (node == null) {
			node = new GraphNode(component);
			nodes.put(component,node);
		}
		return node;
	}
}
