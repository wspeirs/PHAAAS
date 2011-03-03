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
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;

import com.bittrust.http.HttpUtils;
import com.bittrust.http.PhaaasContext;

/**
 * @class ProxyHttpRequestor
 * Implements a proxy requestor where the host is passed in the request.
 */
public class ProxyHttpRequestor implements HttpRequestor {

	private DefaultHttpClient client;
	private ThreadSafeClientConnManager connManager;
	
	public ProxyHttpRequestor() {
		HttpParams params = new SyncBasicHttpParams();
		SchemeRegistry registry = new SchemeRegistry();
		
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
		String hostName = HttpUtils.getHeader(request, "Host");
		HttpHost httpHost = null;
		
		if(request.getRequestLine().getMethod().equalsIgnoreCase("connect")) {
			BasicHttpRequest newRequest = new BasicHttpRequest("GET", "/");
			
			newRequest.setHeaders(request.getAllHeaders());
			request = newRequest;
			httpHost = new HttpHost(hostName, 443, "https");
		}
		else
			httpHost = new HttpHost(hostName);
		
		// set the hostname
		request.setHeader("Host", httpHost.getHostName());
		
		try {
			long startTime = System.currentTimeMillis();
			context.setHttpResponse(client.execute(httpHost, request, context));
			long endTime = System.currentTimeMillis();
			
			System.out.println("TIME: " + (endTime - startTime) + "ms");
			
		} catch (ClientProtocolException e) {
			System.err.println("PROTOCOL ERROR: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
