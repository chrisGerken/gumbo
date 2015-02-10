package com.gerken.gumbo.monitor.transport.http;

import java.util.ArrayList;

import com.gerken.gumbo.monitor.contract.IMetricsHistory;
import com.gerken.gumbo.monitor.contract.MetricSnaphot;

public class MetricsHistoryHttpClient implements IMetricsHistory {

	public MetricsHistoryHttpClient() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void declare(String metricGroup, String metric, Integer task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void topologyConnect(String fromNode, String edgeName, String toNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void topologyConnect(String fromNode, String edgeName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void restart(Long start, Long bucketSize, int port) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(ArrayList<MetricSnaphot> snapshots) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColor(String metric, String color) {
		// TODO Auto-generated method stub

	}

}
