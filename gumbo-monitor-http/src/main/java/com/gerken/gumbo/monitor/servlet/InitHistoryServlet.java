package com.gerken.gumbo.monitor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gerken.gumbo.monitor.contract.cargo.InitHttp;
import com.gerken.gumbo.monitor.server.MetricsHistoryManager;

@WebServlet( urlPatterns = {"/init/*"} )
public class InitHistoryServlet extends GumboServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {

			InitHttp cargo = new InitHttp(bytesFrom(req));
			
			MetricsHistoryManager.getHistory(cargo.getHistoryId()).init(cargo.getStart(), cargo.getBucketSize());
			
			respond(resp, "", 200);
			
		} catch (Exception e) {
			respond(resp, e.toString(), 500);
		}
	}

}
