package com.gerken.gumbo.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.gerken.gumbo.monitor.contract.IMetricsHistory;
import com.gerken.gumbo.monitor.contract.MetricSnaphot;
import com.gerken.gumbo.monitor.contract.MetricsHistoryFactory;

public class MonitorClient {

	private Long   	start;
	private Long   	bucketSize;
	private int     users = 0;
	
	private Long lastBucket = null;
	
	private HashSet<TaskKey> taskKeys = new HashSet<TaskKey>();
//	private HashMap<Integer, String> taskMeta = new HashMap<Integer, String>();
	
	private static IMetricsHistory history;
	private static HashMap<String, MonitorClient> clients = new HashMap<String, MonitorClient>();
	private HashMap<TaskKey, AtomicLong> currentSums = new HashMap<TaskKey, AtomicLong>();
	
	private MonitorClient(Map config) {
		if (history==null) {
			history = MetricsHistoryFactory.connect(config);
		}
		this.start = MetricsHistoryFactory.getStart(config);
		this.bucketSize = MetricsHistoryFactory.getBucketSize(config);
	}

	public synchronized static MonitorClient connect(Map conf) {
		MonitorClient client = null;
		String key = MetricsHistoryFactory.clientKey(conf);
		if (clients.containsKey(key)) {
			client = clients.get(key);
		} else {
			client = new MonitorClient(conf);
			clients.put(key, client);
		}
		client.addUser();
		return client;
	}	

	public void increment(String metricGroup, String metric, Long increase, Collection<Integer> ids) {
		if (metricGroup==null) { return; }
		if (metric==null) { return; }
		Long bucket = getCurrentBucket();
//		ArrayList<MetricSnaphot> snapshots = new ArrayList<MetricSnaphot>();
		for (Integer id : ids) {
			increment(metricGroup, metric, bucket, increase, id);
		}
	}

	public void increment(String metricGroup, String metric, Long increase, Integer id) {
		if (metricGroup==null) { return; }
		if (metric==null) { return; }
		if (id==null) { return; }
		Long bucket = getCurrentBucket();
		increment(metricGroup, metric, bucket, increase, id);
	}

	public void increment(String metricGroup, String metric, Long bucket, Long increase, Integer task) {
		if (metricGroup==null) { return; }
		if (metric==null) { return; }
		if (task==null) { return; }
	
		TaskKey key = new TaskKey(metricGroup, metric, task);

		if (lastBucket == null) {
			synchronized (this) {
				if (lastBucket == null) {
					lastBucket = bucket;
				}
			}
		} 

		if (lastBucket < bucket) { 
			synchronized (this) {
				if (lastBucket < bucket) {
					send();
					lastBucket = bucket;
					
				}
			}
		} 

		if (!currentSums.containsKey(key)) {
			synchronized (this) {
				if (!currentSums.containsKey(key)) {
					currentSums.put(key, new AtomicLong());
				}
			}
		}
		
		currentSums.get(key).addAndGet(increase);

	}

	private void send() {
		ArrayList<MetricSnaphot> snapshots = new ArrayList<MetricSnaphot>();
		for (TaskKey key : currentSums.keySet()) {
			snapshots.add(new MetricSnaphot(key.getMetricGroup(), key.getMetric(), key.getTask(), lastBucket, currentSums.get(key).get()));
		}
		history.update(snapshots);
	}

	private Long getCurrentBucket() {
		return (System.currentTimeMillis() - start) / bucketSize;
	}
	

	public void declare(String metricGroup, String metric, int task) {
		if (metricGroup==null) { return; }
		if (metric==null) { return; }
		declare(metricGroup, metric, task, "default");
	}

	public void declare(String metricGroup, String metric, int task, String taskGroup) {
		if (metricGroup==null) { return; }
		if (metric==null) { return; }
		TaskKey key = new TaskKey(metricGroup,metric,task);
//		if (taskKeys.contains(key)) { return; }
		taskKeys.add(key);
		history.declare(metricGroup, metric, task);
//		taskMeta.put(task, taskGroup);
	}

	public void topologyConnect(String fromNode, String edgeName, String toNode) {
		history.topologyConnect(fromNode, edgeName, toNode);
	}

	public void topologyConnect(String fromNode, String edgeName) {
		history.topologyConnect(fromNode, edgeName);
	}

	public void addUser() {
		users++;
	}
	
	public void cleanup() {
		users--;
		if (users == 0) {
			history.stop();
		}
	}

	public void setColor(String metric, String color) {
		history.setColor(metric, color);
	}

}
