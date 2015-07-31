package com.gerken.gumbo.monitor.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONObject;

import com.gerken.gumbo.graph.Graph;

/**
 * A convenience class to hold information about the directed graph managed or monitored by an application
 * 
 * @author chrisgerken
 *
 */
public class Topology {

	private HashMap<String, HashSet<String>> components = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> streams = new HashMap<String, HashSet<String>>();
	private HashMap<String, Integer> trends = new HashMap<String, Integer>();
	private HashMap<String, Integer> upTrendCounts = new HashMap<String, Integer>();
	private HashMap<String, Long> latestBacklogs = new HashMap<String, Long>();
	
	private Graph graph = null;
	
	public Topology() {

	}

	protected Set<String> getStreams() {
		return streams.keySet();
	}

	protected Set<String> getComponents() {
		return components.keySet();
	}
	
	protected void setTrend(String stream, Integer trend) {
		if (getTrend(stream) > 0) {
			incrementTrendCount(stream);
		} else {
			upTrendCounts.put(stream,0);
		}
		trends.put(stream,trend);
	}
	
	private void incrementTrendCount(String stream) {
		Integer count = upTrendCounts.get(stream);
		if (count == null) {
			count = 0;
		}
		count++;
		upTrendCounts.put(stream, count);
		
	}
	
	protected Integer getUpTrendCount(String stream) {
		Integer count = upTrendCounts.get(stream);
		if (count == null) {
			count = 0;
			upTrendCounts.put(stream, count);
		}
		return count;		
	}

	protected int getTrend(String stream) {
		Integer trend = trends.get(stream);
		if (trend == null) {
			trend = 0;
			trends.put(stream,trend);
		}
		return trend;
	}

	protected synchronized void addComponentInput(String stream, String component) {
		HashSet<String> names = componentsFromStream(stream);
		if (names.add(component)) {
			setGraph(null);
		}
	}

	protected HashSet<String> componentsFromStream(String stream) {
		HashSet<String> names = streams.get(stream);
		if (names==null) {
			names = new HashSet<String>();
			streams.put(stream, names);
		}
		return names;
	}

	protected synchronized void addComponentOutput(String component, String stream) {
		HashSet<String> names = streamsFromComponent(component);
		if (names.add(stream)) {
			setGraph(null);
		}
	}

	protected HashSet<String> streamsFromComponent(String component) {
		HashSet<String> names = components.get(component);
		if (names==null) {
			names = new HashSet<String>();
			components.put(component, names);
		}
		return names;
	}

	protected HashSet<String> streamsToComponent(String component) {
		HashSet<String> names = new HashSet<String>();
		for (String stream: streams.keySet()) {
			if (componentsFromStream(stream).contains(component)) {
				names.add(stream);
			}
		}
		return names;
	}

	protected long getLatest(String stream) {
		Long latest = latestBacklogs.get(stream);
		if (latest == null) {
			latest = 0L;
		}
		return latest;
	}

	protected void setLatest(String stream, Long backlog) {
		latestBacklogs.put(stream,backlog);
	}

	protected Graph getGraph() {
		if (graph==null) {
			graph = new Graph(components, streams);
		}
		return graph;
	}

	private void setGraph(Graph graph) {
		this.graph = graph;
	}

	protected JSONObject getGraphAsJson() {
		return getGraph().asJson();
	}

}
