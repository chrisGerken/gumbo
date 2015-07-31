package com.gerken.gumbo.monitor.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.gerken.gumbo.monitor.contract.IMetricsHistory;


public abstract class GumboServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected JSONObject contentFrom(HttpServletRequest req) throws IOException, JSONException {
		
		byte bytes[] = read(req.getInputStream());
		JSONObject jobj = new JSONObject(new String(bytes));
		return jobj;
	}

	protected byte[] bytesFrom(HttpServletRequest req) throws IOException, JSONException {
		
		byte bytes[] = read(req.getInputStream());
		return bytes;

	}

	protected String stringFrom(HttpServletRequest req) throws IOException, JSONException {
		
		return new String(bytesFrom(req));

	}
	
	protected String[] pathInfoSegmentsFrom(HttpServletRequest req) {
		String info = req.getPathInfo();
		if (info == null) { return new String[0]; }
		StringTokenizer st = new StringTokenizer(info,"/");
		String result[] = new String[st.countTokens()];
		for (int i = 0; i < result.length; i++) {
			result[i] = st.nextToken();
		}
		return result;
	}

	protected Map<String, String> parse(String queryString) {
		if (queryString==null) { queryString = ""; }
		StringTokenizer st = new StringTokenizer(queryString, "&");
		HashMap<String, String> results = new HashMap<String, String>();
		while (st.hasMoreTokens()) {
			String part = st.nextToken();
			int index = part.indexOf('=');
			if (index < 0) {
				results.put(part.toLowerCase(), "");
			} else {
				String key = part.substring(0, index).toLowerCase();
				String value = part.substring(index+1);
				value = URLDecoder.decode(value);
				results.put(key, value);
			}
		}
		return results;
	}
	
	protected void respond(HttpServletResponse resp, JSONObject jobj) throws IOException {
		respond(resp, jobj.toString(), 200);
	}
	
	protected void respond(HttpServletResponse resp, JSONArray jarr) throws IOException {
		respond(resp, jarr.toString(), 200);
	}
	
	protected void respond(HttpServletResponse resp, String content, int status) throws IOException {
		OutputStream os = resp.getOutputStream();
		os.write(content.getBytes());
		os.flush();
		os.close();
		resp.setStatus(status);
	}
	
	protected void respondStatusRefresh(HttpServletResponse resp, JSONObject status, int interval, boolean refresh) throws IOException {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("<html>");

		if (refresh) {
			sb.append("<head><meta http-equiv=\"refresh\" content=\""+interval+"\"></head>");
		}
		
		sb.append("<body><pre>");
		
		try {
			sb.append(status.toString(4));
		} catch (JSONException e) {
			sb.append(e.toString());
		}

		sb.append("</pre></body></html>");
		String content = sb.toString();
		
		respond(resp, content, 200);
	}
	
	protected byte[] read(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte b[] = new byte[4000];
		int len = is.read(b);
		while (len > -1) {
			baos.write(b, 0, len);
			len = is.read(b);
		}
		is.close();
		return baos.toByteArray();
	}

}
