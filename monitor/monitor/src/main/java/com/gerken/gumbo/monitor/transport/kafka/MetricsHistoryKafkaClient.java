package com.gerken.gumbo.monitor.transport.kafka;

import java.util.ArrayList;

import com.gerken.gumbo.monitor.contract.IMetricsHistory;
import com.gerken.gumbo.monitor.contract.MetricSnaphot;

public class MetricsHistoryKafkaClient implements IMetricsHistory {

	private String kafkaBrokers;
	
	public MetricsHistoryKafkaClient(String kafkaBrokers) {
		this.kafkaBrokers = kafkaBrokers;
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
