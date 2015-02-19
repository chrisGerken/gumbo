package com.gerken.gumbo.graph;

import java.util.HashMap;
import java.util.HashSet;

import org.codehaus.jettison.json.JSONException;

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
	
	public static Graph graph02() {
		GraphBuilder gb = new GraphBuilder();
		gb.connect("Reader", "Parser", "Raw");
		gb.connect("Parser", "Validator", "Parsed");
		gb.connect("Validator", "Enricher", "Validated");
		gb.connect("Enricher", "Multiplexer", "Enriched");
		gb.connect("Multiplexer", "Approver", "Important");
		gb.connect("Multiplexer", "RedTaper", "UnImportant");
		gb.connect("Multiplexer", "Overseer", "Urgent");
		gb.connect("Approver", "Executor", "Approved");
		gb.connect("Approver", "Logger", "ToBeLogged");
		gb.connect("Overseer", "Approver", "Overseen");
		gb.connect("Executor", "Logger", "ToBeLogged1");
		gb.connect("Logger", "Publisher", "Logged");
		return gb.build();
	}
	
	public static Graph graph03() {
		GraphBuilder gb = new GraphBuilder();
//		gb.connect("A", "B", "e1");
//		gb.connect("B", "C", "e2");
		gb.connect("B", "D", "e3");
		gb.connect("B", "D", "e4");
		gb.connect("B", "D", "e5");
		gb.connect("B", "D", "e6");
//		gb.connect("C", "D", "e7");
		return gb.build();
	}
	
	public static void main(String args[]) {
		try {
			Graph g = graph03();
			g.asJson().toString(4);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
