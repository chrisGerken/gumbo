package com.gerken.gumbo.monitor.client.simple;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import com.gerken.gumbo.monitor.contract.IMetricsHistory;
import com.gerken.gumbo.monitor.server.MetricsHistory;


public class MetricsHistoryHandler extends HandlerWrapper {
	
	private IMetricsHistory history;
	
	public MetricsHistoryHandler(IMetricsHistory history) {
		this.history = history;
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		OutputStream os = response.getOutputStream();
		String content = "";
		try {
			content = history.getJson().toString(4);
		} catch (JSONException e) {
			content = e.toString();
		}
		os.write(content.getBytes());
		os.close();
		response.setStatus(200);
	}

}