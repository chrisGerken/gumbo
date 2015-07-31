package com.gerken.gumbo.monitor.server.kafka;

import java.util.ArrayList;

import com.gerken.gumbo.monitor.contract.IMetricsHistory;
import com.gerken.gumbo.monitor.contract.MetricSnaphot;
import com.gerken.gumbo.monitor.server.MetricsHistory;

public class MetricsHistoryKafkaServer implements IMetricsHistory {

	MetricsHistory history;
	
	public MetricsHistoryKafkaServer() {

	}

	@Override
	public void declare(String metricGroup, String metric, Integer task) {
		history.declare(metricGroup, metric, task);
	}

	@Override
	public void topologyConnect(String fromNode, String edgeName, String toNode) {
		history.topologyConnect(fromNode, edgeName, toNode);
	}

	@Override
	public void topologyConnect(String fromNode, String edgeName) {
		history.topologyConnect(fromNode, edgeName);
	}

	@Override
	public void restart(Long start, Long bucketSize, int port) {
		if (history == null) {
			history = new MetricsHistory(start, bucketSize, port);
		} else {
			history.restart(start, bucketSize, port);
		}
	}

	@Override
	public void stop() {
		history.stop();
	}

	@Override
	public void update(ArrayList<MetricSnaphot> snapshots) {
		history.update(snapshots);
	}

	@Override
	public void setColor(String metric, String color) {
		history.setColor(metric, color);
	}

}
