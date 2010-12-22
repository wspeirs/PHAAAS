/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.client;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/**
 * @interface HttpRequestor
 */
public interface HttpRequestor {

	/**
	 * Makes a request to the given host.
	 * @param request The HTTP request to send to the host.
	 * @param context The context for which the HTTP request should operate in.
	 * @return The response from the server.
	 */
	public HttpResponse request(HttpRequest request, HttpContext context);
}
