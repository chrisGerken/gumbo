package com.gerken.gumbo.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class MetricsHistory {

	private long latestBucket = 0L;
	private int maxCount = 100;
	private long start;
	private long bucketSize;

	private HashMap<String, String> colors = new HashMap<String, String>();
	private ArrayList<String> availableColors = colorStrings();
	private ArrayList<MetricSnaphot> recentData = new ArrayList<MetricSnaphot>();
	private HashMap<String, HashMap<String, HashSet<Integer>>> schema = new HashMap<String, HashMap<String,HashSet<Integer>>>();
	
	private HashMap<String, Graph> graphs = new HashMap<String, Graph>();

		// bucket -> metricGroup -> metric -> task -> value
	private HashMap<Long, HashMap<String, HashMap<String, HashMap<Integer, Long>>>> history = new HashMap<Long, HashMap<String, HashMap<String, HashMap<Integer, Long>>>>();

	private MonitorServer monitorServer;
	
	public MetricsHistory(Long start, Long bucketSize, int port) {
		this.start = start;
		this.bucketSize = bucketSize;
		monitorServer = new MonitorServer(port,this);
	}

	public void stop() {
		monitorServer.stop();
	}
	
	private void updateUsing(MetricSnaphot snapshot) {

		String metricGroup = snapshot.getMetricGroup();
		String metric = snapshot.getMetric();
		Integer task = snapshot.getTask();
		Long bucket = snapshot.getBucket();
		Long increment = snapshot.getValue();
		updateUsing(metricGroup, metric, task, bucket, increment);
		
	}
	
	private Long retrieve(String metricGroup, String metric, Integer task, Long bucket) {
		return updateUsing(metricGroup, metric, task, bucket, null);
	}
	
	private Long updateUsing(String metricGroup, String metric, Integer task, Long bucket, Long increment) {

		if (!colors.containsKey(metric)) {
			colors.put(metric, availableColors.remove(0));
		}
		HashMap<String, HashMap<String, HashMap<Integer, Long>>> bucketSlice = history.get(bucket);
		if (bucketSlice == null) {
			bucketSlice = new HashMap<String, HashMap<String, HashMap<Integer, Long>>>();
			history.put(bucket,bucketSlice);
		}
		
		HashMap<String, HashMap<Integer, Long>> metricGroupSlice = bucketSlice.get(metricGroup);
		if (metricGroupSlice == null) {
			metricGroupSlice = new HashMap<String, HashMap<Integer, Long>>();
			bucketSlice.put(metricGroup, metricGroupSlice);
		}

		HashMap<Integer, Long> metricSlice = metricGroupSlice.get(metric);
		if (metricSlice == null) {
			metricSlice = new HashMap<Integer, Long>();
			metricGroupSlice.put(metric, metricSlice);
		}
		
		Long taskValue = metricSlice.get(task);
		if (taskValue == null) {
			taskValue = 0L;
		}
		
		if (increment != null) {
			taskValue = taskValue + increment;
			metricSlice.put(task, taskValue);
			
			if (bucket > latestBucket) {
				latestBucket = bucket;
			}
		}
		
		return taskValue;
	}

	public JSONObject getJson() throws JSONException {
		
		ArrayList<MetricSnaphot> pending = null;

		pending = recentData;
		recentData = new ArrayList<MetricSnaphot>();

		Long latestBucketWithData = latestBucket - 1;
		if (latestBucketWithData < 0) {
			latestBucketWithData = 0L;
		}
		if (latestBucketWithData > (maxCount-1)) {
			latestBucketWithData = (long)(maxCount - 1);
		}
		advanceLatestTimeToPresent();

		for (int i = 0; i < pending.size(); i++) {
			updateUsing(pending.get(i));
		}
		
		ArrayList<Long> toBeRemoved = new ArrayList<Long>();
		for (Long bucket : history.keySet()) {
			if (bucket < (latestBucket - maxCount - 10)) {
				toBeRemoved.add(bucket);
			}
		}
		for (Long bucket : toBeRemoved) {
			history.remove(bucket);
		}
		
//		{ 
//			labels : [ ] ,
//          latestOffsetWithData: 99,
//			metricGroups: [
//			       { 
//			    	   metricGroup: "" ,
//			    	   metrics: [1
//			    	       { 
//			    	    	   metric: "",
//			    	    	   color:  "120,120,120",
//			    	    	   values: [ .... ] ,
//                             max: 14,		
//			    	    	   tasks: [
//			    	    	      {
//			    	    	          task : 1,
//			    	    	          values: [ .... ] ,
//                                    max: 107		
//			    	    	      }
//			    	    	   ]
//			    	       }
//			    	   ]
//			       }
//			               
//			   ]
//		}
		
		JSONObject wrapper = new JSONObject();
	
		JSONArray jarr = new JSONArray();
		for (int i = Math.max(0,(int)latestBucket-maxCount); i < latestBucket; i++) {
			if ((i%20)==0) {
				jarr.put(String.valueOf(i));
			} else {
				jarr.put("");
			}
		}
		for (int i = (int)latestBucket; i < maxCount; i++) {
			jarr.put("");
		}
		wrapper.put("labels", jarr);
		wrapper.put("latestOffsetWithData", latestBucketWithData);
		
		JSONArray jarr1 = new JSONArray();   // array of metric groups
		
		for (String metricGroup : schema.keySet()) {
			
			JSONObject jMetricGroup = new JSONObject();
			
			jMetricGroup.put("metricGroup", metricGroup);

			JSONArray jarr2 = new JSONArray();   // array of metrics

			for (String metric : schema.get(metricGroup).keySet()) {
				
				JSONObject jMetric = new JSONObject();
				jMetric.put("metric", metric);
				jMetric.put("color", colors.get(metric));

				Long[] sums = new Long[maxCount];
				for (int i=0; i < maxCount; i++) { 
					sums[i] = 0L;
				}

				JSONArray jarr3 = new JSONArray();   // array of tasks
				
				for (Integer task : schema.get(metricGroup).get(metric)) {
					JSONObject jTask = new JSONObject();
					JSONArray jarr4 = new JSONArray();   // array of task values
					
					long maxTaskValue = 0L;
					int offset = 0;
					for (int d = Math.max(0, (int)latestBucket-maxCount); d < latestBucket; d++) {
						Long bucket = (long) d;
						Long value = retrieve(metricGroup, metric, task, bucket);
						jarr4.put(value);
						if (value > maxTaskValue) {
							maxTaskValue = value;
						}
						sums[offset] = sums[offset] + value;
						offset++;
					}
					jTask.put("task", task);
					jTask.put("values",jarr4);
					jTask.put("max", maxTaskValue);

					jarr3.put(jTask);
				}
				
				jMetric.put("tasks", jarr3);

				long maxMetricValue = 0L;
				JSONArray jarr4 = new JSONArray();   // array of task values
				for (int i = 0; i < maxCount; i++) {
					jarr4.put(sums[i]);
					if (sums[i]>maxMetricValue) {
						maxMetricValue = sums[i];
					}
				}
				jMetric.put("values", jarr4);
				jMetric.put("max", maxMetricValue);
								
				jarr2.put(jMetric);
			}
			
			jMetricGroup.put("metrics", jarr2);
			
			jarr1.put(jMetricGroup);
		}

		wrapper.put("metricGroups", jarr1);
		
		return wrapper;
	}
	
	private void advanceLatestTimeToPresent() {
		long now = (System.currentTimeMillis() - start) / bucketSize;
		if (now > latestBucket) {
			latestBucket = now;
		};
		latestBucket--;
	}

	private ArrayList<String> colorStrings() {
		HashMap<Integer,ArrayList<String>> map = new HashMap<Integer,ArrayList<String>>();
		
		int max = 0;
		int inc = 60;
		for (int r = 0; r <= 240; r=r+inc) {
			for (int b = 0; b <= 240; b = b+inc) {
				for (int g = 0; g <= 240; g=g+inc) {
					int total = r + b + g;
					if ((total>inc*3)&(total<inc*9)) {
						ArrayList<String> list = map.get(total);
						if (list == null) {
							list = new ArrayList<String>();
							map.put(total,list);
						}
						list.add(String.valueOf(r)+","+String.valueOf(g)+","+String.valueOf(b));
						if (max < total) { max = total; }
					}
				}
			}
		}
		
		ArrayList<String> result = new ArrayList<String>();
		for (int i = max; i > 0; i=i-inc) {
			if (map.containsKey(i)) {
				for (String str : map.get(i)) {
					result.add(str);
				}
			}
		}
		
		result = repulse(result);
		
		return result;
	}

	private ArrayList<String> repulse(ArrayList<String> colors) {
		ArrayList<String> result = new ArrayList<String>();
		String from = colors.remove(0);
		result.add(from);
		while (!colors.isEmpty()) {
			String farthest = null;
			double max = -1.0;
			for (String to : colors) {
				double distance = distanceBetween(from,to);
				if (distance > max) {
					max = distance;
					farthest = to;
				}
			}
			colors.remove(farthest);
			from = farthest;
			result.add(from);
		}
		return result;
	}

	private double distanceBetween(String from, String to) {
		StringTokenizer st = new StringTokenizer(from, ",");
		double x1 = Double.parseDouble(st.nextToken());
		double y1 = Double.parseDouble(st.nextToken());
		double z1 = Double.parseDouble(st.nextToken());
		st = new StringTokenizer(to, ",");
		double x2 = Double.parseDouble(st.nextToken());
		double y2 = Double.parseDouble(st.nextToken());
		double z2 = Double.parseDouble(st.nextToken());

		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z1-z2)*(z1-z2));
	}

	public void declare(String metricGroup, String metric, Integer task) {
		HashMap<String, HashSet<Integer>> metrics = schema.get(metricGroup);
		if (metrics==null) {
			metrics = new HashMap<String, HashSet<Integer>>();
			schema.put(metricGroup, metrics);
		}
		HashSet<Integer> tasks = metrics.get(metric);
		if (tasks == null) {
			tasks = new HashSet<Integer>();
			metrics.put(metric, tasks);
		}
		tasks.add(task);
	}

	public void graphConnect(String graphName, String fromNode, String edgeName, String toNode) {
		Graph graph = graphs.get(graphName);
		if (graph == null) {
			graph = new Graph(graphName);
			graphs.put(graphName, graph);
		}
		
		// Still under construction... 
		
//		graph.connect(fromNode, toNode, edgeName);
	}

	public void graphConnect(String graphName, String fromNode, String edgeName) {
		// TODO Auto-generated method stub
		
	}

	public void update(ArrayList<MetricSnaphot> snapshots) {
		recentData.addAll(snapshots);
	}

}
