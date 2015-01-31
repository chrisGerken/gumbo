package com.gerken.gumbo.graph;

import java.util.HashMap;
import java.util.HashSet;

public class GraphBuilder {

	HashMap<String, HashSet<String>> components = new HashMap<String, HashSet<String>>();
	HashMap<String, HashSet<String>> streams = new HashMap<String, HashSet<String>>();
	
	public GraphBuilder() {

	}

	public void addStreamFromComponent(String component, String stream) {
		HashSet<String> set = components.get(component);
		if (set==null) {
			set = new HashSet<String>();
			components.put(component, set);
		}
		set.add(stream);
	}

	public void addComponentFromStream(String stream, String component) {
		HashSet<String> set = streams.get(stream);
		if (set==null) {
			set = new HashSet<String>();
			streams.put(stream, set);
		}
		set.add(component);
	}
	
	public void connect(String fromComponent, String toComponent, String stream) {
		addStreamFromComponent(fromComponent, stream);
		addComponentFromStream(stream, toComponent);
	}
	
	public Graph build() {
		return new Graph(components,streams);
	}
	
	public static Graph graph01() {
		GraphBuilder gb = new GraphBuilder();
		gb.connect("A", "B", "a1");
		gb.connect("A", "E", "a2");
		gb.connect("B", "C", "b");
		gb.connect("C", "D", "c");
		gb.connect("D", "F", "d");
		gb.connect("E", "D", "e");
		gb.connect("E", "D", "e");
		return gb.build();
	}
	
	public static void main(String args[]) {
		Graph g = graph01();
		g.print();
	}
}
