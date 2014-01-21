gumbo
=====

A performance monitor for Storm topologies.  Consists of:

1) A TaskHook that extracts topology information and manages local metrics

2) A simple HTTP server that can report the last 100 seconds of gathered metrics

3) A set of configurable charts that understand the metrics schema and which can help identify performance problems in the monitored topology:

  - A histogram that shows pending tuple counts (e.g. the backlog) on each stream in the topology
  - A "Share of Voice" chart that shows the pending tuple counts for a given stream for each of a component's tasks
  - A "Share of Voice" chart that shows the relative size of the backlogs on all streams for the topology
