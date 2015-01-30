package com.gerken.gumbo.graph;

import java.util.ArrayList;

public class GraphNode {

	private String component;
	private ArrayList<GraphNode> children = new ArrayList<GraphNode>();
	private ArrayList<GraphNode> parents = new ArrayList<GraphNode>();
	
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

}
