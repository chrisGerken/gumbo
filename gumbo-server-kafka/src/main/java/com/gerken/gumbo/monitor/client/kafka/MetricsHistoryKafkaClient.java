package com.gerken.gumbo.monitor.client.kafka;

import java.util.ArrayList;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.gerken.gumbo.monitor.contract.IMetricsHistory;
import com.gerken.gumbo.monitor.contract.MetricSnapshot;

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
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(ArrayList<MetricSnapshot> snapshots) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColor(String metric, String color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(Map config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JSONObject getJson() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(long start, long bucketSize) {
		// TODO Auto-generated method stub
		
	}

}
