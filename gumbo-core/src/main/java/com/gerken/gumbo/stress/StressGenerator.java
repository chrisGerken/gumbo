package com.gerken.gumbo.stress;

import java.util.ArrayList;

import com.gerken.gumbo.monitor.client.MonitorClient;

public class StressGenerator {
	
	private long start;
	private long every;
	private long sent = 0L;
	private long increment;
	private String metricGroup;
	private String metric;
	private int task;
	private ArrayList<Integer> tasks;
	
	public StressGenerator(long start, String every, String increment, String metricGroup, String metric, int task) {
		this.start = start;
		this.every = Long.parseLong(every);
		this.increment = Long.parseLong(increment);
		this.metricGroup = metricGroup;
		this.metric = metric;
		this.task = task;
		tasks = null;
	}
	
	public StressGenerator(long start, String every, String increment, String metricGroup, String metric, ArrayList<Integer> tasks) {
		this.start = start;
		this.every = Long.parseLong(every);
		this.increment = Long.parseLong(increment);
		this.metricGroup = metricGroup;
		this.metric = metric;
		this.tasks = tasks;
	}

	public long pending(long now) {
		long total = (now - start) / every + 1L;
		long pend = total - sent;
		sent = total;
		return pend;
	}
	
	public long getIncrement() {
		return increment;
	}

	public void exec(long now, MonitorClient mclient) {
		long pending = pending(now);
		for (long l = 0; l < pending; l++) {
			if (tasks == null) {
				mclient.increment(metricGroup, metric, increment, task);
			} else {
				mclient.increment(metricGroup, metric, increment, tasks);
			}
		}
	}

}
