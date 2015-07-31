package com.gerken.gumbo.monitor.client.simple;

import java.util.Map;

import com.gerken.gumbo.monitor.contract.IMetricsHistory; 
import com.gerken.gumbo.monitor.contract.MetricsHistoryFactory;
import com.gerken.gumbo.monitor.server.MetricsHistory;

public class LocalMetricsHistory extends MetricsHistory implements IMetricsHistory {

	private MonitorRestfulServer monitorServer;

	public LocalMetricsHistory() {

	}
	
	public void init(Map config) {
		super.init(config);
		int port = MetricsHistoryFactory.getInteger(MetricsHistoryFactory.PROPERTY_LOCAL_PORT, config, 0);
		monitorServer = new MonitorRestfulServer(port,this);
	}

	public void stop() {
		super.stop();
		monitorServer.stop();
	}
	
}
