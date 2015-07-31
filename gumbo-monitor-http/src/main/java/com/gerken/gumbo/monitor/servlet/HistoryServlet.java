package com.gerken.gumbo.monitor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONObject;

import com.gerken.gumbo.monitor.server.MetricsHistoryManager;

@WebServlet( urlPatterns = {"/history/*"} )
public class HistoryServlet extends GumboServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			
			JSONObject history = MetricsHistoryManager.getLast().getJson();
			
			respond(resp, history.toString(), 200);
			
		} catch (Exception e) {
			respond(resp, e.toString(), 500);
		}
	}

}
