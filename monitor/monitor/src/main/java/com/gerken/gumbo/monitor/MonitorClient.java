package com.gerken.gumbo.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MonitorClient implements IMetricsAggregator {

	private Long   	start;
	private Long   	bucketSize;
	private int     users = 0;
	
	private Long lastBucket = null;
	
	private HashSet<TaskKey> taskKeys = new HashSet<TaskKey>();
	private HashMap<Integer, String> taskMeta = new HashMap<Integer, String>();
	
	private static MetricsHistory history;
	private static HashMap<String, MonitorClient> clients = new HashMap<String, MonitorClient>();
	HashMap<TaskKey, AtomicLong> currentSums = new HashMap<TaskKey, AtomicLong>();
	
	private MonitorClient(String host, int port, Long start, Long bucketSize) {
		this.start = start;
		this.bucketSize = bucketSize;
		if (history==null) {
			history = new MetricsHistory(start,bucketSize,port);
		}
	}

	public synchronized static MonitorClient connect(String url, int port, Long start, Long bucketSize) {
		MonitorClient client = null;
		String key = url + port;
		if (clients.containsKey(key)) {
			client = clients.get(key);
		} else {
			client = new MonitorClient(url, port, start, bucketSize);
			clients.put(key, client);
		}
		client.addUser();
		return client;
	}
	
	public static MonitorClient forConfig(Map conf) {
		
		String host = (String) conf.get("storm.monitor.host");
		int port = Integer.parseInt(String.valueOf(conf.get("storm.monitor.port")));
		Long start = (Long) conf.get("storm.monitor.start");
		Long bsize = (Long) conf.get("storm.monitor.bucketSize");
		
		return MonitorClient.connect(host,port,start,bsize);
		
	}

	@Override
	public void increment(String metricGroup, String metric, Long increase, Collection<Integer> ids) {
		Long bucket = getCurrentBucket();
		ArrayList<MetricSnaphot> snapshots = new ArrayList<MetricSnaphot>();
		for (Integer id : ids) {
			increment(metricGroup, metric, bucket, increase, id);
		}
	}

	@Override
	public void increment(String metricGroup, String metric, Long increase, Integer id) {
		Long bucket = getCurrentBucket();
		increment(metricGroup, metric, bucket, increase, id);
	}

	public void increment(String metricGroup, String metric, Long bucket, Long increase, Integer task) {
	
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
		declare(metricGroup, metric, task, "default");
	}

	public void declare(String metricGroup, String metric, int task, String taskGroup) {
		taskKeys.add(new TaskKey(metricGroup,metric,task));
		history.declare(metricGroup, metric, task);
		taskMeta.put(task, taskGroup);
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

	@Override
	public void setColor(String metric, String color) {
		history.setColor(metric, color);
	}

}
