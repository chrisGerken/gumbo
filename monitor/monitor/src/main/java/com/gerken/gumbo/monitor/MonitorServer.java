package com.gerken.gumbo.monitor;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;

public class MonitorServer {

	private Server server;
	private MetricsHistoryHandler metricsHandler;
	
	public MonitorServer(int port, MetricsHistory history) {
		start(port,history);
	}

	private void start(int port, MetricsHistory history) {
        try {
        	System.out.println("Starting monitoring server");
			server = new Server(port);
 
			HandlerList handlers = new HandlerList();
			metricsHandler = new MetricsHistoryHandler(history);
			handlers.setHandlers(new Handler[] { metricsHandler, new DefaultHandler() });
			server.setHandler(handlers);
 
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public  void stop() {
        try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
