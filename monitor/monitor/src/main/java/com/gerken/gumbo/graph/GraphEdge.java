package com.gerken.gumbo.graph;

public class GraphEdge {

	private GraphNode from;
	private GraphNode to;
	private String stream;
	
	public GraphEdge(GraphNode from, GraphNode to, String stream) {
		this.from = from;
		this.to = to;
		this.stream = stream;
	}

}
