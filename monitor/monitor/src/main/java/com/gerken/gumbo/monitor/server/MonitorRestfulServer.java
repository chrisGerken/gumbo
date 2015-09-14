package com.gerken.gumbo.monitor.server;

import javax.servlet.Servlet;

import org.eclipse.jetty.http.HttpParser.HttpHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.Holder.Source;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class MonitorRestfulServer {

	private Server server;
	private MetricsHistoryHandler metricsHandler;
	
	public MonitorRestfulServer(int port, MetricsHistory history) {
		start(port,history);
	}

	private void start(int port, MetricsHistory history) {
        try {
        	System.out.println("Starting monitoring server on port "+port);
			server = new Server(port);
 
			HandlerList handlers = new HandlerList();
			metricsHandler = new MetricsHistoryHandler(history);
			
//			ServletContextHandler sch = new ServletContextHandler();
//			server.setHandler(sch);
//			sch.setContextPath("/metrics");
//			Servlet aServlet = null;
//			sch.addServlet(new ServletHolder(aServlet), "/wakawaka");		
			
			handlers.setHandlers(new Handler[] { metricsHandler, new DefaultHandler() });
			server.setHandler(handlers);
 
			server.start();
		} catch (Throwable e) {
			e.printStackTrace();
//			System.out.println(e.toString());
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
