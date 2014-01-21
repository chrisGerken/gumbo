package com.gerken.gumbo.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Graph {

	private String name;
	private HashMap<String, GraphNode> nodes = new HashMap<String, GraphNode>();
	private HashSet<GraphNode> froms = new HashSet<GraphNode>();
	private HashSet<GraphNode> tos = new HashSet<GraphNode>();
	
	public Graph(String name) {
		this.name = name;
	}

	public synchronized void connect(String fromNode, String toNode, String edgeName) {
		GraphNode from = getNode(fromNode);
		GraphNode to = getNode(toNode);
		from.addOutput(edgeName,to);
		froms.add(from);
		tos.add(to);
		layout();
	}

	private GraphNode getNode(String nodeName) {
		GraphNode node = nodes.get(nodeName);
		if (node == null) {
			node = new GraphNode(nodeName);
			nodes.put(nodeName, node);
		}
		return node;
	}

	private void layout() {

		for (GraphNode node : nodes.values()) {
			node.reset();
		}
		
		ArrayList<GraphNode> pending = new ArrayList<GraphNode>();
		HashSet<GraphNode> roots = new HashSet<GraphNode>();

		for (GraphNode from : froms) {
			if (!tos.contains(from)) {
				roots.add(from);
				pending.add(from);
				from.setDepth(0);
			}
		}
		
		while (!pending.isEmpty()) {
			GraphNode node = pending.remove(0);
			for (GraphNode to : node.allOutputs()) {
				if (!to.isPlaced()) {
					to.setDepth(node.getDepth()+1);
					to.setPlaced(true);
				} else if (to.isAncestorOf(node)) {
					// loop back
				} else {
					to.setDepth(Math.max(node.getDepth()+1,to.getDepth()));
				}
				to.addAncestors(node.getAncestors());
				to.addAncestor(node);
				
				
			}
			
			
		}
	}

}
