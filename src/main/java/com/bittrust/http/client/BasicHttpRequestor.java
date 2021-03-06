/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.client;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;

import com.bittrust.http.PhaaasContext;


/**
 * @class BasicHttpRequestor
 */
public class BasicHttpRequestor implements HttpRequestor {
	
	private DefaultHttpClient client;
	private ThreadSafeClientConnManager connManager;
	private final HttpHost httpHost;
	
	public BasicHttpRequestor(String host) {
		HttpParams params = new SyncBasicHttpParams();
		SchemeRegistry registry = new SchemeRegistry();
		
		// setup the host for this requestor
		if(host.contains(":")) {
			int i = host.indexOf(":");
			httpHost = new HttpHost(host.substring(0, i), Integer.parseInt(host.substring(i+1)));
		} else {
			httpHost = new HttpHost(host);
		}
		
		// this should be specified in the config
		params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 10);
		params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		
		this.connManager = new ThreadSafeClientConnManager(params, registry);
		this.client = new DefaultHttpClient(this.connManager, params);
	}

	public void request(PhaaasContext context) {
		HttpRequest request = context.getHttpRequest();
		
		// set the hostname
		request.setHeader("Host", httpHost.getHostName());
		
		try {
			context.setHttpResponse(client.execute(httpHost, request, context));
		} catch (ClientProtocolException e) {
			System.err.println("PROTOCOL ERROR: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
