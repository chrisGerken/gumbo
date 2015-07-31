package com.gerken.gumbo.monitor.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gerken.gumbo.monitor.contract.IMetricsHistory;
import com.gerken.gumbo.monitor.contract.MetricSnapshot;
import com.gerken.gumbo.monitor.contract.cargo.Update;
import com.gerken.gumbo.monitor.server.MetricsHistoryManager;

@WebServlet( urlPatterns = {"/update/*"} )
public class UpdateServlet extends GumboServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {

			Update cargo = new Update(bytesFrom(req));
			
			IMetricsHistory history = MetricsHistoryManager.getHistory(cargo.getHistoryId());
			ArrayList<MetricSnapshot> snapshots = cargo.getSnapshots();
			history.update(snapshots);
			
			respond(resp, "", 200);
			
		} catch (Exception e) {
			respond(resp, e.toString(), 500);
		}
	}

}
