package com.gerken.gumbo.stress;

import java.util.ArrayList;
import java.util.Map;

import com.gerken.gumbo.monitor.client.MonitorClient;
import com.gerken.gumbo.monitor.contract.MetricsHistoryFactory;

/**
 * Represents a gumbo client.  Will connect once to the gumbo monitor
 * and will manage the returned mclient.
 * 
 * @author chrisgerken
 *
 */
public class StressTask {

	private int			  	taskId;
	private boolean			active = false;
	private MonitorClient 	mclient;
	private ArrayList<StressGenerator> gens = new ArrayList<StressGenerator>();
	private long 			start;
	
	public StressTask(int taskId) {
		this.taskId = taskId;
	}
	
	public void activate(Map conf) throws Exception {
		active = true;
		mclient = MonitorClient.connect(conf);
		Object val = conf.get(MetricsHistoryFactory.PROPERTY_START);
		if (val instanceof String) {
			start = Long.parseLong((String)val);
		} else {
			start = (Long) val;
		}
	}

	public void connect(String fromNode, String edgeName, String toNode) {
		// connect <task> fromComponent stream toComponent
		if (!active) { return; }
		mclient.topologyConnect(fromNode, edgeName, toNode);
		mclient.topologyConnect(fromNode, edgeName);
	}

	public void declare(String metricGroup, String metric, int task, String taskGroup) {
		// declare <task> metricGroup  metric  task  toComponent  
		if (!active) { return; }
		mclient.declare(metricGroup, metric, task, taskGroup);
	}

	public int getTaskId() {
		return taskId;
	}

	public void setColor(String metric, String rgb) {
		// color <task> metric r,g,b
		if (!active) { return; }
		mclient.setColor(metric, rgb);
	}

	public void increment(String every, String increment, String metricGroup, String metric) {
		// increment <task> every amount  metricGroup  metric		
		if (!active) { return; }
		StressGenerator gen = new StressGenerator(start, every, increment, metricGroup, metric, taskId);
		gens.add(gen);
	}

	public long getStart() {
		return start;
	}

	public void generate(long now) {
		if (!active) { return; }
		for (StressGenerator gen: gens) {
			gen.exec(now, mclient);
		}
	}

	public void increment(String every, String increment, String metricGroup, String metric, String[] to) {
		// increments <task> every amount  metricGroup  metric task[]
		if (!active) { return; }
		ArrayList<Integer> destinationTasks = new ArrayList<Integer>();
		for (String buf: to) {
			destinationTasks.add(Integer.parseInt(buf));
		}
		StressGenerator gen = new StressGenerator(start, every, increment, metricGroup, metric, destinationTasks);
		gens.add(gen);
	}
	
}
