/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.client;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;


/**
 * @class BasicHttpRequestor
 */
public class BasicHttpRequestor implements HttpRequestor {
	
	private DefaultHttpClient client;
	
	public BasicHttpRequestor() {
		this.client = new DefaultHttpClient();
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
