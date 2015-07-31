package com.gerken.gumbo.monitor.client.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.gerken.gumbo.monitor.contract.IMetricsHistory;
import com.gerken.gumbo.monitor.contract.MetricSnapshot;
import com.gerken.gumbo.monitor.contract.MetricsHistoryFactory;
import com.gerken.gumbo.monitor.contract.cargo.DeclareMetric;
import com.gerken.gumbo.monitor.contract.cargo.GumboCargo;
import com.gerken.gumbo.monitor.contract.cargo.InitHttp;
import com.gerken.gumbo.monitor.contract.cargo.SetColor;
import com.gerken.gumbo.monitor.contract.cargo.Snapshot;
import com.gerken.gumbo.monitor.contract.cargo.Stop;
import com.gerken.gumbo.monitor.contract.cargo.TopologyConnectFull;
import com.gerken.gumbo.monitor.contract.cargo.TopologyConnectPart;
import com.gerken.gumbo.monitor.contract.cargo.Update;

public class MetricsHistoryHttpClient implements IMetricsHistory {
	
	private String host;
	private int port;
	private String app;
	private String dest;
	private HttpTransportClient client;
	private Long historyId;

	public void init(Map config) {
		port = MetricsHistoryFactory.getInteger(MetricsHistoryFactory.PROPERTY_HTTP_PORT, config, 0);
		host = MetricsHistoryFactory.getString(MetricsHistoryFactory.PROPERTY_HTTP_HOST, config, "");
		app = MetricsHistoryFactory.getString(MetricsHistoryFactory.PROPERTY_HTTP_APP, config, "");
		Long start = MetricsHistoryFactory.getLong(MetricsHistoryFactory.PROPERTY_START, config, 0L);
		Long bucketSize = MetricsHistoryFactory.getLong(MetricsHistoryFactory.PROPERTY_BUCKET_SIZE, config, 0L);
		historyId = start;
		
		dest = "http://" + host + ":" + port + "/" + app;
		
		InitHttp cargo = new InitHttp(historyId, start, bucketSize, host, port);
		post("init",cargo);

	}

	public void declare(String metricGroup, String metric, Integer task) {

		DeclareMetric cargo = new DeclareMetric(historyId, metricGroup, metric, task);
		post("declare",cargo);

	}

	public void topologyConnect(String fromNode, String edgeName, String toNode) {
		TopologyConnectFull cargo = new TopologyConnectFull(historyId, fromNode, edgeName, toNode);
		post("topologyConnectFull", cargo);

	}

	public void topologyConnect(String fromNode, String edgeName) {
		TopologyConnectPart cargo = new TopologyConnectPart(historyId, fromNode, edgeName);
		post("topologyConnectPart", cargo);

	}

	public void stop() {

		Stop cargo = new Stop(historyId);
		post("stop", cargo);

	}

	public void update(ArrayList<MetricSnapshot> snapshots) {

		MetricSnapshot[] s = new MetricSnapshot[snapshots.size()];
		snapshots.toArray(s);
		Update cargo = new Update(historyId, s);
		post("update", cargo);

	}

	public void setColor(String metric, String color) {

		SetColor cargo = new SetColor(historyId, metric, color);
		post("setColor", cargo);

	}

	public JSONObject getJson() throws JSONException {
		return null;
	}

	private void post(String action, GumboCargo cargo) {
		try {
			client.doPost(dest+"/"+action, cargo.asBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(long start, long bucketSize) {
		// TODO Auto-generated method stub
		
	}


}
