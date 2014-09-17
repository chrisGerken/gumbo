package com.gerken.gumbo.monitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Topology {

	private HashMap<String, HashSet<String>> components = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> streams = new HashMap<String, HashSet<String>>();
	private HashMap<String, Integer> trends = new HashMap<String, Integer>();
	private HashMap<String, Integer> upTrendCounts = new HashMap<String, Integer>();
	private HashMap<String, Long> latestBacklogs = new HashMap<String, Long>();
	
	public Topology() {

	}

	public Set<String> getStreams() {
		return streams.keySet();
	}

	public Set<String> getComponents() {
		return components.keySet();
	}
	
	public void setTrend(String stream, Integer trend) {
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
	
	public Integer getUpTrendCount(String stream) {
		Integer count = upTrendCounts.get(stream);
		if (count == null) {
			count = 0;
			upTrendCounts.put(stream, count);
		}
		return count;		
	}

	public int getTrend(String stream) {
		Integer trend = trends.get(stream);
		if (trend == null) {
			trend = 0;
			trends.put(stream,trend);
		}
		return trend;
	}

	public synchronized void addComponentInput(String stream, String component) {
		HashSet<String> names = componentsFromStream(stream);
		names.add(component);
	}

	public HashSet<String> componentsFromStream(String stream) {
		HashSet<String> names = streams.get(stream);
		if (names==null) {
			names = new HashSet<String>();
			streams.put(stream, names);
		}
		return names;
	}

	public synchronized void addComponentOutput(String component, String stream) {
		HashSet<String> names = streamsFromComponent(component);
		names.add(stream);
	}

	public HashSet<String> streamsFromComponent(String component) {
		HashSet<String> names = components.get(component);
		if (names==null) {
			names = new HashSet<String>();
			components.put(component, names);
		}
		return names;
	}

	public HashSet<String> streamsToComponent(String component) {
		HashSet<String> names = new HashSet<String>();
		for (String stream: streams.keySet()) {
			if (componentsFromStream(stream).contains(component)) {
				names.add(stream);
			}
		}
		return names;
	}

	public long getLatest(String stream) {
		Long latest = latestBacklogs.get(stream);
		if (latest == null) {
			latest = 0L;
		}
		return latest;
	}

	public void setLatest(String stream, Long backlog) {
		latestBacklogs.put(stream,backlog);
	}

}
