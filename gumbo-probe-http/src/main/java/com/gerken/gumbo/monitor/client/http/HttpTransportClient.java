package com.gerken.gumbo.monitor.client.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

public class HttpTransportClient {

	private HashMap<String, String> props = new HashMap<String,String>();
    private String charset   = "UTF-8";
    private String cookiePath = null;
    
    private int httpResponseCode = 0;
    private byte[] response = new byte[0];
    private long lastCall = 0;
    
    private Map<String, String> cookies = new HashMap<String,String>();
	
	public HttpTransportClient() {
		setContentType("text/json");
	}

    public void setAuthentication(String username, String password) {
    	String auth = username + ":" + password;
    	auth = "Basic " +  new String(Base64.encodeBase64(auth.getBytes()));
    	props.put("Authorization", auth);
    }

	public void setContentType(String contentType) {
		props.put("Content-Type", contentType);
	}
	
	public void setCookiepath(String path) {
		cookiePath = path;
	}

	public void setProperty(String name, String value) {
		props.put(name, value);
	}

	public int doPost(String postUrl) throws IOException {
		return doPost(postUrl,new byte[0]);
	}

	public int doPost(String postUrl, byte[] content) throws IOException {

		HttpURLConnection conn = getConnection("POST",postUrl);
		conn.setRequestProperty("Accept-Charset", charset);

		setCookies(conn);
		
        Iterator<String> keys = props.keySet().iterator();
        while (keys.hasNext()) {
        	String key = keys.next();
        	String value = props.get(key);
            conn.setRequestProperty(key,value);
        }
        props = new HashMap<String, String>();

        conn.setDoOutput(true);
		OutputStream os = conn.getOutputStream();
		os.write(content);
		os.flush();
		os.close();
        getResponse(conn);
		keepCookies(conn);
        return httpResponseCode;
	}
	
	public int doPost(String postUrl, Map<String, String> form) throws IOException {
		String content = "";
		String delim = "";
		for (String key : form.keySet()) {
			String value = form.get(key);
			content = content + delim + key + "=" + URLEncoder.encode(value, "UTF-8");
			delim = "&";
		}
		return doPost(postUrl, content.getBytes());
	}

	public int doGet(String getUrl) throws IOException {

		HttpURLConnection conn = getConnection("GET", getUrl);

		setCookies(conn);
		
        Iterator<String> keys = props.keySet().iterator();
        while (keys.hasNext()) {
        	String key = keys.next();
        	String value = props.get(key);
            conn.setRequestProperty(key,value);
        }
        props = new HashMap<String, String>();

		getResponse(conn);
		keepCookies(conn);
        return httpResponseCode;

	}
	
	private void keepCookie(String cookie) {
		String pair = cookie.substring(0, cookie.indexOf(";"));
		String key = pair.substring(0, pair.indexOf("="));
 		cookies.put(key,cookie);
	}

	private void keepCookies(HttpURLConnection conn) {
		
		String headerName=null;
		for (int i=1; (headerName = conn.getHeaderFieldKey(i))!=null; i++) {
		 	if (headerName.equals("Set-Cookie")) {                  
		 		String cookie = conn.getHeaderField(i);               
		 		keepCookie(cookie);
		 	}
		}
		
		if (cookiePath != null) {
			try {
				FileOutputStream fos = new FileOutputStream(cookiePath);
				for (String cookie : getLastCookies()) {
					fos.write((cookie+"\n").getBytes());
				}
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setCookies(HttpURLConnection conn) {
		
		if (cookiePath != null) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(cookiePath)); 
				String cookie = br.readLine();
				while (cookie != null) {
					keepCookie(cookie);
					cookie = br.readLine();
				}
				br.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (cookies.isEmpty()) { return; }
		
		String value = "";
		String delim = "";
		
		for (String cookie : cookies.values()) {
			String pair = cookie.substring(0, cookie.indexOf(";"));
			value = value + delim + pair;
			delim = "; ";
		}

		setProperty("Cookie", value);
	}
	
	public Collection<String> getLastCookies() {
		return cookies.values();
	}

	public byte[] getResponse() {
		return response;
	}

	public String getStringResponse() {
		return new String(getResponse());
	}

	private void getResponse(HttpURLConnection connection) throws IOException {
		
		response = new byte[0];
		InputStream is = null;
		
		is = connection.getInputStream();
		httpResponseCode = connection.getResponseCode();
		if ((200 <= httpResponseCode) && (httpResponseCode <= 299)) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();			
			byte b[] = new byte[32000];
			int read = is.read(b);
			while (read > -1) {
				os.write(b, 0, read);
				read = is.read(b);
			}
			response = os.toByteArray();
		}
        
        try { is.close(); } catch (Throwable t) { }

	}
	
    protected HttpURLConnection getConnection(String verb, String urlString) throws IOException {
    	if (minTime() > 0) {
    		long now = System.currentTimeMillis();
    		long msToWait = lastCall + minTime() - now;
    		if (msToWait > 0) {
    			try { Thread.sleep(msToWait); } catch (InterruptedException e) { }
    		}
    		lastCall = System.currentTimeMillis();
    	}
    	URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(verb);
        connection.setReadTimeout(1000 * 60 * 60);
        connection.setConnectTimeout(1000 * 10);

        return connection;
    }

	protected long minTime() {
		return 0;
	}
	
}
