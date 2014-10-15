gumbo
=====

A performance monitor for Storm topologies.  Consists of:

1) A TaskHook that extracts topology information and manages local metrics

2) A simple HTTP server that can report the last 100 seconds of gathered metrics

3) A set of configurable charts that understand the metrics schema and which can help identify performance problems in the monitored topology:

  - A histogram that shows pending tuple counts (e.g. the backlog) on each stream in the topology
  - A "Share of Voice" chart that shows the pending tuple counts for a given stream for each of a component's tasks
  - A "Share of Voice" chart that shows the relative size of the backlogs on all streams for the topology


So for this ![topology](https://github.com/chrisGerken/gumbo/blob/master/monitor/monitor/src/main/resources/IngesterTopology.png) you might see this ![dashboard](https://github.com/chrisGerken/gumbo/blob/master/monitor/monitor/src/main/resources/Gumbo.png)


Code
===
<pre>
HashMap map = new HashMap();
map.put("storm.monitor.host", a_String_IP_address);
map.put("storm.monitor.port", an_Integer_Port_Number);
map.put("storm.monitor.start", System.currentTimeMillis());  // should be the same for all calls
map.put("storm.monitor.bucketsize", 1000L);

MonitorClient mclient = MonitorClient.forConfig(conf);

   ...

//  There are multiple metric groups, each with multiple metrics.
//  Components have names and multiple instances, each of which has an integer ID
   
mclient.declare(metricGroup,metric,task_id,component_id);

   ...   
   
mclient.increment(metricGroup,metric, 1L , task_id);
</pre>


  
