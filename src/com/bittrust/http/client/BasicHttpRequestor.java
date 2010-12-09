/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.client;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HttpContext;


/**
 * @class BasicHttpRequestor
 */
public class BasicHttpRequestor implements HttpRequestor {
	
	private DefaultHttpClient client;
	private ThreadSafeClientConnManager connManager;
	
	public BasicHttpRequestor() {
		HttpParams params = new SyncBasicHttpParams();
		SchemeRegistry registry = new SchemeRegistry();
		
		// this should be specified in the config
		params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 10);
		params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 443));
		
		this.connManager = new ThreadSafeClientConnManager(params, registry);
		this.client = new DefaultHttpClient(this.connManager, params);
	}

	@Override
	public HttpResponse request(HttpRequest request, HttpContext context) {
		String host = request.getFirstHeader("Host").getValue();
		HttpHost httpHost = new HttpHost(host);
		HttpResponse response = null;
		
		try {
			response = client.execute(httpHost, request, context);
			
		} catch (ClientProtocolException e) {
			System.err.println("REQUEST: " + request.getRequestLine());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response;
	}

}
