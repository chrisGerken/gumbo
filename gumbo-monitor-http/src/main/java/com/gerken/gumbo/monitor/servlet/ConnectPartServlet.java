package com.gerken.gumbo.monitor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gerken.gumbo.monitor.contract.cargo.TopologyConnectPart;
import com.gerken.gumbo.monitor.server.MetricsHistoryManager;

@WebServlet( urlPatterns = {"/topologyConnectPart/*"} )
public class ConnectPartServlet extends GumboServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {

			TopologyConnectPart cargo = new TopologyConnectPart(bytesFrom(req));
			
			MetricsHistoryManager.getHistory(cargo.getHistoryId()).topologyConnect(cargo.getFromNode(), cargo.getEdgeName());
			
			respond(resp, "", 200);
			
		} catch (Exception e) {
			respond(resp, e.toString(), 500);
		}
	}

}
