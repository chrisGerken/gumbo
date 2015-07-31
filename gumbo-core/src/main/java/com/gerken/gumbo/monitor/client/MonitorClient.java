package com.gerken.gumbo.monitor.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.gerken.gumbo.monitor.contract.IMetricsHistory;
import com.gerken.gumbo.monitor.contract.MetricSnapshot;
import com.gerken.gumbo.monitor.contract.MetricsHistoryFactory;

/**
 * MonitorClient represents an instrumentation helper that is able to communicate with a configured
 * monitor server.  Application code which produces gumbo metrics uses this class to persist and 
 * transmit metrics.
 * 
 * @author chrisgerken
 *
 */
public class MonitorClient {

	private Long   	start;
	private Long   	bucketSize;
	private int     users = 0;
	
	private Long lastBucket = null;
	
	private HashSet<TaskKey> taskKeys = new HashSet<TaskKey>();
	private HashMap<Integer, String> taskMeta = new HashMap<Integer, String>();
	
	private static IMetricsHistory history;
	private static HashMap<String, MonitorClient> clients = new HashMap<String, MonitorClient>();
	private HashMap<TaskKey, AtomicLong> currentSums = new HashMap<TaskKey, AtomicLong>();
	
	private MonitorClient(Map config) throws Exception {
		if (history==null) {
			history = MetricsHistoryFactory.connect(config);
		}
		this.start = MetricsHistoryFactory.getStart(config);
		this.bucketSize = MetricsHistoryFactory.getBucketSize(config);
	}

	/**
	 * Maintain a set of MonitorClients and return the appropriate client instance.
	 * 
	 * @param conf a Map containing configuration information, usually from a properties file.
	 * @return the appropriate MonitorClient instance
	 * @throws Exception
	 */
	public synchronized static MonitorClient connect(Map conf) throws Exception {
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

	/**
	 * Increment the given metric in the given metricGroup a given amount for each of a 
	 * given number of tasks.  Often used to increment the pending tuple count for streams
	 * destined for specific tasks.
	 * @param metricGroup the name of the group of metrics, e.g. "Backlog"
	 * @param metric the name of the metric, e.g. the stream name
	 * @param increase the amount to increase each count
	 * @param ids the destination tasks for which the pending tuple count should be incremented
	 */
	public void increment(String metricGroup, String metric, Long increase, Collection<Integer> ids) {
		Long bucket = getCurrentBucket();
//		ArrayList<MetricSnaphot> snapshots = new ArrayList<MetricSnaphot>();
		for (Integer id : ids) {
			increment(metricGroup, metric, bucket, increase, id);
		}
	}

	/**
	 * Increment the given metric in the given metric group the given amount for a single task.  
	 * Often used to decrement the pending tuple count for a stream on bolt execute
	 * @param metricGroup the name of the metric group
	 * @param metric the name of the metric
	 * @param increase the amount to increase
	 * @param id the specific task
	 */
	public void increment(String metricGroup, String metric, Long increase, Integer id) {
		Long bucket = getCurrentBucket();
		increment(metricGroup, metric, bucket, increase, id);
	}

	/**
	 * Increment the value of the given metric in the given metric group for the given time
	 * interval.
	 * @param metricGroup the name of the metric group
	 * @param metric the name of the metric
	 * @param bucket the time interval for which the metric is to be incremented
	 * @param increase the amount of the increase
	 * @param task the specific task
	 */
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
		ArrayList<MetricSnapshot> snapshots = new ArrayList<MetricSnapshot>();
		for (TaskKey key : currentSums.keySet()) {
			snapshots.add(new MetricSnapshot(key.getMetricGroup(), key.getMetric(), key.getTask(), lastBucket, currentSums.get(key).get()));
		}
		history.update(snapshots);
	}

	private Long getCurrentBucket() {
		return (System.currentTimeMillis() - start) / bucketSize;
	}
	
	/**
	 * Declare the name of the metric and metric group for a particular task.
	 * @param metricGroup the name of the metric group
	 * @param metric the name of the metric
	 * @param task the task number
	 */
	public void declare(String metricGroup, String metric, int task) {
		declare(metricGroup, metric, task, "default");
	}

	/**
	 * Declare the name of the metric, metric group and task group for a particular task
	 * @param metricGroup the name of the metric group
	 * @param metric the name of the metric
	 * @param task the task number
	 * @param taskGroup the name of the task group, usually a component name for Storm
	 */
	public void declare(String metricGroup, String metric, int task, String taskGroup) {
		taskKeys.add(new TaskKey(metricGroup,metric,task));
		history.declare(metricGroup, metric, task);
		taskMeta.put(task, taskGroup);
	}

	/**
	 * Specify that two nodes are connected by an edge
	 * @param fromNode the name of the from node
	 * @param edgeName the name of the edge
	 * @param toNode the name of the to node
	 */
	public void topologyConnect(String fromNode, String edgeName, String toNode) {
		history.topologyConnect(fromNode, edgeName, toNode);
	}

	/**
	 * Specify that an edge leaves from a specific node
	 * @param fromNode the name of the from node
	 * @param edgeName the name of the edge
	 */
	public void topologyConnect(String fromNode, String edgeName) {
		history.topologyConnect(fromNode, edgeName);
	}

	/**
	 * Increment the number of users of this instance of MonitorClient
	 */
	public void addUser() {
		users++;
	}
	
	/**
	 * Decrement the number of users of this instance of MonitorClient.
	 * If there are no more active users of this instance then send a stop 
	 * request on behalf of this client to the monitor server.
	 */
	public void cleanup() {
		users--;
		if (users == 0) {
			history.stop();
		}
	}

	/**
	 * Specify the color with which to display the given metric on the gumbo dashboard.
	 * @param metric the name of the metric
	 * @param color the color, in r,g,b format with each number being between 0 and 255 inclusive
	 */
	public void setColor(String metric, String color) {
		history.setColor(metric, color);
	}

}
